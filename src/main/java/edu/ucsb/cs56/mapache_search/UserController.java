package edu.ucsb.cs56.mapache_search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;


@Controller
public class UserController {

    private UserRepository userRepository;

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
        model.addAttribute("user", u);
        model.addAttribute("user_template", new AppUser());
        return "user/settings";
    }

}
