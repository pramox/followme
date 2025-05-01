package dse.followme.services.control.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the status of a follower vehicle.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowerStatusDTO {
    private Boolean isFollowMeMode;
    private String LeaderVin;
    private Integer requiredLane;
    private Double requiredSpeed;

    private Boolean error;
    private Integer actualLane;
    private Double actualSpeed;
}
