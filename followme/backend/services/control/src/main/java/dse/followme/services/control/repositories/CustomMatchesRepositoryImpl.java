package dse.followme.services.control.repositories;

import dse.followme.services.control.entities.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of custom repository methods for Match entities.
 */
@Repository
@RequiredArgsConstructor
public class CustomMatchesRepositoryImpl implements CustomMatchesRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * Finds a match by the VIN of the following vehicle.
     *
     * @param followingVin the VIN of the following vehicle
     * @return an Optional containing the match, or empty if not found
     */
    @Override
    public Optional<Match> findByFollowingVin(final String followingVin) {
        Query query = new Query();
        query.addCriteria(Criteria.where("followerVin").is(followingVin));
        return mongoTemplate.find(query, Match.class).stream().findFirst();
    }

    /**
     * Updates or inserts a match.
     *
     * @param match the match to update or insert
     */
    @Override
    public void update(final Match match) {
        Query query = new Query();
        query.addCriteria(Criteria.where("followerVin").is(match.getFollowerVin()));

        Match existingMatch = mongoTemplate.findOne(query, Match.class);
        if (existingMatch != null) {
            Update update = new Update();
            update.set("leaderVin", match.getLeaderVin());
            update.set("startTime", match.getStartTime());
            update.set("allowedSpeedDiscrepancy", match.getAllowedSpeedDiscrepancy());
            update.set("requiredLane", match.getRequiredLane());
            update.set("requiredSpeed", match.getRequiredSpeed());
            update.set("unmatched", match.getUnmatched());
            mongoTemplate.updateFirst(query, update, Match.class);
        } else {
            mongoTemplate.insert(match);
        }
    }

    /**
     * Finds all active matches where "unmatched" is null.
     *
     * @return a list of active matches
     */
    @Override
    public List<Match> findAllActiveMatches() {
        Query query = new Query();
        query.addCriteria(Criteria.where("unmatched").is(null));
        return mongoTemplate.find(query, Match.class);
    }
}
