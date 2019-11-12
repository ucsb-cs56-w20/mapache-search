package edu.ucsb.cs56.mapache_search;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ucsb.cs56.mapache_search.entities.User;
import edu.ucsb.cs56.mapache_search.search.SearchResult;

@Controller
public class SearchController {

    @Autowired   
    private SearchService searchService;

    @GetMapping("user/settings")
    public String UserSettings(Principal principal, Model model) {
        
        model.addAttribute("username", principal.getName());
        return "user/settings";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchObject", new SearchObject());
        return "index";
    }

    @GetMapping("/searchResults")
    public String search(
        @RequestParam(name = "query", required = true) 
        String query,
        Model model
        ) {
        model.addAttribute("query", query);

        String json = searchService.getJSON(query);

        SearchResult sr = SearchResult.fromJSON(json);
        model.addAttribute("searchResult", sr);
        
        return "searchResults"; // corresponds to src/main/resources/templates/searchResults.html
    }

}
