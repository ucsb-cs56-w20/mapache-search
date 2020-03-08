package edu.ucsb.cs56.mapache_search.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs56.mapache_search.entities.Tag;

@Repository //Crud auto-configures everything, automatically know ur connected to PostgreSQL
//Driver-class-name in pom.xml tells u which db to use
public interface TagRepository extends CrudRepository<Tag, Long> {
    List<Tag> findById(long id);


    List<Tag> findByName(String tag);
    
}