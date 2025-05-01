package dse.followme.services.control.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration class for setting up outbound Pub/Sub message handling.
 */
@Configuration
@RequiredArgsConstructor
public class Outbound {
    private final Constants constants;

    /**
     * Creates a {@link MessageHandler} to send messages to Pub/Sub.
     *
     * @param pubsubTemplate the Pub/Sub template
     * @return a configured MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = "pubsubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        return new PubSubMessageHandler(pubsubTemplate, constants.getTopic());
    }

    /**
     * Defines a messaging gateway for outbound Pub/Sub messages.
     */
    @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
    public interface PubsubOutboundGateway {
        void sendToPubsub(String text);
    }

}

