package dse.followme.services.beachcomb.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MatchDTO {
    private String leaderVin;
    private String followerVin;
    private Integer requiredLane;
    private Double requiredSpeed;
    private LocalDateTime startTime;
    private Double allowedSpeedDiscrepancy;

    /**
     * In ms - this field describes how long a following vehicle has to perform the required actions,
     * such as switch lane or change speed.
     */
    private Long timeDiscrepancy;
}
