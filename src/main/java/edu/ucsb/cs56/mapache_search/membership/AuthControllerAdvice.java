package edu.ucsb.cs56.mapache_search.membership;

import org.springframework.web.bind.annotation.ModelAttribute;

import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;

@ControllerAdvice // All ModelAttributes are shared to any view and this code runs before every
                  // controller action
public class AuthControllerAdvice {

    @Autowired // it will find GithubOrgMembershipService and create bean/instance of it
    private MembershipService membershipService;

    @Autowired // create instance of this
    private UserRepository userRepository;

    @ModelAttribute("isLoggedIn")
    public boolean getIsLoggedIn(OAuth2AuthenticationToken token) {
        return token != null;
    }

    //returns -1 for not logged in, returns 0 for github, 1 for google
    @ModelAttribute("isGoogleOrGithub")
    public Integer isGoogleOrGitHub(OAuth2AuthenticationToken token) {
        if (token == null)
            return -1;
        if (token.getPrincipal().getAttributes().get("email") == null)
            return 0;
        return 1;
    }

    @ModelAttribute("auth")
    public String getToken(OAuth2AuthenticationToken token) {
        if (token == null)
            return "";
        return token.toString();
        //return token.getPrincipal().getAttributes().get("given_name").toString();
    }

    @ModelAttribute("id")
    public String getUid(OAuth2AuthenticationToken token) {
        if (token == null)
            return "";
        
        OAuth2User oAuth2User = token.getPrincipal();
        String uid = "";

        //github
        if (oAuth2User.getAttributes().get("email") == null) {
            uid = oAuth2User.getAttributes().get("id").toString();
        }
        else { //google
            uid = oAuth2User.getAttributes().get("sub").toString();
        }

        List<AppUser> users = userRepository.findByUid(uid);

        if (users.size() == 0) {
            AppUser u = new AppUser();
            u.setUid(uid);
            u.setUsername(token2login(token));
            u.setApikey("");
            u.setSearches(0l);
            userRepository.save(u);
            // username, apikey, uid
        } else {
            if (token2login(token) != users.get(0).getUsername()) {
                //if they changed name or username
                AppUser u = users.get(0);
                u.setUsername(token2login(token));
                userRepository.save(u);
            }
        }

        return uid;
    }

    @ModelAttribute("login")
    public String getLogin(OAuth2AuthenticationToken token) {
        if (token == null)
            return "";
        return token2login(token);
    }

    @ModelAttribute("isMember")
    public boolean getIsMember(OAuth2AuthenticationToken token) {
        return membershipService.isMember(token);
    }

    @ModelAttribute("isAdmin")
    public boolean getIsAdmin(OAuth2AuthenticationToken token) {
        return membershipService.isAdmin(token);
    }

    @ModelAttribute("role")
    public String getRole(OAuth2AuthenticationToken token) {
        return membershipService.role(token);
    }

    private String token2login(OAuth2AuthenticationToken token) {
        if (token.getPrincipal().getAttributes().get("email") == null) //github
            return token.getPrincipal().getAttributes().get("login").toString();
        return token.getPrincipal().getAttributes().get("email").toString();
    }

}
