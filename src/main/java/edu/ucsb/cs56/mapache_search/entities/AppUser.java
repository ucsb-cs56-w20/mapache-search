package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.validation.constraints.NotBlank;

@Entity //Need this or error: Not a managed type
public class AppUser { //Can't use User since User is a defined word in DB

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String apikey;
    private String uid;
    private String searchHistory [];  

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getApikey() { return apikey; }
    public void setApikey(String apikey) { this.apikey = apikey; }
   
    public String getUid() { return uid; }
    public void setUid (String uid) { this.uid = uid; }

    public String [] getSearchHistory() { return this.searchHistory; }
    public void setSearchHistory(String [] searchHistory) { 
        this.searchHistory = searchHistory;   
    }
    
}
