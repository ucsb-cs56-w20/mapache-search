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

import edu.ucsb.cs56.mapache_search.entities;
import edu.ucsb.cs56.mapache_search.repositories;
import java.util.Optional;
import javax.validation.Valid;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstructorController {
@PostMapping("/instructorDashboard/delete/{uid}")
    public String deleteViewer(@PathVariable("id") long uid, Model model,
            RedirectAttributes redirAttrs, AppUser user) {
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }

        Optional<AppUser> appUser = UserRepository.findByUid(uid);
        if (!appUser.isPresent()) {
            redirAttrs.addFlashAttribute("alertDanger", "Instructor with that uid does not exist.");
        } else {
            String uid = appUser.getUid();
            UserRepository.findByUid(uid).setIsInstructor(false);
            redirAttrs.addFlashAttribute("alertSuccess", "Instructor successfully deleted.");      
        }
        return "redirect:/instructorDashboard/addInstructor";
    }

    @PostMapping("/instructorDashboard/add")
    public String addAdmin(@Valid Admin admin, BindingResult result, Model model,
            RedirectAttributes redirAttrs, OAuth2AuthenticationToken token) {
        if (!user.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        Optional<AppUser> appUser = UserRepository.findByUid(uid);
        if (!appUser.isPresent()) {
            redirAttrs.addFlashAttribute("alertDanger", "User with that uid does not exist.");
        } else {
            String uid = appUser.getUid();
            if (appUser.get().getIsInstructor()) {
                redirAttrs.addFlashAttribute("alertDanger", "User " + uid + " has already been an Instructor.");    
            } else {
                UserRepository.findByUid(uid).setViewer(true);
                redirAttrs.addFlashAttribute("alertSuccess", "Instructor successfully added.");    
            }
        }
        return "redirect:/instructorDashboard/addInstructor";
    }
}