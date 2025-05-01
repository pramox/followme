package dse.followme.services.control.repositories;

import dse.followme.services.control.entities.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for CRUD operations on Match entities.
 */
public interface MatchesRepository extends MongoRepository<Match, String>, CustomMatchesRepository {

}
