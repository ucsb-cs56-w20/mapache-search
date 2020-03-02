package edu.ucsb.cs56.mapache_search.controllers;

import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.membership.GithubOrgMembershipService;

@Controller
public class GithubController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private AuthControllerAdvice controllerAdvice;

    @GetMapping("/github/")
    public String gitHub(Model model, OAuth2AuthenticationToken token, RedirectAttributes redirAttrs) {
        logger.info(controllerAdvice.toString());
        if(controllerAdvice.getIsAdmin(token) || controllerAdvice.getIsMember(token)){
            return "github/index";
        }
        else{
            redirAttrs.addFlashAttribute("alertDanger", "You need to be logged in to access that page");
            return "redirect:/";
        }
    }
}