package dse.followme.services.inventory.service;

import dse.followme.services.inventory.exceptions.VehicleNotFoundException;
import dse.followme.services.inventory.mapper.VehicleMapper;
import dse.followme.services.inventory.model.dtos.VehicleDTO;
import dse.followme.services.inventory.model.entities.Vehicle;
import dse.followme.services.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public VehicleDTO getVehicle(String vin) throws VehicleNotFoundException {
        return inventoryRepository.findByVin(vin).map(VehicleMapper::toDto).orElseThrow(VehicleNotFoundException::new);
    }

    public VehicleDTO setVehicle(VehicleDTO vehicleDTO){
        if(inventoryRepository.findByVin(vehicleDTO.getVin()).isEmpty()){
            Vehicle vehicle = inventoryRepository.insert(VehicleMapper.toEntity(vehicleDTO));
            return VehicleMapper.toDto(vehicle);
        }
        return null;
    }

    public List<VehicleDTO> getAllVehicles() {
        return inventoryRepository.findAll()
                .stream()
                .map(VehicleMapper::toDto)
                .collect(Collectors.toList());
    }
}
