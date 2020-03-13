package edu.ucsb.cs56.mapache_search.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs56.mapache_search.entities.AppUser;

@Repository //Crud auto-configures everything, automatically know ur connected to PostgreSQL
//Driver-class-name in pom.xml tells u which db to use
public interface UserRepository extends CrudRepository<AppUser, Long> {
   List<AppUser> findByUid(String uid); //any such attribute will work, as long as it's associated with the getter/setter
   List<AppUser> findByUsername(String username); //any such attribute will work, as long as it's associated with the getter/setter
  
}