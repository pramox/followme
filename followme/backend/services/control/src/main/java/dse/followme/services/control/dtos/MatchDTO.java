package dse.followme.services.control.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * Data Transfer Object (DTO) representing a match between a leader and a follower vehicle.
 */
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class MatchDTO {
    private String leaderVin;
    private String followerVin;
    private Integer requiredLane;
    private Double requiredSpeed;
    private LocalDateTime startTime;
    private Double allowedSpeedDiscrepancy;
}
