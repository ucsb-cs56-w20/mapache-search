package edu.ucsb.cs56.mapache_search;

import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;

import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.entities.AppUser;

import edu.ucsb.cs56.mapache_search.search.SearchResult;
import net.minidev.json.JSONObject;
import edu.ucsb.cs56.mapache_search.search.Item;
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
import java.util.Arrays;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
public class SearchController {

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
    public SearchController(SearchResultRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchObject", new SearchObject());
        return "index";
    }

    @GetMapping("/UpDownSearch")
    public String upDownSearch(Model model) {
        model.addAttribute("searchObject", new SearchObject());
        return "upDownIndex";
    }





    @GetMapping("/searchResults")
    public String search(SearchParameters params, Model model, OAuth2AuthenticationToken token) throws IOException {
        model.addAttribute("searchParams", params);

        String apiKey = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getApikey();
        Long searches = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getSearches() + 1l;
        Long time = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getTime();
        Long currentTime = (long) (new Date().getTime()/1000/60/60/24); //get relative days as a Long

        AppUser u = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);
        u.setSearches(searches);
        userRepository.save(u);

        //up the search count, if maxed, dont search, if more than 24hrs reset.
        if(currentTime > time){
            u.setSearches(1l);
            u.setTime(currentTime);
            userRepository.save(u);
        }

        String json = searchService.getJSON(params, apiKey);

        SearchResult sr = SearchResult.fromJSON(json);
        model.addAttribute("searchResult", sr);
        model.addAttribute("searchObject", new SearchObject());
        model.addAttribute("previousSearch", params.getQuery());

        if (json.equals("{\"error\": \"401: Unauthorized\"}")) {
            return "errors/401.html"; // corresponds to src/main/resources/templates/errors/401.html
        }
        //add query to previous search table
        String [] searchHistory = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getSearchHistory();
        String[] newHistory; 
        if (searchHistory == null){
            newHistory = new String[1]; 
            newHistory[0] = params.getQuery();
        }else{
            newHistory = new String[searchHistory.length+1]; 
            newHistory[0] = params.getQuery();
            for(int i = 0; i <searchHistory.length; i++){
                newHistory[i+1] = searchHistory[i];
            }
        }
        u.setSearchHistory(newHistory);
        model.addAttribute("previousSearches", newHistory);
        userRepository.save(u);
            
        
    

        //model.addAttribute("voteResult", voteResults);
        model.addAttribute("api_uses", searches);
        model.addAttribute("max_api_uses", AppUser.MAX_API_USES);

        logger.info("currentTime=" + Long.toString(currentTime));
        logger.info("searches=" + Long.toString(searches));
        logger.info("max_api_uses=" + Long.toString(AppUser.MAX_API_USES));
        logger.info(Arrays.toString(searchHistory));
        logger.info(Arrays.toString(newHistory));
        return "searchResults"; // corresponds to src/main/resources/templates/searchResults.html
    }

    public class ResultVoteWrapper implements Comparable<ResultVoteWrapper> {
        private Item googleResult;
        private SearchResultEntity dbResult;
        private int position;
        private boolean didUpvote;
        private boolean didDownvote;

        public ResultVoteWrapper(Item googleResult, SearchResultEntity dbResult, int position, boolean upvoted, boolean downvoted) {
            this.googleResult = googleResult;
            this.dbResult = dbResult;
            this.position = position;
            this.didUpvote = upvoted;
            this.didDownvote = downvoted;
        }

        public SearchResultEntity getDBResult() {
            return dbResult;
        }

        public Item getGoogleResult() {
            return googleResult;
        }

        public int getPosition() {
            return position;
        }

        public boolean hasUpvoted() {
            return didUpvote;
        }

        public boolean hasDownvoted() {
            return didDownvote;
        }

        public int compareTo(ResultVoteWrapper oWrapper) {
            if (getDBResult().getVotecount() > oWrapper.getDBResult().getVotecount()) {
                return 1;
            }
            else if (getDBResult().getVotecount() < oWrapper.getDBResult().getVotecount()) {
                return -1;
            } else {
                if (getPosition() < oWrapper.getPosition()) {
                    return 1;
                } else if (getPosition() > oWrapper.getPosition()) {
                    return -1;
                }
                return 0;
            }
        }
    }


    @GetMapping("/searchUpDownResults")
    public String searchUpDown(SearchParameters params, Model model, OAuth2AuthenticationToken token) throws IOException {
        model.addAttribute("searchParams", params);

        AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);

        String apiKey = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getApikey();
        String json = searchService.getJSON(params, apiKey);



        SearchResult sr = SearchResult.fromJSON(json);
        model.addAttribute("searchResult", sr);

        if(sr.getKind() != "error"){
            List<ResultVoteWrapper> voteResults = new ArrayList<>();
            int count = 0;
            for(Item item : sr.getItems()) {
                List<SearchResultEntity> matchingResults = searchRepository.findByUrl(item.getLink());
                SearchResultEntity result;
                if (matchingResults.isEmpty()) {
                    result = new SearchResultEntity();
                    result.setUrl(item.getLink());
                    result.setVotecount((long) 0);
                    searchRepository.save(result);
                } else {
                    result = matchingResults.get(0);
                }
                boolean upvoted = false, downvoted = false;

                List<UserVote> myVotes = voteRepository.findByUserAndResult(user, result);
                if (!myVotes.isEmpty()) {
                    UserVote myVote = myVotes.get(0);
                    if (myVote.getUpvote()) {
                        upvoted = true;
                    } else {
                        downvoted = true;
                    }
                }
                voteResults.add(new ResultVoteWrapper(item, result, count, upvoted, downvoted));


                if (++count == 10)
                    break;
            }

            Collections.sort(voteResults, Collections.reverseOrder());
            model.addAttribute("voteResult", voteResults);
        }

        return "searchUpDownResults"; // corresponds to src/main/resources/templates/searchResults.html
    }

    @GetMapping("/updateVote")
    @ResponseBody
    public String searchUpDown(@RequestParam(name = "direction", required = true) String direction, @RequestParam(name = "id", required = true) long id, Model model, OAuth2AuthenticationToken token) throws IOException {
        long voteCount = 0;

        List<SearchResultEntity> matchingResults = searchRepository.findById(id);
        if (!matchingResults.isEmpty()) {

            SearchResultEntity result = matchingResults.get(0);
            AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);

            List<UserVote> byUserAndResult = voteRepository.findByUserAndResult(user, result);

            if(byUserAndResult.size() > 0){
                voteRepository.delete(byUserAndResult.get(0));
            }
            if(!(direction.equals("none"))){
                UserVote vote = new UserVote();
                vote.setUser(user);
                vote.setResult(result);
                if (direction.equals("up")){
                    vote.setUpvote(true);
                }
                if(direction.equals("down")){
                    vote.setUpvote(false);
                }
                voteRepository.save(vote);
            }
            voteCount = voteRepository.findByResultAndUpvote(result, true).size() - voteRepository.findByResultAndUpvote(result, false).size(); // the score is the number of upvotes minus the number of downvotes
            result.setVotecount(voteCount);
            searchRepository.save(result);
        }

        return String.valueOf(voteCount); // returns the new vote total
    }


}
