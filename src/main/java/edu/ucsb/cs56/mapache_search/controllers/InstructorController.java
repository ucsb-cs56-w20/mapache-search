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
    @Autowired
    private MembershipService ms;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private SearchTermsRepository searchtermsRepository;

    @Autowired
    public InstructorController(UserRepository repo) {
        this.userRepository = repo;
    }
    @GetMapping("instructor")
    public String index(Model model) {
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

    @GetMapping("instructor/student_stats")
    public String data(Model model) {
        return "instructor/student_stats";
    }

    @GetMapping("instructor/upvotes")
    public String upvotes(Model model) {
        List<UserVote> upVoteList = voteRepository.findAll();
        ArrayList<searchUpVotedWrapper> upVotedSearches = new ArrayList<>();
        for(int pos = 0; pos < upVoteList.size(); pos++) {
            if (pos > 100) break;
            UserVote vote = upVoteList.get(pos);
            upVotedSearches.add(new searchUpVotedWrapper(vote.getResult()));
        }
        Collections.sort(upVotedSearches);
        int x = upVotedSearches.size();
        model.addAttribute("upVotedSearchesSize",x);
        model.addAttribute("upVotedSearches", upVotedSearches);

        return "instructor/upvote_page";
    }

        @GetMapping("instructor/popular_searches")
        public String searches(Model model) {
        List<SearchTerms> searchTermsList = searchtermsRepository.findAll();
        ArrayList<searchedTermsWrapper> searchedTerms = new ArrayList<>();
        for(int pos = 0; pos < searchTermsList.size(); pos++) {
            if (pos > 100) break;
            SearchTerms searched = searchTermsList.get(pos);
            searchedTerms.add(new searchedTermsWrapper(searched));
        }
        Collections.sort(searchedTerms);
        int x = searchedTerms.size();
        model.addAttribute("searchedTermsSize",x);
        model.addAttribute("searchedTerms", searchedTerms);

        return "instructor/popular_searches";
    }

  /*   WITH ADMIN CHECK */
/*     @GetMapping("instructor")
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

    @GetMapping("instructor/student_stats")
    public String data(Model model, RedirectAttributes redirAttrs, AppUser user, OAuth2AuthenticationToken token) {
        String role = ms.role(token);
        if (!role.equals("Admin")) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        return "instructor/student_stats";
    }

    @GetMapping("instructor/upvotes")
    public String upvotes(Model model, RedirectAttributes redirAttrs, AppUser user, OAuth2AuthenticationToken token) {
        String role = ms.role(token);
        if (!role.equals("Admin")) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        List<UserVote> upVoteList = voteRepository.findAll();
        ArrayList<searchUpVotedWrapper> upVotedSearches = new ArrayList<>();
        for(int pos = 0; pos < upVoteList.size(); pos++) {
            if (pos > 100) break;
            UserVote vote = upVoteList.get(pos);
            upVotedSearches.add(new searchUpVotedWrapper(vote.getResult()));
        }
        Collections.sort(upVotedSearches);
        int x = upVotedSearches.size();
        model.addAttribute("upVotedSearchesSize",x);
        model.addAttribute("upVotedSearches", upVotedSearches);

        return "instructor/upvote_page";
    }
    
    @GetMapping("instructor/popular_searches")
    public String searches(Model model, RedirectAttributes redirAttrs, AppUser user, OAuth2AuthenticationToken token) {
        String role = ms.role(token);
        if (!role.equals("Admin")) {
            redirAttrs.addFlashAttribute("alertDanger",
                    "You do not have permission to access that page");
            return "redirect:/";
        }
        List<SearchTerms> searchTermsList = searchtermsRepository.findAll();
        ArrayList<searchedTermsWrapper> searchedTerms = new ArrayList<>();
        for(int pos = 0; pos < searchTermsList.size(); pos++) {
            if (pos > 100) break;
            SearchTerms searched = searchTermsList.get(pos);
            searchedTerms.add(new searchedTermsWrapper(searched));
        }
        Collections.sort(searchedTerms);
        int x = searchedTerms.size();
        model.addAttribute("searchedTermsSize",x);
        model.addAttribute("searchedTerms", searchedTerms);

        return "instructor/popular_searches";
    }
 */
    
    @PostMapping("/instructor/delete/{uid}")
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
        return "redirect:/instructor/addInstructor";
    }

    @PostMapping("/instructor/add")
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
        return "redirect:/instructor/addInstructor";
    }
    
    public class searchUpVotedWrapper implements Comparable<searchUpVotedWrapper> {
        private SearchResultEntity result;

        public searchUpVotedWrapper(SearchResultEntity result) {
            this.result = result;
        }

        public SearchResultEntity getResult() {
            return result;
        }

        public int compareTo(searchUpVotedWrapper objSearch) {
            if (getResult().getVotecount() > objSearch.getResult().getVotecount()) {
                return 1;
            }
            else if (getResult().getVotecount() < objSearch.getResult().getVotecount()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public class searchedTermsWrapper implements Comparable<searchedTermsWrapper> {
        private SearchTerms result;

        public searchedTermsWrapper(SearchTerms result) {
            this.result = result;
        }

        public SearchTerms getResult() {
            return result;
        }

        public int compareTo(searchedTermsWrapper objSearch) {
            if (getResult().getCount() > objSearch.getResult().getCount()) {
                return 1;
            }
            else if (getResult().getCount() < objSearch.getResult().getCount()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
