package edu.ucsb.cs56.mapache_search.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
	
@Controller
public class LinkController {

    
    @GetMapping("/link")
    //    @ResponseBody
    public String redirect(@RequestParam(name = "url", required = true) String url) {
	return "redirect:" + url;
    }
}
