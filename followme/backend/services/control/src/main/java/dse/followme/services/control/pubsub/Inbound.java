package dse.followme.services.control.pubsub;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import dse.followme.services.control.dtos.MatchDTO;
import dse.followme.services.control.service.MatchesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration class for setting up inbound Pub/Sub message handling.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class Inbound {
    private final Constants constants;
    private final MatchesService matchesService;
    private final ObjectMapper objectMapper;

    /**
     * Creates a {@link PubSubInboundChannelAdapter} to receive messages from the Pub/Sub subscription.
     *
     * @param inputChannel  the channel to send messages to
     * @param pubSubTemplate the Pub/Sub template
     * @return a configured PubSubInboundChannelAdapter
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
     * Creates a {@link MessageChannel} for Pub/Sub input.
     *
     * @return a new DirectChannel
     */
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    /**
     * Creates a {@link MessageHandler} to process received messages.
     *
     * @return a configured MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE,
                            BasicAcknowledgeablePubsubMessage.class);
            if (originalMessage != null) {
                try {
                    String jsonPayload = String.valueOf(message.getPayload());
                    MatchDTO matchDTO = objectMapper.readValue(jsonPayload, MatchDTO.class);
                    log.info("RECEIVED pubsub data: {}", matchDTO);
                    originalMessage.ack();
                    matchesService.updateOrCreateMatch(matchDTO);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}