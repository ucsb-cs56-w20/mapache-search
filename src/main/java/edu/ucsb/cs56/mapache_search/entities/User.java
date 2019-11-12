package edu.ucsb.cs56.mapache_search.user;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

public class User {

    @NotBlank(message = "Google search API Key is necessary")
    private String apiKey;

    @NotBlank(message = "Last Name is mandatory")
    private String username;

    public User(String username, String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAPIKey() {
        return this.apiKey;
    }
}
