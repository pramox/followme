package dse.followme.services.beachcomb.repository;

import dse.followme.services.beachcomb.model.entities.MotionData;
import dse.followme.services.beachcomb.model.enums.Role;

import java.util.List;
import java.util.Optional;

public interface CustomMotionDataRepository {

    Optional<MotionData> findNearbyVehicleWithOppositeRole(String vin, double maxDistanceKm, long maxTimePeriodMs);
    Optional<MotionData> findNearbyFollowingVehicleByRole(String vin, Role otherRole, double maxDistanceKm, long maxTimePeriodMs);
    List<MotionData> findAllVehiclesMostRecent();
}
