package dse.followme.services.inventory.mapper;

import dse.followme.services.inventory.model.dtos.VehicleDTO;
import dse.followme.services.inventory.model.entities.Vehicle;

public class VehicleMapper {
    public static Vehicle toEntity(VehicleDTO vehicleDTO){
        return new Vehicle(
                vehicleDTO.getVin(),
                vehicleDTO.getOem(),
                vehicleDTO.getModelType()
        );
    }

    public static VehicleDTO toDto(Vehicle vehicle){
        return new VehicleDTO(
                vehicle.getVin(),
                vehicle.getOem(),
                vehicle.getModelType()
        );
    }
}
