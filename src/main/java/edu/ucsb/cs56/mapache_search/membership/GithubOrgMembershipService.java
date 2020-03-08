package edu.ucsb.cs56.mapache_search.membership;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import edu.ucsb.cs56.mapache_search.entities.GitHubTeam;
import edu.ucsb.cs56.mapache_search.entities.GitHubIssues;
import edu.ucsb.cs56.mapache_search.entities.GitHubRepos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.github.Github;
import com.jcabi.github.Organization;
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;
import com.jcabi.github.wire.RetryCarefulWire;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;

/**
 * Service object that wraps the UCSB Academic Curriculum API
 */
@Service
public class GithubOrgMembershipService implements MembershipService {

    private final Logger logger = LoggerFactory.getLogger(GithubOrgMembershipService.class);

    // If you put in final, then you can't assign with @Value properly
    private final List<String> adminEmails;

    // @Value("#{'${app.project_repos}'.split(',')}")
    private final List<String> projectRepos;

    @Value("${app.member.hosted-domain}")
    private String memberHostedDomain;

    @Value("${app_github_org}")
    private String githubOrg;

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    public GithubOrgMembershipService(@Value("#{'${app.admin.emails}'.split(',')}") final List<String> adminEmails,
            @Value("#{'${app.project_repos}'.split(',')}") final List<String> projectRepos) {
        logger.info("GoogleHostedDomain=" + memberHostedDomain);
        this.adminEmails = adminEmails;
        this.projectRepos = projectRepos;

        for (int i = 0; i < adminEmails.size(); i++) {
            adminEmails.set(i, adminEmails.get(i).replaceAll("\\s+", ""));
        }

        for (int i = 0; i < projectRepos.size(); i++) {
            projectRepos.set(i, projectRepos.get(i).replaceAll("\\s+", ""));
        }

        logger.info("adminEmails=" + adminEmails.toString());
        logger.info("projectRepos=" + projectRepos.toString());
        logger.info("githubOrg=" + githubOrg);

    }

    /**
     * is current logged in user a member but NOT an admin of the github org
     */
    public boolean isMember(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return hasRole(oAuth2AuthenticationToken, "member");
    }

    /** is current logged in user a member of the github org */
    public boolean isAdmin(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return hasRole(oAuth2AuthenticationToken, "admin");
    }

    /**
     * is current logged in user has role
     * 
     * @param roleToTest "member" or "admin"
     * @param oauthToken oauth token
     * @return if the current logged in user has that role
     */

    public boolean hasRole(final OAuth2AuthenticationToken oauthToken, final String roleToTest) {
        if (oauthToken == null) {
            return false;
        }

        final OAuth2User oAuth2User = oauthToken.getPrincipal();
        final String user = (String) oAuth2User.getAttributes().get("login");

        Github github = null;

        if (clientService == null) {
            logger.error(String.format("unable to obtain autowired clientService"));
            return false;
        }
        final OAuth2AuthorizedClient client = clientService
                .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        if (client == null) {
            logger.info(String.format("clientService was not null but client returned was null for user %s", user));
            return false;
        }

        final OAuth2AccessToken token = client.getAccessToken();

        if (token == null) {
            logger.info(String.format("client for %s was not null but getAccessToken returned null", user));
            return false;
        }
        final String accessToken = token.getTokenValue();
        if (accessToken == null) {
            logger.info(String.format("token was not null but getTokenValue returned null for user %s", user));
            return false;
        }

        try {

            // Check for Admin first
            if (oAuth2User.getAttributes().get("email") != null && roleToTest.equals("admin")
                    && isAdminEmail((String) oAuth2User.getAttributes().get("email"))) {
                logger.info(oAuth2User.getAttributes().get("email") + " is an Admin");
                return true;
            }

            // I forget why we have Github wrapped like this
            // TODO: find the tutorial that explains it
            // I think it has something to do with respecting rate limits
            github = new RtGithub(new RtGithub(accessToken).entry().through(RetryCarefulWire.class, 50));

            // logger.info("github=" + github);
            // User ghuser = github.users().get(user);
            // logger.info("ghuser=" + ghuser);
            // JsonResponse jruser =
            // github.entry().uri().path("/user").back().method(Request.GET).fetch()
            // .as(JsonResponse.class);
            // logger.info("jruser =" + jruser);
            // Organization org = github.organizations().get(githubOrg);
            // logger.info("org =" + org);

            final String path = String.format("/user/memberships/orgs/%s", githubOrg);

            final JsonResponse jr = github.entry().uri().path(path).back().method(Request.GET).fetch()
                    .as(JsonResponse.class);

            logger.info("jr =" + jr);

            final String actualRole = jr.json().readObject().getString("role");
            final String state = jr.json().readObject().getString("state");

            logger.info("actualRole =" + actualRole);
            logger.info("roleToTest =" + roleToTest);
            logger.info("state =" + state);

            return actualRole.equals(roleToTest);

        } catch (final Exception e) {
            logger.error("Exception happened while trying to determine membership in github org");
            logger.error("Exception", e);
        }

        return false;
    }

    private boolean isAdminEmail(final String email) {
        // return (!adminRepository.findByEmail(email).isEmpty() ||
        // (adminEmails.contains(email));
        return (adminEmails.contains(email));
    }

    // get method for repos from application.properties
    public List<String> getRepos() {

        return projectRepos;
    }

    public String getProjectOrg() {

        return githubOrg;
    }

