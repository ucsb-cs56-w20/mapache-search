package edu.ucsb.cs56.mapache_search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchTermsRepository;

import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.membership.MembershipService;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.entities.SearchTerms;
import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class InstructorController {

    static final Logger logger = LoggerFactory.getLogger(InstructorController.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthControllerAdvice controllerAdvice;

    @Autowired
    public InstructorController(UserRepository repo) {
        this.userRepository = repo;
    }

    @GetMapping("instructor")
    public String index(Model model, RedirectAttributes redirAttrs, AppUser user, OAuth2AuthenticationToken token) {
        String role = ms.role(token);
        if (!role.equals("Admin")) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        List<SearchTerms> searchTermsList = searchtermsRepository.findAll();
        int amountSearched = 0;
        for(int pos = 0; pos < searchTermsList.size(); pos++) {
            if (pos > 100) break;
            SearchTerms searched = searchTermsList.get(pos);
            amountSearched += searched.getCount();
        }
        model.addAttribute("searchCount",amountSearched);
        return "instructor/index";
    }  
    
    @PostMapping("/instructor/delete/{username}")
    public String deleteViewer(@PathVariable("username") String username, Model model,
            RedirectAttributes redirAttrs, OAuth2AuthenticationToken token) {
        AppUser user = userRepository.findByUsername(controllerAdvice.getTheUsername(token)).get(0);
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/"; 
        }

        AppUser appUser = userRepository.findByUsername(username).get(0);
        appUser.setIsInstructor(false);
        redirAttrs.addFlashAttribute("alertSuccess", "Instructor successfully deleted.");      
        model.addAttribute("newInstructor", new AppUser());
        model.addAttribute("appUsers", userRepository.findAll());
        return "redirect:/instructor/add_instructor";
    }

    @PostMapping("/instructor/add/{username}")
    public String addInstructor(@PathVariable("username") String username, Model model,
            RedirectAttributes redirAttrs, OAuth2AuthenticationToken token) {
        AppUser user = userRepository.findByUsername(controllerAdvice.getTheUsername(token)).get(0);
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        AppUser appUser = userRepository.findByUsername(username).get(0);
        appUser.setIsInstructor(true);
        redirAttrs.addFlashAttribute("alertSuccess", "Instructor successfully added.");   
        model.addAttribute("newInstructor", new AppUser());
        model.addAttribute("appUsers", userRepository.findAll());
        return "redirect:/instructor/add_instructor";
    }


    @GetMapping("/instructor/add_instructor")
    public String getaddInstructor(Model model, RedirectAttributes redirAttrs, OAuth2AuthenticationToken token, AppUser newInstructor){
        AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        model.addAttribute("appUsers", userRepository.findAll());
        return "instructor/add_instructor";
    }
    

    @GetMapping("/instructor/random_student_generator")
    public String getRandomStudent(Model model, OAuth2AuthenticationToken token){
        return "instructor/random_student_generator";
    }

}
