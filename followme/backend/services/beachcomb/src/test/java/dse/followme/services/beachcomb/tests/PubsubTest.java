package dse.followme.services.beachcomb.tests;


import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.pubsub.Inbound;
import dse.followme.services.beachcomb.pubsub.Outbound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.Charset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PubsubTest {
    @InjectMocks
    private Outbound.PubsubOutboundGateway pubsubOutboundGateway;

    @Mock
    private PubSubTemplate pubSubTemplate;

    @Mock
    private PubSubInboundChannelAdapter inbound;

    @Test
    void publishingMsgIsPossible() {
        final String msg = "Pyrotechnik ist ja kein Verbrechen!";

        // Mock the messageReceiver behavior
        doAnswer(invocation -> {
            BasicAcknowledgeablePubsubMessage message = mock(BasicAcknowledgeablePubsubMessage.class);
            when(message).thenReturn(message);
            return null;
        });

        // Mock the PubSubMessageHandler to simulate sending a message
        PubSubMessageHandler messageHandler = new PubSubMessageHandler(pubSubTemplate, "topic-two");
        messageHandler.setSuccessCallback((ackId, message) ->
                System.out.println("Message was sent via the outbound channel adapter to topic-two!"));
        messageHandler.setFailureCallback((cause, message) ->
                System.out.println("Error sending " + message + " due to " + cause));
        when(pubSubTemplate.publish(any(String.class), any(String.class))).thenReturn(null);

        pubsubOutboundGateway.sendToPubsub(msg);

        // Verify that the message was sent
        verify(pubSubTemplate, times(1)).publish(eq("topic-two"), eq(msg));
    }
}