    public List<String> getTeams(final OAuth2AuthenticationToken oauthToken){
        final List<String> teams = new ArrayList<String>();

        //Lots of code reuse with hasRole, could generalize
        if (oauthToken == null) {
            return teams;
        }

        final OAuth2User oAuth2User = oauthToken.getPrincipal();
        final String user = (String) oAuth2User.getAttributes().get("login");

        Github github = null;

        if (clientService == null) {
            logger.error(String.format("unable to obtain autowired clientService"));
            return teams;
        }
        final OAuth2AuthorizedClient client = clientService
                .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        if (client == null) {
            logger.info(String.format("clientService was not null but client returned was null for user %s", user));
            return teams;
        }

        final OAuth2AccessToken token = client.getAccessToken();

        if (token == null) {
            logger.info(String.format("client for %s was not null but getAccessToken returned null", user));
            return teams;
        }
        final String accessToken = token.getTokenValue();
        if (accessToken == null) {
            logger.info(String.format("token was not null but getTokenValue returned null for user %s", user));
            return teams;
        }

        github = new RtGithub(new RtGithub(accessToken).entry().through(RetryCarefulWire.class, 50));

        final String path = String.format("/user/teams");
        try{
            //GET /user/teams
            final JsonResponse jr = github.entry().uri().path(path).back().method(Request.GET).fetch()
                    .as(JsonResponse.class);
            
            logger.info("jr =" + jr);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //Read the Json data as a list of teams
            List<GitHubTeam> teamArray = objectMapper.readValue(jr.body(), new TypeReference<List<GitHubTeam>>(){});

            //You could build this to use the rest of the data if you wanted more than the names
            for (GitHubTeam gitHubTeam : teamArray) {
                logger.info(user + " is part of team: " +gitHubTeam.name);
                teams.add(gitHubTeam.name);
            }
        }
        catch(final Exception e){
            logger.error("Exception happened while trying to get teams of user");
            logger.error("Exception", e);
        }

        return teams;
    }

    public List<String> getIssues(final OAuth2AuthenticationToken oauthToken){
        final List<String> issues = new ArrayList<String>();

        if (oauthToken == null) {
            return issues;
        }

        final OAuth2User oAuth2User = oauthToken.getPrincipal();
        final String user = (String) oAuth2User.getAttributes().get("login");

        Github github = null;

        if (clientService == null) {
            logger.error(String.format("unable to obtain autowired clientService"));
            return issues;
        }
        final OAuth2AuthorizedClient client = clientService
                .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        if (client == null) {
            logger.info(String.format("clientService was not null but client returned was null for user %s", user));
            return issues;
        }

        final OAuth2AccessToken token = client.getAccessToken();

        if (token == null) {
            logger.info(String.format("client for %s was not null but getAccessToken returned null", user));
            return issues;
        }
        final String accessToken = token.getTokenValue();
        if (accessToken == null) {
            logger.info(String.format("token was not null but getTokenValue returned null for user %s", user));
            return issues;
        }

        github = new RtGithub(new RtGithub(accessToken).entry().through(RetryCarefulWire.class, 50));

        List<String> repos = getRepos(oauthToken);
        for (String fullname : repos) {
            final String path = String.format("/user/issues");
        
        
            try{
                /// /repos/:owner/:repo/issues
                final JsonResponse jr = github.entry().uri().path(path).back().method(Request.GET).fetch()
                        .as(JsonResponse.class);
                
                logger.info("jr =" + jr);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                //Read the Json data as a list of issues
                List<GitHubIssues> issueArray = objectMapper.readValue(jr.body(), new TypeReference<List<GitHubIssues>>(){});

                //You could build this to use the rest of the data if you wanted more than the names
                for (GitHubIssues gitHubIssue : issueArray) {
                    logger.info(user + " is assigned issue: " + gitHubIssue.title);
                    issues.add(gitHubIssue.title);
                }
            }
            catch(final Exception e){
                logger.error("Exception happened while trying to get issues of user");
                logger.error("Exception", e);
            }
        }

        return issues;
    }

    public List<String> getRepos(final OAuth2AuthenticationToken oauthToken){
        final List<String> Repos = new ArrayList<String>();

        if (oauthToken == null) {
            return Repos;
        }

        final OAuth2User oAuth2User = oauthToken.getPrincipal();
        final String user = (String) oAuth2User.getAttributes().get("login");

        Github github = null;

        if (clientService == null) {
            logger.error(String.format("unable to obtain autowired clientService"));
            return Repos;
        }
        final OAuth2AuthorizedClient client = clientService
                .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        if (client == null) {
            logger.info(String.format("clientService was not null but client returned was null for user %s", user));
            return Repos;
        }

        final OAuth2AccessToken token = client.getAccessToken();

        if (token == null) {
            logger.info(String.format("client for %s was not null but getAccessToken returned null", user));
            return Repos;
        }
        final String accessToken = token.getTokenValue();
        if (accessToken == null) {
            logger.info(String.format("token was not null but getTokenValue returned null for user %s", user));
            return Repos;
        }

        github = new RtGithub(new RtGithub(accessToken).entry().through(RetryCarefulWire.class, 50));

        final String path = String.format("/user/repos");
        try{
            //GET /user/repos
            final JsonResponse jr = github.entry().uri().path(path).back().method(Request.GET).fetch()
                    .as(JsonResponse.class);

            logger.info("jr =" + jr);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //Read the Json data as a list of teams
            List<GitHubRepos> ReposArray = objectMapper.readValue(jr.body(), new TypeReference<List<GitHubRepos>>(){});

            //You could build this to use the rest of the data if you wanted more than the names
            for (GitHubRepos gitHubRepos : ReposArray) {
                logger.info(user + " has repo " + gitHubRepos.full_name);
                Repos.add(gitHubRepos.full_name);
            }
        }
        catch(final Exception e){
            logger.error("Exception happened while trying to get teams of user");
            logger.error("Exception", e);
        }

        return Repos;
    }
}
