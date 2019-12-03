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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
        
        return "searchResults"; // corresponds to src/main/resources/templates/searchResults.html
    }

    public class ResultVoteWrapper {
        private Item googleResult;
        private SearchResultEntity dbResult;

        public ResultVoteWrapper(Item googleResult, SearchResultEntity dbResult) {
            this.googleResult = googleResult;
            this.dbResult = dbResult;
        }

        public SearchResultEntity getDBResult() {
            return dbResult;
        }

        public void setDBResult(SearchResultEntity dbResult) {
            this.dbResult = dbResult;
        }

        public Item getGoogleResult() {
            return googleResult;
        }

        public void setGoogleResult(Item googleResult) {
            this.googleResult = googleResult;
        }
    }

    @GetMapping("/searchUpDownResults")
    public String searchUpDown(@RequestParam(name = "query", required = true) String query, Model model, OAuth2AuthenticationToken token) throws IOException {
        model.addAttribute("query", query);

        String apiKey = userRepository.findByUid(controllerAdvice.getUid(token)).get(0).getApikey();
        String json = searchService.getJSON(query, apiKey);



        SearchResult sr = SearchResult.fromJSON(json);
        model.addAttribute("searchResult", sr);

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

            List<UserVote> byResult = voteRepository.findByResult(result);
            long voteCount = 0;
            for(UserVote vote : byResult){
                if(vote.getUpvote() == true){
                    voteCount += 1;
                }else{
                    voteCount -= 1;
                }
            }
            result.setVotecount(voteCount);

            voteResults.add(new ResultVoteWrapper(item, result));

            
            if (++count == 10)
                break;
        }

        model.addAttribute("voteResult", voteResults);
        
        return "searchUpDownResults"; // corresponds to src/main/resources/templates/searchResults.html
    }

    @GetMapping("/updateVote")
    public String searchUpDown(@RequestParam(name = "direction", required = true) String direction, @RequestParam(name = "id", required = true) long id, Model model, OAuth2AuthenticationToken token) throws IOException {
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
        }

        return "forward:/searchUpDownResults"; // brings you back to results view
    }


}
