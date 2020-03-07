package edu.ucsb.cs56.mapache_search.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs56.mapache_search.entities.Tag;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.SearchTerms;

@Repository
public interface SearchTermsRepository extends CrudRepository<SearchTerms, Long> {
    SearchTerms findOneBySearchTerms(String searchTerms);

    
};



