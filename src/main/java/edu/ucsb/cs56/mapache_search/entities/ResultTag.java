package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
public class ResultTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date timestamp;

    @ManyToOne
    private Tag tag;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private SearchResultEntity result;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    
    public SearchResultEntity getResult() { return result; }
    public void setResult(SearchResultEntity result) { this.result = result; }     

}