package dse.followme.services.control.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing motion data of a vehicle.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MotionDataDTO {
    private String vin;
    private Integer lane;
    private Double speed;
    private LocalDateTime timestamp;
}

