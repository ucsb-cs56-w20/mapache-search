package edu.ucsb.cs56.mapache_search.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs56.mapache_search.entities.Tag;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;

@Repository //Crud auto-configures everything, automatically know ur connected to PostgreSQL
//Driver-class-name in pom.xml tells u which db to use
public interface ResultTagRepository extends CrudRepository<ResultTag, Long> {
    List<ResultTag> findById(long id);
    List<ResultTag> findByUser(AppUser user);
    List<ResultTag> findByTag(Tag tag);
    List<ResultTag> findByUserAndResult(AppUser user, SearchResultEntity result);
    List<ResultTag> findByResult(SearchResultEntity result);
    
}