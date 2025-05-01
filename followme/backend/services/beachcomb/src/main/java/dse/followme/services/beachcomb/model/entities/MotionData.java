package dse.followme.services.beachcomb.model.entities;

import dse.followme.services.beachcomb.model.enums.Role;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "beachcomb")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MotionData {
    @Id
    private String vin;
    private GeoJsonPoint position;
    private Role role;
    private Integer lane; // ELEM OF 1,2,3
    private Double speed;
    private Date timestamp;
}
