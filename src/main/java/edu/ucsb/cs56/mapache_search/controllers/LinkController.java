package edu.ucsb.cs56.mapache_search.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.controllers.BasicErrorController;
import edu.ucsb.cs56.mapache_search.entities.Link;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.LinkRepository;
import java.util.Date;

@Controller
public class LinkController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private AuthControllerAdvice controllerAdvice;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/link")
    public String redirect(@RequestParam(name = "url", required = true) String url, OAuth2AuthenticationToken token, RedirectAttributes redirAttrs) {
        logger.info("Redirecting to: "  + url);
        logger.info(Boolean.toString(controllerAdvice.getIsLoggedIn(token)));
    
        if (controllerAdvice.getIsAdmin(token) || controllerAdvice.getIsMember(token)) {
            AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);

            Link userLink = new Link();
            userLink.setUserId(user);
            userLink.setUrl(url);
            userLink.setTimestamp(new Date());

            linkRepository.save(userLink);

            return "redirect:" + url;
            
        } 
        else {
            redirAttrs.addFlashAttribute("alertDanger", "You need to be logged in to access that endpoint");
            return "redirect:/";
        }
    }
}