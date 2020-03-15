package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import java.util.Date;

@Entity
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
   

    private Date timestamp;
    private String url;
    private String userId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(AppUser user) { this.userId = user.getUid(); }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getUrl() {return url;}
    public void setUrl(String url){this.url = url;}
    


    @Override
    public String toString() {
        return "Link info [id=" + userId + ", timestamp=" + timestamp  + ", url=" + url  + "]";
    }

}