package dse.followme.services.control;

import dse.followme.services.control.dtos.FollowerStatusDTO;
import dse.followme.services.control.dtos.MotionDataDTO;
import dse.followme.services.control.entities.Match;
import dse.followme.services.control.repositories.MatchesRepository;
import dse.followme.services.control.service.MatchesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchesServiceTest {

    @Mock
    private MatchesRepository matchesRepository;

    @InjectMocks
    private MatchesService controlService;

    private Match match;
    private MotionDataDTO vehicleUpdateDTO;

    @BeforeEach
    void setUp() {
        match = new Match();
        match.setLeaderVin("leaderVin");
        match.setFollowerVin("followerVin");
        match.setStartTime(LocalDateTime.now().minusMinutes(1));
        match.setRequiredSpeed(60.0);
        match.setRequiredLane(1);
        match.setAllowedSpeedDiscrepancy(5.0);

        vehicleUpdateDTO = new MotionDataDTO();
        vehicleUpdateDTO.setVin("followerVin");
        vehicleUpdateDTO.setLane(1);
        vehicleUpdateDTO.setSpeed(60.0);
        vehicleUpdateDTO.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testValidateFollowerPosition_NoMatch() {
        when(matchesRepository.findByFollowingVin("followerVin")).thenReturn(Optional.empty());

        FollowerStatusDTO status = controlService.validateFollowerPosition(vehicleUpdateDTO);

        assertFalse(status.getIsFollowMeMode());
        assertNull(status.getRequiredLane());
        assertNull(status.getRequiredSpeed());
        assertNull(status.getLeaderVin());
        assertFalse(status.getError());
    }

    @Test
    void testValidateFollowerPosition_MatchWithinTimeDiscrepancy() {
        when(matchesRepository.findByFollowingVin("followerVin")).thenReturn(Optional.of(match));

        vehicleUpdateDTO.setTimestamp(match.getStartTime().plusSeconds(30));

        FollowerStatusDTO status = controlService.validateFollowerPosition(vehicleUpdateDTO);

        assertTrue(status.getIsFollowMeMode());
        assertEquals(match.getRequiredLane(), status.getRequiredLane());
        assertEquals(match.getRequiredSpeed(), status.getRequiredSpeed());
        assertEquals(match.getLeaderVin(), status.getLeaderVin());
        assertFalse(status.getError());
    }

    @Test
    void testValidateFollowerPosition_MatchOutsideTimeDiscrepancy() {
        when(matchesRepository.findByFollowingVin("followerVin")).thenReturn(Optional.of(match));

        vehicleUpdateDTO.setTimestamp(match.getStartTime().plusMinutes(2));
        vehicleUpdateDTO.setSpeed(56.);

        FollowerStatusDTO status = controlService.validateFollowerPosition(vehicleUpdateDTO);

        assertTrue(status.getIsFollowMeMode());
        assertEquals(match.getRequiredLane(), status.getRequiredLane());
        assertEquals(match.getRequiredSpeed(), status.getRequiredSpeed());
        assertEquals(match.getLeaderVin(), status.getLeaderVin());
        assertFalse(status.getError());
    }

    @Test
    void testValidateFollowerPosition_LaneMismatch() {
        when(matchesRepository.findByFollowingVin("followerVin")).thenReturn(Optional.of(match));

        vehicleUpdateDTO.setLane(2);

        FollowerStatusDTO status = controlService.validateFollowerPosition(vehicleUpdateDTO);

        assertFalse(status.getIsFollowMeMode());
        assertEquals(match.getRequiredLane(), status.getRequiredLane());
        assertEquals(match.getRequiredSpeed(), status.getRequiredSpeed());
        assertEquals(match.getLeaderVin(), status.getLeaderVin());
        assertTrue(status.getError());
        assertEquals(vehicleUpdateDTO.getLane(), status.getActualLane());
    }

    @Test
    void testValidateFollowerPosition_SpeedMismatch() {
        when(matchesRepository.findByFollowingVin("followerVin")).thenReturn(Optional.of(match));

        vehicleUpdateDTO.setSpeed(65.6);

        FollowerStatusDTO status = controlService.validateFollowerPosition(vehicleUpdateDTO);

        assertFalse(status.getIsFollowMeMode());
        assertEquals(match.getRequiredLane(), status.getRequiredLane());
        assertEquals(match.getRequiredSpeed(), status.getRequiredSpeed());
        assertEquals(match.getLeaderVin(), status.getLeaderVin());
        assertTrue(status.getError());
        assertEquals(vehicleUpdateDTO.getSpeed(), status.getActualSpeed());
    }

    @Test
    void testValidateFollowerPosition_LaneAndSpeedMismatch() {
        when(matchesRepository.findByFollowingVin("followerVin")).thenReturn(Optional.of(match));

        vehicleUpdateDTO.setLane(2);
        vehicleUpdateDTO.setSpeed(70.0);

        FollowerStatusDTO status = controlService.validateFollowerPosition(vehicleUpdateDTO);

        assertFalse(status.getIsFollowMeMode());
        assertEquals(match.getRequiredLane(), status.getRequiredLane());
        assertEquals(match.getRequiredSpeed(), status.getRequiredSpeed());
        assertEquals(match.getLeaderVin(), status.getLeaderVin());
        assertTrue(status.getError());
        assertEquals(vehicleUpdateDTO.getLane(), status.getActualLane());
        assertEquals(vehicleUpdateDTO.getSpeed(), status.getActualSpeed());
    }
}