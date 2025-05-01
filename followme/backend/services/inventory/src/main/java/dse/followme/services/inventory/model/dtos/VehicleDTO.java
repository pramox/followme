package dse.followme.services.inventory.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VehicleDTO {
    private String vin;
    private String oem;
    private String modelType;
}
