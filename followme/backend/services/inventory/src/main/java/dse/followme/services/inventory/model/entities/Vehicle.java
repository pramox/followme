package dse.followme.services.inventory.model.entities;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicle")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    private String vin;
    private String oem;
    private String modelType;
}
