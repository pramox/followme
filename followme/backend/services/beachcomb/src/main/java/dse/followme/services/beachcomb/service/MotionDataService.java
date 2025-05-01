package dse.followme.services.beachcomb.service;

import dse.followme.services.beachcomb.exceptions.VehicleNotFoundException;
import dse.followme.services.beachcomb.mapper.MotionDataMapper;
import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.model.entities.MotionData;
import dse.followme.services.beachcomb.model.enums.Role;
import dse.followme.services.beachcomb.repository.MotionDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing motion data of vehicles.
 */
@Service
@RequiredArgsConstructor
public class MotionDataService {
    private final MotionDataRepository motionDataRepository;
    private final MatchingService matchingService;

    /**
     * Sets the motion data for a vehicle and checks for matching opportunities if the vehicle is a leading vehicle.
     *
     * @param motionDataDto the motion data transfer object containing the vehicle's motion data
     * @return the saved motion data transfer object
     */
    public MotionDataDTO setMotionData(MotionDataDTO motionDataDto) {
        // Insert new data into DB
        MotionData motionData = motionDataRepository.insert(MotionDataMapper.toEntity(motionDataDto));

        // Check for Leading vehicles whether there is a following vehicle nearby
        if (motionDataDto.getRole().equals(Role.LEADING_VEHICLE)) {
            matchingService.checkForMatchOpportunity(motionDataDto, Role.FOLLOWING_VEHICLE);
        }

        return MotionDataMapper.toDto(motionData);
    }

    /**
     * Retrieves the motion data for a vehicle by its VIN.
     *
     * @param vin the vehicle identification number
     * @return the motion data transfer object of the vehicle
     * @throws VehicleNotFoundException if the vehicle with the given VIN is not found
     */
    public MotionDataDTO getMotionData(String vin) throws VehicleNotFoundException {
        return motionDataRepository
                .findById(vin).map(MotionDataMapper::toDto)
                .orElseThrow(VehicleNotFoundException::new);
    }

    /**
     * Retrieves the most recent motion data for all vehicles.
     *
     * @return a list of motion data transfer objects for all vehicles
     */
    public List<MotionDataDTO> getAllVehicles() {
        return motionDataRepository.findAllVehiclesMostRecent()
                .stream()
                .map(MotionDataMapper::toDto)
                .collect(Collectors.toList());
    }
}
