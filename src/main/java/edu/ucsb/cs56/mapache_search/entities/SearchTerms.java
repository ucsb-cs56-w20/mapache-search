package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class SearchTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String searchTerms;
    private Date timestamp;
    private int count;

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getSearchTerms() {return searchTerms;}
    public void setSearchTerms(String searchTerms) {this.searchTerms = searchTerms;}

    public Date getTimestamp() {return timestamp;}
    public void setTimestamp(Date timestamp) {this.timestamp = timestamp;}

    public int getCount() {return count;}
    public void setCount(int count) {this.count = count;}

};