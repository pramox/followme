package dse.followme.services.beachcomb.model.dtos;

import dse.followme.services.beachcomb.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MotionDataDTO {
    private String vin;
    private Double latitude;
    private Double longitude;
    private Role role;
    private Integer lane;
    private Double speed;
    private Date timestamp;
}
