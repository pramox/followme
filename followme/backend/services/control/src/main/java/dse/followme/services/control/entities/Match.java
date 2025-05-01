package dse.followme.services.control.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a match between a leader and a follower vehicle.
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Document(collection = "match")
public class Match {

    @Id
    private String followerVin;
    @Column(unique = true)
    private String leaderVin;
    private LocalDateTime startTime;
    private Double requiredSpeed;
    private Integer requiredLane;
    private Double allowedSpeedDiscrepancy;
    private LocalDateTime unmatched;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match match)) return false;
        return Objects.equals(getLeaderVin(), match.getLeaderVin())
                && Objects.equals(getFollowerVin(), match.getFollowerVin())
                && Objects.equals(getRequiredSpeed(), match.getRequiredSpeed())
                && Objects.equals(getRequiredLane(), match.getRequiredLane())
                && Objects.equals(getAllowedSpeedDiscrepancy(), match.getAllowedSpeedDiscrepancy());
    }
}
