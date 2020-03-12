package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class SearchQueries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String uid;

    private Date timestamp;


    public String getUid() {return uid;}
    public void setUid(String uid) {this.uid = uid;}

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public Date getTimestamp() {return timestamp;}
    public void setTimestamp(Date timestamp) {this.timestamp = timestamp;}

};