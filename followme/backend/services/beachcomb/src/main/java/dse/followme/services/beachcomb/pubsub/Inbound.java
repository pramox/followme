package dse.followme.services.beachcomb.pubsub;


import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import lombok.RequiredArgsConstructor;

/**
 * Configuration for inbound Pub/Sub message processing.
 */
@Configuration
@RequiredArgsConstructor
public class Inbound {
    private final Constants constants;

    /**
     * Creates a PubSubInboundChannelAdapter that subscribes to a Pub/Sub subscription and forwards messages
     * to the specified input channel.
     *
     * @param inputChannel the channel to which messages will be forwarded
     * @param pubSubTemplate the Pub/Sub template for interacting with Google Cloud Pub/Sub
     * @return the configured PubSubInboundChannelAdapter
     */
    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, constants.getSubscription());
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL); // is it necessary to acknowledge the msg manually after processing?
        adapter.setPayloadType(String.class);
        return adapter;
    }

    /**
     * Defines a direct channel for Pub/Sub input messages.
     *
     * @return the configured MessageChannel
     */
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    /**
     * Creates a message handler that acknowledges received Pub/Sub messages.
     *
     * @return the configured MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE,
                            BasicAcknowledgeablePubsubMessage.class);
            originalMessage.ack();
        };
    }

}