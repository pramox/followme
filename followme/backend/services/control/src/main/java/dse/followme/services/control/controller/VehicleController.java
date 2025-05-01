package dse.followme.services.control.controller;

import dse.followme.services.control.dtos.EventDTO;
import dse.followme.services.control.dtos.FollowerStatusDTO;
import dse.followme.services.control.dtos.LeaderStatusDTO;
import dse.followme.services.control.dtos.MatchDTO;
import dse.followme.services.control.dtos.MotionDataDTO;
import dse.followme.services.control.service.MatchesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST controller for handling vehicle-related requests.
 */
@RestController
@RequestMapping("/vehicle")
@AllArgsConstructor
@Slf4j
public class VehicleController {

    private final MatchesService matchesService;

    /**
     * Validates the position of a follower vehicle based on the provided motion data.
     *
     * @param vehicleUpdateDTO the motion data of the follower vehicle
     * @return ResponseEntity containing FollowerStatusDTO and HTTP status
     */
    @PostMapping("/following")
    public ResponseEntity<FollowerStatusDTO> validateFollowerPosition(@RequestBody MotionDataDTO vehicleUpdateDTO) {
        log.info("POST /vehicle/following with data: {}", vehicleUpdateDTO);
        final FollowerStatusDTO followerStatusDTO = matchesService.validateFollowerPosition(vehicleUpdateDTO);
        return new ResponseEntity<>(followerStatusDTO, HttpStatus.OK);
    }

    /**
     * Validates the position of a leader vehicle based on the provided motion data.
     *
     * @param vehicleUpdateDTO the motion data of the leader vehicle
     * @return ResponseEntity containing LeaderStatusDTO and HTTP status
     */
    @PostMapping("/leading")
    public ResponseEntity<LeaderStatusDTO> validateLeaderPosition(@RequestBody MotionDataDTO vehicleUpdateDTO) {
        log.info("POST /vehicle/leading with data: {}", vehicleUpdateDTO);
        final LeaderStatusDTO leaderStatusDTO = matchesService.validateLeaderPosition(vehicleUpdateDTO);
        return new ResponseEntity<>(leaderStatusDTO, HttpStatus.OK);
    }

    /**
     * Retrieves all match records.
     *
     * @return ResponseEntity containing a list of MatchDTO and HTTP status
     */
    @GetMapping
    public ResponseEntity<List<MatchDTO>> findAllMatches() {
        log.debug("GET /vehicle received");
        return new ResponseEntity<>(matchesService.findAllMatches(), HttpStatus.OK);
    }

    /**
     * Retrieves all Event records.
     *
     * @return ResponseEntity containing a list of EventDTO and HTTP status
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> findAllEvents() {
        log.info("GET /vehicle/events");
        return new ResponseEntity<>(matchesService.findAllEvents(), HttpStatus.OK);
    }
}
