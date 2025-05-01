package dse.followme.services.beachcomb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dse.followme.services.beachcomb.mapper.MotionDataMapper;
import dse.followme.services.beachcomb.model.dtos.MatchDTO;
import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.model.entities.MotionData;
import dse.followme.services.beachcomb.model.enums.Role;
import dse.followme.services.beachcomb.pubsub.Outbound;
import dse.followme.services.beachcomb.repository.MotionDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for matching vehicles based on their motion data.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    @Value("${properties.matching.matching-distance-tolerance-km}")
    private double matchingDistanceTolerance;

    @Value("${properties.matching.matching-time-tolerance-ms}")
    private long matchingTimeTolerance;

    @Value("${properties.matching.following-vehicle-speed-discrepancy-kmh}")
    private double followingVehicleSpeedDiscrepancy;

    @Value("${properties.matching.following-vehicle-time-discrepancy-ms}")
    private long followingVehicleTimeDiscrepancy;

    private final MotionDataRepository motionDataRepository;
    private final Outbound.PubsubOutboundGateway messagingGateway;

    /**
     * Checks for a match opportunity between the given motion data and nearby vehicles with the specified role.
     *
     * @param motionDataDTO the motion data to check for matching opportunities
     * @param aimedRole the role of the vehicle to match with
     */
    public void checkForMatchOpportunity(MotionDataDTO motionDataDTO, Role aimedRole) {
        Optional<MotionData> nearbyVehicle = motionDataRepository.findNearbyFollowingVehicleByRole(
                motionDataDTO.getVin(),
                aimedRole,
                matchingDistanceTolerance,
                matchingTimeTolerance
        );
        if (nearbyVehicle.isEmpty()) {
            return;
        }

        MotionDataDTO lead = motionDataDTO.getRole() == Role.LEADING_VEHICLE ? motionDataDTO : MotionDataMapper.toDto(nearbyVehicle.get());
        MotionDataDTO follow = motionDataDTO.getRole() == Role.FOLLOWING_VEHICLE ? motionDataDTO : MotionDataMapper.toDto(nearbyVehicle.get());
        publishMatch(createMatch(lead, follow));
    }

    /**
     * Publishes the match information to the messaging gateway.
     *
     * @param matchDTO the match data transfer object to publish
     */
    private void publishMatch(MatchDTO matchDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            String matchDtoJson = mapper.writeValueAsString(matchDTO);
            log.info("PUBLISH pubsub data: {}", matchDtoJson);
            messagingGateway.sendToPubsub(matchDtoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a match data transfer object from the lead and follow vehicle data.
     *
     * @param lead the lead vehicle data transfer object
     * @param follow the follow vehicle data transfer object
     * @return the match data transfer object
     */
    private MatchDTO createMatch(MotionDataDTO lead, MotionDataDTO follow) {
        return new MatchDTO(
                lead.getVin(),
                follow.getVin(),
                lead.getLane(),
                lead.getSpeed(),
                LocalDateTime.now(),
                followingVehicleSpeedDiscrepancy,
                followingVehicleTimeDiscrepancy
        );
    }
}
