package dse.followme.services.beachcomb.repository;

import dse.followme.services.beachcomb.model.entities.MotionData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MotionDataRepository extends MongoRepository<MotionData, String>, CustomMotionDataRepository {

}