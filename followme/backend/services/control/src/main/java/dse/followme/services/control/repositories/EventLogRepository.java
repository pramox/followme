package dse.followme.services.control.repositories;

import dse.followme.services.control.entities.EventLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for CRUD operations on EventLog entities.
 */
public interface EventLogRepository extends MongoRepository<EventLog, String> {
}
