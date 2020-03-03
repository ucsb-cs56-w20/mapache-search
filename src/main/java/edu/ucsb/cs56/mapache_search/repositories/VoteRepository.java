package edu.ucsb.cs56.mapache_search.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;

@Repository //Crud auto-configures everything, automatically know ur connected to PostgreSQL
//Driver-class-name in pom.xml tells u which db to use
public interface VoteRepository extends CrudRepository<UserVote, Long> {
    List<UserVote> findById(long id);
    List<UserVote> findByUpvoteOrderByTimestampDesc(boolean upvoted);
    List<UserVote> findByUserAndUpvote(AppUser user, boolean upvoted);
    List<UserVote> findByUserAndResult(AppUser user, SearchResultEntity result);
    List<UserVote> findByResult(SearchResultEntity result);
    List<UserVote> findByResultAndUpvote(SearchResultEntity result, boolean upvoted);
}