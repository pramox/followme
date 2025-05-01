package dse.followme.services.control.repositories;

import dse.followme.services.control.entities.Match;

import java.util.List;
import java.util.Optional;

/**
 * Custom Repository interface for CRUD operations on Match entities.
 */
public interface CustomMatchesRepository {

    /**
     * Finds a match by the VIN of the following vehicle.
     *
     * @param followingVin the VIN of the following vehicle
     * @return an Optional containing the match, or empty if not found
     */
    Optional<Match> findByFollowingVin(final String followingVin);

    /**
     * Updates or inserts a match.
     *
     * @param match the match to update or insert
     */
    void update(final Match match);

    /**
     * Finds all active matches where "unmatched" is null.
     *
     * @return a list of active matches
     */
    List<Match> findAllActiveMatches();
}
