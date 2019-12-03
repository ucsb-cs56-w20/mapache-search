package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean upvote;
    // @EmbeddedId
    @ManyToOne
    // @JoinColumn(name = "id")
    private AppUser user;

    // @EmbeddedId
    @ManyToOne
    // @JoinColumn(name = "id")
    private SearchResultEntity result;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean getUpvote() { return upvote; }
    public void setUpvote(boolean upvote) { this.upvote = upvote; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    
    public SearchResultEntity getResult() { return result; }
    public void setResult(SearchResultEntity result) { this.result = result; }     

}