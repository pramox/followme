package dse.followme.services.inventory.controller;

import dse.followme.services.inventory.exceptions.VehicleNotFoundException;
import dse.followme.services.inventory.model.dtos.VehicleDTO;
import dse.followme.services.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller for handling inventory-related requests.
 */
@RestController
@RequestMapping("/inventory")
@AllArgsConstructor
@Slf4j
public class InventoryController {

    private InventoryService inventoryService;

    /**
     * Sets vehicle data.
     *
     * @param vehicleDTO the vehicle data transfer object containing vehicle information
     * @return ResponseEntity containing the saved VehicleDTO and HTTP status OK
     */
    @PostMapping
    public ResponseEntity<VehicleDTO> setVehicleData(@RequestBody VehicleDTO vehicleDTO) {
        log.info("POST /inventory received. Data: " + vehicleDTO);
        return new ResponseEntity<>(inventoryService.setVehicle(vehicleDTO), HttpStatus.OK);
    }

    /**
     * Retrieves vehicle data by VIN.
     *
     * @param vin the vehicle identification number
     * @return ResponseEntity containing the VehicleDTO and HTTP status OK
     * @throws VehicleNotFoundException if the vehicle is not found with the provided VIN
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> gsetVehicleData(@PathVariable("id") String vin) throws VehicleNotFoundException {
        log.info("GET /inventory/{} received", vin);
        VehicleDTO vehicleDTO = inventoryService.getVehicle(vin);

        if (vehicleDTO == null) {
            throw new VehicleNotFoundException("Vehicle not found with VIN: " + vin);
        }
        return new ResponseEntity<>(vehicleDTO, HttpStatus.OK);
    }

    /**
     * Retrieves all vehicles.
     *
     * @return ResponseEntity containing a list of VehicleDTOs and HTTP status OK
     */
    @GetMapping("")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        log.info("GET /inventory received");
        return new ResponseEntity<>(inventoryService.getAllVehicles(), HttpStatus.OK);
    }

}
