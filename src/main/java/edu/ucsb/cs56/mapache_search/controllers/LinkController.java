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

@Controller
public class LinkController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private AuthControllerAdvice controllerAdvice;

    @Autowired
    private BasicErrorController signInError;
    
    @GetMapping("/link")
    public String redirect(@RequestParam(name = "url", required = true) String url, OAuth2AuthenticationToken token) {
        logger.info("Redirecting to: "  + url);
	if (controllerAdvice.getIsAdmin(token) || controllerAdvice.getIsMember(token)) {
	    return "redirect:" + url;
	} else {
	    return signInError.loginError();
	}
    }
}
