package dse.followme.services.beachcomb.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "pubsub")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Constants {

    private String topic;
    private String subscription;

}
