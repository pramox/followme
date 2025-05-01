package dse.followme.services.control.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object (DTO) representing the status of a leader vehicle.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderStatusDTO {
    private Boolean isFollowMeMode;
    private String FollowerVin;

    private Boolean error;
    private Integer actualLane;
    private Double actualSpeed;
}