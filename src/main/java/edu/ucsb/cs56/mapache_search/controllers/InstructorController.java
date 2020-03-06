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

import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import javax.validation.Valid;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
public class InstructorController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public InstructorController(UserRepository repo) {
        this.userRepository = repo;
    }
    @GetMapping("/instructor")
    public String index(Model model) {
        //Iterable is interface in java.lang
        return "/instructor/index";
    }
    @PostMapping("/instructorDashboard/delete/{uid}")
    public String deleteViewer(@PathVariable("id") String uid, Model model,
            RedirectAttributes redirAttrs, AppUser user) {
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }

        AppUser appUser = userRepository.findByUid(uid).get(0);
        if (appUser == null) {
            redirAttrs.addFlashAttribute("alertDanger", "Instructor with that uid does not exist.");
        } else {
            userRepository.findByUid(uid).get(0).setIsInstructor(false);
            redirAttrs.addFlashAttribute("alertSuccess", "Instructor successfully deleted.");      
        }
        return "redirect:/instructorDashboard/addInstructor";
    }

    @PostMapping("/instructorDashboard/add")
    public String addInstructor(@Valid AppUser user, BindingResult result, Model model,
            RedirectAttributes redirAttrs, OAuth2AuthenticationToken token) {
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        AppUser appUser = userRepository.findByUid(user.getUid()).get(0);
        if (appUser == null) {
            redirAttrs.addFlashAttribute("alertDanger", "User with that uid does not exist.");
        } else {
            String uid = appUser.getUid();
            if (appUser.getIsInstructor()) {
                redirAttrs.addFlashAttribute("alertDanger", "User " + uid + " is already an Instructor.");    
            } else {
                appUser.setIsInstructor(true);
                redirAttrs.addFlashAttribute("alertSuccess", "Instructor successfully added.");    
            }
        }
        return "redirect:/instructorDashboard/addInstructor";
    }
}
