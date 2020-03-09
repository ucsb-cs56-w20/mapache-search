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
import edu.ucsb.cs56.mapache_search.entities.Tag;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchTermsRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchQueriesRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;
import edu.ucsb.cs56.mapache_search.repositories.TagRepository;
import edu.ucsb.cs56.mapache_search.repositories.ResultTagRepository;

import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.SearchTerms;
import edu.ucsb.cs56.mapache_search.entities.SearchQueries;
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
    private StackExchangeQueryService queryService;

    @Autowired
    private PreviewProviderService previewService;

    @Autowired
    public SearchController(SearchResultRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Autowired
    private SearchTermsRepository searchTermsRepository;

    @Autowired
    private SearchQueriesRepository searchQueriesRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ResultTagRepository resultTagRepository;

    private Map<Item, StackExchangeItem> fetchFromStackExchange(SearchResult sr) {
        // index items by site, then by question id
        Map<String, Map<Integer, List<StackExchangeItem>>> itemsBySite = sr.getItems().stream()
            .filter(i -> previewService.getPreviewType(i).equals("stackexchange"))
            .map(StackExchangeItem::new)
            .collect(
                Collectors.groupingBy(
                    i -> i.getItem().getDisplayLink(),
                    Collectors.groupingBy(
                        StackExchangeItem::getQuestionId,
                        Collectors.toList()
                    )
                )
            );

        // find questions and populate each StackExchangeItem
        itemsBySite.forEach((site, items) -> {
            Questions questions = queryService.findQuestions(site, new ArrayList<>(items.keySet()));

            questions.getItems()
                .forEach(question ->
                    items.get(question.getId())
                        .forEach(item -> item.setQuestion(question)));
        });

        // flatten map, index by original item
        return itemsBySite.values().stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .flatMap(List::stream)
            .collect(Collectors.toMap(
                StackExchangeItem::getItem,
                Function.identity()
            ));
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

        SearchQueries searchQueries = new SearchQueries();
        searchQueries.setUid(u.getUid());
        searchQueries.setTimestamp(new Date());

        boolean haveSearched = doesSearchExist(params.getQuery());
        if (!haveSearched) // Have never been searched before
        {
            SearchTerms searchTerm = new SearchTerms();
            searchTerm.setSearchTerms(params.getQuery());
            searchTerm.setCount(1);
            searchTerm.setTimestamp(new Date());
            searchTermsRepository.save(searchTerm);
            searchQueries.setId(searchTerm.getId());
            logger.info("count is: " + searchTerm.getCount() + "query is: " + searchTerm.getSearchTerms());
        }
        else
        {
            String cleanedStrings = sanitizedSearchTerms(params.getQuery());
            SearchTerms searchTerm = searchTermsRepository.findOneBySearchTerms(cleanedStrings);
            searchTerm.setSearchTerms(params.getQuery());
            int newSearchTermCount = searchTerm.getCount() + 1;
            searchTerm.setCount((newSearchTermCount));
            searchTerm.setTimestamp(new Date());
            searchTermsRepository.save(searchTerm);
            searchQueries.setId(searchTerm.getId());
            logger.info("count is: " + searchTerm.getCount() + " and query is: " + searchTerm.getSearchTerms());
        }

        searchQueriesRepository.save(searchQueries);
        logger.info("uid is:" + searchQueries.getUid() + ", time stamp is: " + searchQueries.getTimestamp() + ", and Id of query is: " + searchQueries.getId());
        

        //up the search count, if maxed, dont search, if more than 24hrs reset.
        if(currentTime > time){
            userRepository.findByUid(controllerAdvice.getUid(token)).get(0).setSearches(1l);
            userRepository.findByUid(controllerAdvice.getUid(token)).get(0).setTime(currentTime);
        }

        String json = searchService.getJSON(params, apiKey);
        SearchResult sr = SearchResult.fromJSON(json);
        /* remove this comment to test search Parameters
        if (params.getSortByUpvotes()) {
            System.out.println("ohdaijhdijsdisadhk, " + params.getWebsite() + " " + params.getLastUpdated());
        }
        */

        Iterable<Tag> tags = tagRepository.findAll();
        List<Tag> allTags = new ArrayList<Tag>();
        for( Tag tag : tags ) {
            allTags.add(tag);
        }
        Collections.sort(allTags, (t1, t2)->{
            return t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase());
        });

        if(sr.getKind() != "error") {
            List<ResultVoteWrapper> voteResults = new ArrayList<>();
            int count = 0;
            for(Item item : sr.getItems()) {
                List<SearchResultEntity> matchingResults = searchRepository.findByLink(item.getLink());
                SearchResultEntity result;
                if (matchingResults.isEmpty()) {
                    result = new SearchResultEntity();
                    result.setLink(item.getLink());
                    result.setHtmlTitle(item.getHtmlTitle());
                    result.setDisplayLink(item.getDisplayLink());

                    result.setVotecount((long) 0);
                    searchRepository.save(result);
                } else {
                    result = matchingResults.get(0);
                }
                boolean upvoted = false, downvoted = false;

                List<UserVote> myVotes = voteRepository.findByUserAndResult(u, result);
                if (!myVotes.isEmpty()) {
                    UserVote myVote = myVotes.get(0);
                    if (myVote.getUpvote()) {
                        upvoted = true;
                    } else {
                        downvoted = true;
                    }
                }

                List<Tag> otherTags = new ArrayList<Tag>(allTags);
                List<Tag> addedTags = new ArrayList<Tag>();
                List<ResultTag> resultTags = resultTagRepository.findByResult(result);
                for (ResultTag resultTag : resultTags) {
                    addedTags.add(resultTag.getTag());
                }
                Collections.sort(addedTags, (t1, t2)->{
                    return t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase());
                });

                otherTags.removeAll(addedTags);
                voteResults.add(new ResultVoteWrapper(item, result, count, upvoted, downvoted, addedTags, otherTags));

                if (++count == 10)
                    break;
            }

            Collections.sort(voteResults, Collections.reverseOrder());
            model.addAttribute("voteResult", voteResults);
        }
        model.addAttribute("searchResult", sr);
        model.addAttribute("searchObject", new SearchObject());
        model.addAttribute("previousSearch", params.getQuery());

        if (json.equals("{\"error\": \"401: Unauthorized\"}")) {
            return "errors/401.html"; // corresponds to src/main/resources/templates/errors/401.html
        }

        model.addAttribute("api_uses", searches);
        model.addAttribute("max_api_uses", AppUser.MAX_API_USES);

        logger.info("currentTime=" + Long.toString(currentTime));
        logger.info("searches=" + Long.toString(searches));
        logger.info("max_api_uses=" + Long.toString(AppUser.MAX_API_USES));

        Map<Item, StackExchangeItem> stackExchangeQuestions = fetchFromStackExchange(sr);
        model.addAttribute("stackExchangeQuestions", stackExchangeQuestions);

        return "searchResults"; // corresponds to src/main/resources/templates/searchResults.html
    }


    public class ResultVoteWrapper implements Comparable<ResultVoteWrapper> {
        private Item googleResult;
        private SearchResultEntity dbResult;
        private int position;
        private boolean didUpvote;
        private boolean didDownvote;
        private List<Tag> addedTags;
        private List<Tag> otherTags;

        public ResultVoteWrapper(Item googleResult, SearchResultEntity dbResult, int position, boolean upvoted, boolean downvoted, List<Tag> addedTags, List<Tag> otherTags) {
            this.googleResult = googleResult;
            this.dbResult = dbResult;
            this.position = position;
            this.didUpvote = upvoted;
            this.didDownvote = downvoted;
            this.addedTags = addedTags;
            this.otherTags = otherTags;
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

        public List<Tag> getAddedTags() {
            return addedTags;
        }

        public List<Tag> getOtherTags() {
            return otherTags;
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

    @GetMapping("/updateVote")
    @ResponseBody
    public String updateVote(@RequestParam(name = "direction", required = true) String direction, @RequestParam(name = "id", required = true) long id, Model model, OAuth2AuthenticationToken token) throws IOException {
        long voteCount = 0;

        List<SearchResultEntity> matchingResults = searchRepository.findById(id);
        if (!matchingResults.isEmpty()) {

            SearchResultEntity result = matchingResults.get(0);
            AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);

            List<UserVote> byUserAndResult = voteRepository.findByUserAndResult(user, result);

            if(byUserAndResult.size() > 0){
                UserVote toRemove = byUserAndResult.get(0);
                logger.debug("[VOTE DELETED] " + toRemove);
                voteRepository.delete(toRemove);
            }
            if(!(direction.equals("none"))){
                UserVote vote = new UserVote();
                vote.setUser(user);
                vote.setResult(result);
                vote.setTimestamp(new Date());
                logger.debug("[VOTE ADDED] " + vote);
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

    @GetMapping("/updateTags")
    @ResponseBody
    public String updateTags(@RequestParam(name = "tagName", required = true) String tagName, @RequestParam(name = "id", required = true) long id, Model model, OAuth2AuthenticationToken token) throws IOException {
        String message = "failed";
        List<SearchResultEntity> matchingResults = searchRepository.findById(id);
        if (!matchingResults.isEmpty()) {

            SearchResultEntity result = matchingResults.get(0);
            AppUser user = userRepository.findByUid(controllerAdvice.getUid(token)).get(0);
            List<Tag> tags = tagRepository.findByName(tagName);

            Tag tag = null;
            if(tags.size() > 0) {
                tag = tags.get(0);
            } else {
                tag = new Tag();
                tag.setName(tagName);
                tagRepository.save(tag);
            }

            List<ResultTag> byUserAndResultAndTag = resultTagRepository.findByUserAndResultAndTag(user, result, tag);

            if(byUserAndResultAndTag.size() > 0){
                ResultTag toRemove = byUserAndResultAndTag.get(0);
                resultTagRepository.delete(toRemove);
                message = "removed";
            } else {
                ResultTag resultTag = new ResultTag();
                resultTag.setTag(tag);
                resultTag.setUser(user);
                resultTag.setResult(result);
                resultTag.setTimestamp(new Date());
                resultTagRepository.save(resultTag);
                message = "added";
            }
        }

        return message; // returns the new vote total
    }

    // Removes whitespace from search terms //
    private String sanitizedSearchTerms(String searchTerms) 
    {
        return searchTerms.toLowerCase();
    }

    //Search the term in the table add if the term exist return true and if not return false
    private boolean doesSearchExist(String terms)
    {
        String cleanedStrings = sanitizedSearchTerms(terms);
        SearchTerms searchTerm = searchTermsRepository.findOneBySearchTerms(cleanedStrings);
        if (searchTerm == null)
        {
            return false; //If the terms have not been searched return false;
        }
        return true;
    }





    
}