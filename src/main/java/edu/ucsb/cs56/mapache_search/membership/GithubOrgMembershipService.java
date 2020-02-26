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

import java.util.ArrayList;
import java.util.List;

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

    private Logger logger = LoggerFactory.getLogger(GithubOrgMembershipService.class);

    //If you put in final, then you can't assign with @Value properly
    private List<String> adminEmails;

    @Value("${app.member.hosted-domain}")
    private String memberHostedDomain;

    @Value("${app_github_org}")
    private String githubOrg;

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    public GithubOrgMembershipService(@Value("#{'${app.admin.emails}'.split(',')}") List<String> adminEmails) {
        logger.info("GoogleHostedDomain=" + memberHostedDomain);
        this.adminEmails = adminEmails;
        for (int i = 0; i < adminEmails.size(); i++) {
            adminEmails.set(i, adminEmails.get(i).replaceAll("\\s+", ""));
        }
        logger.info("adminEmails=" + adminEmails.toString());
        logger.info("githubOrg=" + githubOrg);
        
    }

    /**
     * is current logged in user a member but NOT an admin of the github org
     */
    public boolean isMember(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return hasRole(oAuth2AuthenticationToken, "member");
    }

    /** is current logged in user a member of the github org */
    public boolean isAdmin(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return hasRole(oAuth2AuthenticationToken, "admin");
    }

    /**
     * is current logged in user has role
     * 
     * @param roleToTest "member" or "admin"
     * @param oauthToken oauth token
     * @return if the current logged in user has that role
     */

    public boolean hasRole(OAuth2AuthenticationToken oauthToken, String roleToTest) {
        if (oauthToken == null) {
            return false;
        }

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String user = (String) oAuth2User.getAttributes().get("login");

        Github github = null;

        if (clientService == null) {
            logger.error(String.format("unable to obtain autowired clientService"));
            return false;
        }
        OAuth2AuthorizedClient client = clientService
                .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        if (client == null) {
            logger.info(String.format("clientService was not null but client returned was null for user %s", user));
            return false;
        }

        OAuth2AccessToken token = client.getAccessToken();

        if (token == null) {
            logger.info(String.format("client for %s was not null but getAccessToken returned null", user));
            return false;
        }
        String accessToken = token.getTokenValue();
        if (accessToken == null) {
            logger.info(String.format("token was not null but getTokenValue returned null for user %s", user));
            return false;
        }

        // github oauth email returns null
        if (oAuth2User.getAttributes().get("email") == null) {
            try {

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

                String path = String.format("/user/memberships/orgs/%s", githubOrg);

                JsonResponse jr = github.entry().uri().path(path).back().method(Request.GET).fetch()
                        .as(JsonResponse.class);

                logger.info("jr =" + jr);

                String actualRole = jr.json().readObject().getString("role");
                String state = jr.json().readObject().getString("state");

                logger.info("actualRole =" + actualRole);
                logger.info("roleToTest =" + roleToTest);
                logger.info("state =" + state);

                return actualRole.equals(roleToTest);
            } catch (Exception e) {
                logger.error("Exception happened while trying to determine membership in github org");
                logger.error("Exception", e);
            }
            // this is google oauth
        }
        if (oAuth2User.getAttributes().get("email") != null) {
            String email = (String) oAuth2User.getAttributes().get("email");
            String hostedDomain = (String) oAuth2User.getAttributes().get("hd");
            logger.info("token email=[" + email + "]");
            logger.info(email + "'s hostedDomain=" + hostedDomain);
            logger.info("All admin Emails=[" + adminEmails.toString() + "]");
            
        }
        return false;
    }

    private boolean isAdminEmail(String email) {
        // return (!adminRepository.findByEmail(email).isEmpty() ||
        // (adminEmails.contains(email));
        return (adminEmails.contains(email));
    }

}
