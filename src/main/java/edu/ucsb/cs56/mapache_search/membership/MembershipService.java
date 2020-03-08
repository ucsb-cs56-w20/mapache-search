package edu.ucsb.cs56.mapache_search.membership;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import java.util.ArrayList;
import java.util.List;

public interface MembershipService {

    /** check membership
     * @param oAuth2AuthenticationToken oauth token 
     * @return is current logged in user a member but NOT an admin of the github org?
     * */
    public boolean isMember(OAuth2AuthenticationToken oAuth2AuthenticationToken);

    /** check admin status
     * @param oAuth2AuthenticationToken oauth token 
     * @return is current logged in user a member of the github org?
    */
    public boolean isAdmin(OAuth2AuthenticationToken oAuth2AuthenticationToken);

    /** check member or admin
     * @param oAuth2AuthenticationToken oauth token 
     * @return is current logged in user a member or admin of the github org 
     * */
    default public boolean isMemberOrAdmin(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return isMember(oAuth2AuthenticationToken) || isAdmin(oAuth2AuthenticationToken);
    }

    default public List<String> getTeams(OAuth2AuthenticationToken oAuth2AuthenticationToken){
        return getTeams(oAuth2AuthenticationToken);
    }

    default public List<String> getIssues(OAuth2AuthenticationToken oAuth2AuthenticationToken){
        return getIssues(oAuth2AuthenticationToken);
    }

    /** get list of properties 
     * @return list of repos from application.properties 
     * */
    public List<String> getRepos();

     /** get Project Organization Name 
     * @return Project Organization Name from application.properties 
     * */
    public String getProjectOrg();

    default public String role(OAuth2AuthenticationToken token) {
        if (token==null)
            return "Guest";
        if (isAdmin(token))
            return "Admin";
        if (isMember(token))
            return "Member";
        return "Guest";
    }

}