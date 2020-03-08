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
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;


@Controller
public class UserController {

    private UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthControllerAdvice controllerAdvice;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;   
    }
    
    @GetMapping("admin/users")
    public String index(Model model) {
        //Iterable is interface in java.lang
        Iterable<AppUser> users= userRepository.findAll();
        model.addAttribute("users", users);
        return "user/index";
    }
    
    @GetMapping("user/settings")
    public String settingsForm(Model model, OAuth2AuthenticationToken token) {
        AppUser u = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);
        Long searches = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getSearches();
        Long time = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getTime();
        model.addAttribute("user", u);
        model.addAttribute("user_template", new AppUser());
        model.addAttribute("api_uses", searches);
        model.addAttribute("time", time);
        model.addAttribute("max_uses", AppUser.MAX_API_USES);
        return "user/settings";
    }

    @PostMapping("user/settings/update")
    public String updateKey(@ModelAttribute AppUser user, Model model, OAuth2AuthenticationToken token) {
        AppUser u = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);
        Long searches = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getSearches();
        Long time = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getTime();
        
        String apiKey = sanitizeApikey(u.getApikey());

        //check if new api key entered
        if (apiKey.equals(sanitizeApikey(user.getApikey()))) {} 
        else {
            u.setApikey(user.getApikey());
            searches = 0l;
            u.setSearches(0l);
        }
        
        userRepository.save(u);
        model.addAttribute("user", u);
        model.addAttribute("user_template", new AppUser());
        model.addAttribute("api_uses", searches);
        model.addAttribute("time", time);
        model.addAttribute("max_uses", AppUser.MAX_API_USES);
        return "user/settings";
    }

    /* Removes whitespace from apikey */
    private String sanitizeApikey(String apikey) {
        return apikey.replaceAll("\\s", "");
    }

}
