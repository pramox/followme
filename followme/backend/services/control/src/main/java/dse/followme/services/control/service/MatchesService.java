package dse.followme.services.control.service;

import dse.followme.services.control.dtos.EventDTO;
import dse.followme.services.control.dtos.FollowerStatusDTO;
import dse.followme.services.control.dtos.LeaderStatusDTO;
import dse.followme.services.control.dtos.MatchDTO;
import dse.followme.services.control.dtos.MotionDataDTO;
import dse.followme.services.control.entities.EventLog;
import dse.followme.services.control.entities.Match;
import dse.followme.services.control.repositories.EventLogRepository;
import dse.followme.services.control.repositories.MatchesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing matches between vehicles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchesService {

    private final MatchesRepository matchesRepository;
    private final EventLogRepository eventLogRepository;

    @Value("${properties.matching.following-vehicle-time-discrepancy-ms}")
    private long followingVehicleTimeDiscrepancy;


    /**
     * Updates or creates a match based on the provided MatchDTO.
     *
     * @param matchDTO the MatchDTO containing match details
     */
    public void updateOrCreateMatch(MatchDTO matchDTO) {
        log.info("Updating or creating match: {}", matchDTO);
        final Optional<Match> matchOpt = matchesRepository.findByFollowingVin(matchDTO.getFollowerVin());
        if (matchOpt.isEmpty()) {
            final Match match = new Match();
            match.setFollowerVin(matchDTO.getFollowerVin());
            match.setLeaderVin(matchDTO.getLeaderVin());
            match.setRequiredLane(matchDTO.getRequiredLane());
            match.setStartTime(matchDTO.getStartTime());
            match.setRequiredSpeed(matchDTO.getRequiredSpeed());
            match.setAllowedSpeedDiscrepancy(matchDTO.getAllowedSpeedDiscrepancy());
            match.setUnmatched(null);
            matchesRepository.insert(match);

            EventLog eventLog = new EventLog(UUID.randomUUID().toString(),
                    "FollowMe Modus gestartet FV: " + matchDTO.getFollowerVin() + " LV: " + matchDTO.getLeaderVin(),
                    LocalDateTime.now());
            eventLogRepository.insert(eventLog);
            return;
        }
        final Match match = matchOpt.get();
        log.info("Match found: {}", match);
        if (matchDTOEqualsMatch(matchDTO, match) || recentlyUnmatched(match)) {
            return;
        }
        match.setLeaderVin(matchDTO.getLeaderVin());
        match.setStartTime(matchDTO.getStartTime());
        match.setRequiredLane(matchDTO.getRequiredLane());
        match.setRequiredSpeed(matchDTO.getRequiredSpeed());
        match.setAllowedSpeedDiscrepancy(matchDTO.getAllowedSpeedDiscrepancy());
        match.setUnmatched(null);
        matchesRepository.update(match);
    }

    private boolean recentlyUnmatched(final Match match) {
        if (match.getUnmatched() == null) {
            return false;
        }
        return match.getUnmatched().plusMinutes(5).isAfter(LocalDateTime.now());
    }

    /**
     * Validates the follower's position based on the provided MotionDataDTO.
     *
     * @param vehicleUpdateDTO the MotionDataDTO containing vehicle update details
     * @return a FollowerStatusDTO indicating the status of the follower
     */
    public FollowerStatusDTO validateFollowerPosition(MotionDataDTO vehicleUpdateDTO) {
        Optional<Match> matchOpt = matchesRepository.findByFollowingVin(vehicleUpdateDTO.getVin());
        if (matchOpt.isEmpty()) {
            return new FollowerStatusDTO(false, null, null, null, false, null, null);
        }
        final Match match = matchOpt.get();
        log.info("Validating match: {}", match);
        final FollowerStatusDTO followerStatus = new FollowerStatusDTO();

        followerStatus.setIsFollowMeMode(true);
        followerStatus.setRequiredLane(match.getRequiredLane());
        followerStatus.setRequiredSpeed(match.getRequiredSpeed());
        followerStatus.setLeaderVin(match.getLeaderVin());
        followerStatus.setError(false);

        if (match.getStartTime().plus(followingVehicleTimeDiscrepancy, ChronoUnit.MILLIS).isAfter(vehicleUpdateDTO.getTimestamp().minusHours(2))) {
            return followerStatus;
        }

        if (vehicleUpdateDTO.getLane() == null || !vehicleUpdateDTO.getLane().equals(match.getRequiredLane())) {
            followerStatus.setError(true);
            followerStatus.setActualLane(vehicleUpdateDTO.getLane());
        }
        if (vehicleUpdateDTO.getSpeed() == null || Math.abs(vehicleUpdateDTO.getSpeed() - match.getRequiredSpeed()) > match.getAllowedSpeedDiscrepancy()) {
            followerStatus.setError(true);
            followerStatus.setActualSpeed(vehicleUpdateDTO.getSpeed());
        }
        if (followerStatus.getError()) {
            if (match.getUnmatched() == null) {
                EventLog e = new EventLog(UUID.randomUUID().toString(),
                        "FV: " + vehicleUpdateDTO.getVin() + " falsche Anpassung an LV: " + match.getLeaderVin()
                                + " Soll Wert: Lane: " + match.getRequiredLane() + " Speed: " + match.getRequiredSpeed()
                                + " Echter Wert: Lane: " + vehicleUpdateDTO.getLane() + " Speed " + vehicleUpdateDTO.getSpeed()
                        , LocalDateTime.now());
                eventLogRepository.insert(e);
            }
            match.setUnmatched(LocalDateTime.now());
            followerStatus.setIsFollowMeMode(false);
            matchesRepository.update(match);
        }
        return followerStatus;
    }

    /**
     * Validates the leader's position based on the provided MotionDataDTO.
     *
     * @param vehicleUpdateDTO the MotionDataDTO containing vehicle update details
     * @return a LeaderStatusDTO indicating the status of the leader
     */
    public LeaderStatusDTO validateLeaderPosition(MotionDataDTO vehicleUpdateDTO) {
        // check if leading vehicle is currently in a match
        Optional<Match> matchOpt = matchesRepository.findByFollowingVin(vehicleUpdateDTO.getVin());
        if (matchOpt.isEmpty()) {
            return new LeaderStatusDTO(false, null, false, null, null);
        }
        Match match = matchOpt.get();
        return new LeaderStatusDTO(
                true,
                match.getFollowerVin(),
                false,
                vehicleUpdateDTO.getLane(),
                vehicleUpdateDTO.getSpeed()
        );
    }

    /**
     * Retrieves all active matches.
     *
     * @return a list of MatchDTO representing all matches
     */
    public List<MatchDTO> findAllMatches() {
        return matchesRepository.findAllActiveMatches().stream().map(match -> new MatchDTO(
                match.getLeaderVin(),
                match.getFollowerVin(),
                match.getRequiredLane(),
                match.getRequiredSpeed(),
                match.getStartTime(),
                match.getAllowedSpeedDiscrepancy())).collect(Collectors.toList());
    }

    private boolean matchDTOEqualsMatch(final MatchDTO matchDTO, final Match match) {
        if (matchDTO == null && match == null) {
            return true;
        }
        if (matchDTO == null || match == null) {
            return false;
        }
        return Objects.equals(matchDTO.getLeaderVin(), match.getLeaderVin())
                && Objects.equals(matchDTO.getFollowerVin(), match.getFollowerVin())
                && Objects.equals(matchDTO.getRequiredSpeed(), match.getRequiredSpeed())
                && Objects.equals(matchDTO.getRequiredLane(), match.getRequiredLane())
                && Objects.equals(matchDTO.getAllowedSpeedDiscrepancy(), match.getAllowedSpeedDiscrepancy());
    }

    public List<EventDTO> findAllEvents() {
        return eventLogRepository.findAll().stream().map(event -> new EventDTO(event.getEvent(), event.getTimestamp())).collect(Collectors.toList());
    }
}

