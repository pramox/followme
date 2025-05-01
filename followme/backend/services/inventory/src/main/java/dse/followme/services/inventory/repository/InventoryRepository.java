package dse.followme.services.inventory.repository;

import dse.followme.services.inventory.model.dtos.VehicleDTO;
import dse.followme.services.inventory.model.entities.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Vehicle, String>{
    Optional<Vehicle> findByVin(String vin);
}
