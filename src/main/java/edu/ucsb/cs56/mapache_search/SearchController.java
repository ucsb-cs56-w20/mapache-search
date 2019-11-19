package edu.ucsb.cs56.mapache_search;

import java.sql.Statement;
import java.util.ArrayList;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ucsb.cs56.mapache_search.entities.User;
import edu.ucsb.cs56.mapache_search.search.SearchResult;

@Controller
public class SearchController {

    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired   
    private SearchService searchService;

    @GetMapping("user/settings")
    public String UserSettings(Principal principal, Model model) {
        
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "SELECT * FROM cuser WHERE username = " + "'" + principal.getName() + "';";
            ResultSet rs = stmt.executeQuery(sql);
            if (!rs.next()) {
                sql = "INSERT INTO cuser (username, apiKey) VALUES (" + principal.getName() + "', 'NULL');";
                model.addAttribute("username", principal.getName());
                model.addAttribute("apiKey", "STUB");
                return "user/settings";
            }
            model.addAttribute("username", rs.getString("username"));
            model.addAttribute("apiKey", rs.getString("apiKey"));
            return "user/settings";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @PostMapping("user/settings/update")
    public String UpdateSettings(@ModelAttribute User user, Model model, Principal principal) {
        try {
        Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        String sql = "UPDATE cuser SET apiKey = '" + user.getAPIKey() + "' WHERE username = " + "'" + principal.getName() + "';";
        ResultSet rs = stmt.executeQuery(sql);
        if (!rs.next()) {
            return "MASSIVE ERROR";
        }
        } catch (Exception e) {
            return e.toString();
        }
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

    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = null;
        if(System.getenv("DATABASE_URL") != null) {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        }else {
            String DATABASE_URL = "postgres://ubuntu:ubuntu@localhost:5432/userdb";
            dbUri = new URI(DATABASE_URL);
        }

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                + dbUri.getPort() + dbUri.getPath()
                + "?sslmode=require";
        /*Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/userdb?sslmode=require",
                "ubuntu",
                "ubuntu");*/
		return DriverManager.getConnection(dbUrl, username, password);
	}

}
