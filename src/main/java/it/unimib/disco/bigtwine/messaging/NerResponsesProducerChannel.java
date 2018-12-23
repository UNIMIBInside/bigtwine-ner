package it.unimib.disco.bigtwine.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface NerResponsesProducerChannel {
    String CHANNEL = "nerResponsesChannel";

    @Output
    MessageChannel nerResponsesChannel();
}
