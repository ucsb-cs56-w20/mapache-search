package edu.ucsb.cs56.mapache_search.controllers;

import edu.ucsb.cs56.mapache_search.preview.PreviewProviderService;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.search.SearchObject;
import edu.ucsb.cs56.mapache_search.search.SearchParameters;
import edu.ucsb.cs56.mapache_search.search.SearchResult;
import edu.ucsb.cs56.mapache_search.search.SearchService;
import edu.ucsb.cs56.mapache_search.stackexchange.StackExchangeItem;
import edu.ucsb.cs56.mapache_search.stackexchange.StackExchangeQueryService;
import edu.ucsb.cs56.mapache_search.stackexchange.objects.Questions;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.Item;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;

import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.search.SearchObject;
import edu.ucsb.cs56.mapache_search.search.SearchParameters;
import edu.ucsb.cs56.mapache_search.search.SearchResult;
import edu.ucsb.cs56.mapache_search.search.SearchService;
import net.minidev.json.JSONObject;
import edu.ucsb.cs56.mapache_search.search.SearchResult;
import java.io.IOException;


import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.ucsb.cs56.mapache_search.repositories.SearchTermsRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchQueriesRepository;
import edu.ucsb.cs56.mapache_search.entities.SearchTerms;
import edu.ucsb.cs56.mapache_search.entities.SearchQueries;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

@Controller
public class HomePageController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchResultRepository searchRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AuthControllerAdvice controllerAdvice;

    @Autowired
    private StackExchangeQueryService queryService;

    @Autowired
    private PreviewProviderService previewService;

    @Autowired
    private SearchTermsRepository searchTermsRepository;

    @Autowired
    private SearchQueriesRepository searchQueriesRepository;


    @Autowired
    public HomePageController(SearchResultRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchObject", new SearchObject());
        List<UserVote> upVoteList = voteRepository.findByUpvoteOrderByTimestampDesc(true); //A List that stores UserVote only when the user upvoted 
        
        ArrayList<UpvoteLink> upVoteLinks = new ArrayList<UpvoteLink>(); // A list that stores the url that got upvoted
        //This for loop is used to get all the url links that have been upvoted

        Calendar currentDateBefore3Days = Calendar.getInstance();
        currentDateBefore3Days.add(Calendar.DATE, -3);

        int countAdded = 0;

        for(int pos = 0; pos < upVoteList.size() && countAdded < 20; pos++) {
            if (pos > 100) break;
            UserVote vote = upVoteList.get(pos);
            if (vote.getTimestamp().after(currentDateBefore3Days.getTime())) {
                UpvoteLink currUpvote = new UpvoteLink();
                currUpvote.srEntity = vote.getResult();
                if (upVoteLinks.contains(currUpvote)) {
                    upVoteLinks.get(upVoteLinks.indexOf(currUpvote)).numUpvotes += 1;
                }
                else {
                    upVoteLinks.add(currUpvote);
                    countAdded++;
                }
            }
        }

        //Addubg an attribute to the model indicating the size of the upVoteList
        // need to do lamba sort thing
        
        int a = upVoteLinks.size();
        model.addAttribute("upVoteLinksSize",a);
        //Adding the upvote links to a model
        model.addAttribute("upVoteLinks", upVoteLinks);
        return "index";
    }

    @GetMapping("/filter")
    public String filter(Model model) {
        model.addAttribute("searchParameters", new SearchParameters());
        List<UserVote> upVoteList = voteRepository.findByUpvoteOrderByTimestampDesc(true); //A List that stores UserVote only when the user upvoted 
        //Addubg an attribute to the model indicating the size of the upVoteList
        int a = upVoteList.size();
        model.addAttribute("upVoteListSize",a);
        //Adding the upvote list to a model
        model.addAttribute("upVoteList", upVoteList);
        return "filter";
    }

    @GetMapping("/searchQueries")
    public String searchQueries(Model model, OAuth2AuthenticationToken token, RedirectAttributes redirAttrs) {
        model.addAttribute("searchObject", new SearchObject());
        List<SearchQueries> queries = searchQueriesRepository.findAllByOrderByTimestampDesc();
        model.addAttribute("SearchQueries", queries);
        model.addAttribute("SearchQueriesSize", queries.size());

        logger.info(controllerAdvice.toString());
        
        if(controllerAdvice.getIsAdmin(token)){
            return "searchQueries"; // new html in the template folder that only admin can access
        }
        else{
            redirAttrs.addFlashAttribute("alertDanger", "You need to be admin to access that page");
            return "redirect:/";
        }
    }


    public class UpvoteLink {
        public int numUpvotes = 1;
        public SearchResultEntity srEntity;
        public List<ResultTag> resultTag;

        @Override
        public boolean equals(Object o){
            if(o instanceof UpvoteLink){
                UpvoteLink p = (UpvoteLink) o;
                 return this.srEntity.getId().equals(p.srEntity.getId());
            } else
                 return false;
        }



    }

};