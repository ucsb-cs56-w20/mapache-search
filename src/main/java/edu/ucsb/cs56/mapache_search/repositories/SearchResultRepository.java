package edu.ucsb.cs56.mapache_search.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;

@Repository
public interface SearchResultRepository extends CrudRepository<SearchResultEntity, Long> {
    List<SearchResultEntity> findByUrl(String url); //any such attribute will work, as long as it's associated with the getter/setter
    List<SearchResultEntity> findById(long id);
    
}