package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date timestamp;
    private boolean upvote;
    @ManyToOne
    private AppUser user;

    @ManyToOne
    private SearchResultEntity result;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }


    public boolean getUpvote() { return upvote; }
    public void setUpvote(boolean upvote) { this.upvote = upvote; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    
    public SearchResultEntity getResult() { return result; }
    public void setResult(SearchResultEntity result) { this.result = result; }

    @Override
    public String toString() {
        return "UserVote [id=" + id + ", result=" + result + ", timestamp=" + timestamp + ", upvote=" + upvote
                + ", user=" + user + "]";
    }

}