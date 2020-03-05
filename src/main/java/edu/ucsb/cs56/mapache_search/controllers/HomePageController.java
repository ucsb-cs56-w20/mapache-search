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

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
    public HomePageController(SearchResultRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchObject", new SearchObject());
        List<UserVote> upVoteList = voteRepository.findByUpvoteOrderByTimestampDesc(true); //A List that stores UserVote only when the user upvoted 
        //Addubg an attribute to the model indicating the size of the upVoteList
        int a = upVoteList.size();
        model.addAttribute("upVoteListSize",a);
        //Adding the upvote list to a model
        model.addAttribute("upVoteList", upVoteList);
        return "index";
    }

    @GetMapping("/filter")
    public String filter(Model model) {
        model.addAttribute("searchObject", new SearchObject());
        List<UserVote> upVoteList = voteRepository.findByUpvoteOrderByTimestampDesc(true); //A List that stores UserVote only when the user upvoted 
        //Addubg an attribute to the model indicating the size of the upVoteList
        int a = upVoteList.size();
        model.addAttribute("upVoteListSize",a);
        //Adding the upvote list to a model
        model.addAttribute("upVoteList", upVoteList);
        return "filter";
    }
};