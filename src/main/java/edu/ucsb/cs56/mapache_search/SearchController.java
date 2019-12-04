package edu.ucsb.cs56.mapache_search;

import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;

import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.entities.AppUser;

import edu.ucsb.cs56.mapache_search.search.SearchResult;
import edu.ucsb.cs56.mapache_search.search.Item;
import java.io.IOException;
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
    public String search(@RequestParam(name = "query", required = true) String query, Model model, OAuth2AuthenticationToken token) throws IOException {
        model.addAttribute("query", query);

        String apiKey = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getApikey();
        String json = searchService.getJSON(query, apiKey);

        SearchResult sr = SearchResult.fromJSON(json);
        model.addAttribute("searchResult", sr);
        model.addAttribute("searchObject", new SearchObject());
        model.addAttribute("previousSearch", query);
        
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
    public String searchUpDown(@RequestParam(name = "query", required = true) String query, Model model, OAuth2AuthenticationToken token) throws IOException {
        model.addAttribute("query", query);

        AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);

        String apiKey = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getApikey();
        String json = searchService.getJSON(query, apiKey);



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
