package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
public class SearchQueries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private SearchTerms term;

    private Date timestamp;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public Date getTimestamp() {return timestamp;}
    public void setTimestamp(Date timestamp) {this.timestamp = timestamp;}

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public SearchTerms getTerm() {
        return term;
    }

    public void setTerm(SearchTerms term) {
        this.term = term;
    }

    

};