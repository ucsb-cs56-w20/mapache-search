package edu.ucsb.cs56.mapache_search.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
	
@Controller
public class LinkController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    @GetMapping("/link")
    public String redirect(@RequestParam(name = "url", required = true) String url) {
        logger.info("Redirecting to: "  + url);
	    return "redirect:" + url;
    }
}
