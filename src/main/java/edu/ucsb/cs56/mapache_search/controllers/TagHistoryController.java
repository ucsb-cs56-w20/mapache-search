package edu.ucsb.cs56.mapache_search.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.ResultTagRepository;


@Controller
public class TagHistoryController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthControllerAdvice controllerAdvice;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResultTagRepository resultTagRepository;


    @GetMapping("/user/taghistory")
    public String tagHist (Model model, OAuth2AuthenticationToken token){
        AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0); 
        List<ResultTag> byUser = resultTagRepository.findByUser(user);

        model.addAttribute("userTags", byUser);
        return "user/taghistory";
    }
}

