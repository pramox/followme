package dse.followme.services.control.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Configuration properties for Pub/Sub integration.
 */
@ConfigurationProperties(prefix = "pubsub")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Constants {

    private String topic;
    private String subscription;
}
