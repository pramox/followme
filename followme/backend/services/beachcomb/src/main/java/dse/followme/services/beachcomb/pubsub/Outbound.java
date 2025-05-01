package dse.followme.services.beachcomb.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration for outbound Pub/Sub message sending.
 */
@Configuration
@RequiredArgsConstructor
public class Outbound {
    private final Constants constants;

    /**
     * Creates a message handler for sending messages to a Pub/Sub topic.
     *
     * @param pubsubTemplate the Pub/Sub template for interacting with Google Cloud Pub/Sub
     * @return the configured PubSubMessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = "pubsubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        return new PubSubMessageHandler(pubsubTemplate, constants.getTopic());
    }

    /**
     * Messaging gateway for sending messages to Pub/Sub.
     */
    @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
    public interface PubsubOutboundGateway {
        void sendToPubsub(String text);
    }

}

