package dse.followme.services.beachcomb.controller;

import dse.followme.services.beachcomb.exceptions.VehicleNotFoundException;
import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.service.MotionDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/beachcomb")
@AllArgsConstructor
@Slf4j
public class BeachcombController {

    private MotionDataService motionDataService;

    /**
     * Handles POST requests to set motion data.
     *
     * @param motionDataDTO the motion data to be set
     * @return ResponseEntity containing the saved MotionDataDTO and HTTP status
     */
    @PostMapping
    public ResponseEntity<MotionDataDTO> setMotionData(@RequestBody MotionDataDTO motionDataDTO) {
        log.info("POST /beachcomb received. Data: " + motionDataDTO);
        return new ResponseEntity<>(motionDataService.setMotionData(motionDataDTO), HttpStatus.OK);
    }

    /**
     * Handles GET requests to retrieve motion data by vehicle identification number (VIN).
     *
     * @param vin the vehicle identification number
     * @return ResponseEntity containing the MotionDataDTO and HTTP status
     * @throws VehicleNotFoundException if the vehicle is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MotionDataDTO> getMotionData(@PathVariable("id") String vin) throws VehicleNotFoundException {
        log.info("GET /beachcomb/{} received", vin);
        return new ResponseEntity<>(motionDataService.getMotionData(vin), HttpStatus.OK);
    }

    /**
     * Handles GET requests to retrieve motion data for all vehicles.
     *
     * @return ResponseEntity containing a list of MotionDataDTO and HTTP status
     */
    @GetMapping("")
    public ResponseEntity<List<MotionDataDTO>> getAllVehicles() {
        log.debug("GET /beachcomb received");
        return new ResponseEntity<>(motionDataService.getAllVehicles(), HttpStatus.OK);
    }
}
