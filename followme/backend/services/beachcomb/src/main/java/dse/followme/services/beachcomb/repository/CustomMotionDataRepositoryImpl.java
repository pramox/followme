package dse.followme.services.beachcomb.repository;

import dse.followme.services.beachcomb.model.entities.MotionData;
import dse.followme.services.beachcomb.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.geo.Point;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Custom repository implementation for querying motion data from the database.
 */
@Repository
public class CustomMotionDataRepositoryImpl implements CustomMotionDataRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Retrieves the most recent motion data for all vehicles.
     *
     * @return a list of the most recent motion data for all vehicles
     */
    @Override
    public List<MotionData> findAllVehiclesMostRecent() {
        final Aggregation aggregation = newAggregation(
                sort(Sort.Direction.DESC, "timestamp"),
                group("vin")
                        .first("vin").as("vin")
                        .first("position").as("position")
                        .first("role").as("role")
                        .first("lane").as("lane")
                        .first("speed").as("speed")
                        .first("timestamp").as("timestamp")
        );
        final AggregationResults<MotionData> result = mongoTemplate.aggregate(aggregation, "beachcomb", MotionData.class);
        return result.getMappedResults();
    }

    /**
     * Finds a nearby vehicle with an opposite role within a specified distance and time period.
     *
     * @param vin the vehicle identification number
     * @param maxDistanceKm the maximum distance in kilometers
     * @param maxTimePeriodMs the maximum time period in milliseconds
     * @return an optional containing the nearby vehicle's motion data if found
     */
    @Override
    public Optional<MotionData> findNearbyVehicleWithOppositeRole(String vin, double maxDistanceKm, long maxTimePeriodMs) {
        Optional<MotionData> optionalMotionData = findMostRecentMotionData(vin);

        if (optionalMotionData.isEmpty() ||
                optionalMotionData.get().getPosition() == null ||
                optionalMotionData.get().getTimestamp().getTime() < Instant.now().minusMillis(maxTimePeriodMs).toEpochMilli()
        ) return optionalMotionData;

        Role otherRole = optionalMotionData.get().getRole() == Role.FOLLOWING_VEHICLE ? Role.LEADING_VEHICLE : Role.FOLLOWING_VEHICLE;
        return findNearbyFollowingVehicleByRole(vin, otherRole, maxDistanceKm, maxTimePeriodMs);
    }

    /**
     * Finds the most recent motion data for a vehicle by its VIN.
     *
     * @param vin the vehicle identification number
     * @return an optional containing the vehicle's most recent motion data if found
     */
    private Optional<MotionData> findMostRecentMotionData(String vin) {
        Query query = new Query();
        query.addCriteria(Criteria.where("vin").is(vin));

        // Sort by timestamp in descending order and limit the result to 1
        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(1);

        return mongoTemplate.find(query, MotionData.class).stream().findFirst();
    }

    /**
     * Finds a nearby following vehicle by role within a specified distance and time period.
     *
     * @param vin the vehicle identification number
     * @param otherRole the role of the other vehicle
     * @param maxDistanceKm the maximum distance in kilometers
     * @param maxTimePeriodMs the maximum time period in milliseconds
     * @return an optional containing the nearby following vehicle's motion data if found
     */
    public Optional<MotionData> findNearbyFollowingVehicleByRole(String vin, Role otherRole, double maxDistanceKm, long maxTimePeriodMs) {

        // Step 1: Find the MotionData document by VIN to get the position
        MotionData motionData = findMostRecentMotionData(vin).orElseThrow();

        // Extract the position from the document
        GeoJsonPoint position = motionData.getPosition();
        double maxDistanceInRadians = maxDistanceKm / 6378.1;

        // Step 2: Perform the geoNear query using the extracted position
        Point location = new Point(position.getX(), position.getY());
        Date timestampWithTolerance = Date.from(Instant.now().minusMillis(maxTimePeriodMs));
        Aggregation aggregation = newAggregation(
                geoNear(NearQuery.near(location).maxDistance(maxDistanceInRadians).spherical(true), "distance"),
                match(Criteria
                        .where("vin").ne(vin)
                        .and("role").is(otherRole.name().toUpperCase())
                        .and("timestamp").gt(timestampWithTolerance)
                ),
                group("vin")
                        .max("timestamp").as("timestamp") // Take the first (i.e., most recent) timestamp within each group
                        .first("vin").as("vin")
                        .first("position").as("position")
                        .first("role").as("role")
                        .first("lane").as("lane")
                        .first("speed").as("speed")
                        .first("timestamp").as("timestamp"),

                sort(Sort.by(Sort.Direction.ASC, "distance")),
                limit(1)
        );

        AggregationResults<MotionData> result = mongoTemplate.aggregate(aggregation, "beachcomb", MotionData.class);

        if (result.getMappedResults().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.getMappedResults().get(0));
    }

}

