package edu.ucsb.cs56.mapache_search.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.entities.SearchQueries;
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;
import edu.ucsb.cs56.mapache_search.repositories.ResultTagRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchQueriesRepository;


@Controller
public class UserController {

    private UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthControllerAdvice controllerAdvice;
    
    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ResultTagRepository resultTagRepository;

	@Autowired
    private SearchQueriesRepository searchQueriesRepository;

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
    
    @GetMapping("instructor/random_student_results")
    public String randomStudent(@ModelAttribute AppUser user, Model model, OAuth2AuthenticationToken token) {
       AppUser current = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);
       if (!current.getIsInstructor()) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        Random rand = new Random();     
        AppUser u = (userRepository.findAll()).get(Math.toIntExact(rand.nextInt()%userRepository.count()));
        Long searches = u.getSearches();
        Long time = u.getTime();
        userRepository.save(u);
	List<SearchQueries> searchqueriesByUser = searchQueriesRepository.findByUid(u.getUid());
        List<UserVote> voteByUser = voteRepository.findByUserAndUpvote(u, true);
        List<ResultTag> tagByUser = resultTagRepository.findByUser(u);
        model.addAttribute("user", u);
        model.addAttribute("user_template", new AppUser());
        model.addAttribute("api_uses", searches);
		model.addAttribute("max_uses", AppUser.MAX_API_USES);
		model.addAttribute("searchqueries", searchqueriesByUser);
        model.addAttribute("userVotes", voteByUser);
        model.addAttribute("userTags", tagByUser);
        return "instructor/random_student_results";
    }

    /* Removes whitespace from apikey */
    private String sanitizeApikey(String apikey) {
        return apikey.replaceAll("\\s", "");
    }

}
