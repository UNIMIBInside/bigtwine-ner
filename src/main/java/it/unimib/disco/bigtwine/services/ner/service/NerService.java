package it.unimib.disco.bigtwine.services.ner.service;

import it.unimib.disco.bigtwine.commons.messaging.NerRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.NerResponseMessage;
import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.commons.models.Counter;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.ner.messaging.NerRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.ner.messaging.NerResponsesProducerChannel;
import it.unimib.disco.bigtwine.services.ner.Recognizer;
import it.unimib.disco.bigtwine.services.ner.processors.NerProcessor;
import it.unimib.disco.bigtwine.services.ner.processors.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class NerService implements ProcessorListener<RecognizedTweet> {

    private final Logger log = LoggerFactory.getLogger(NerService.class);

    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private KafkaTemplate<Integer, String> kafka;
    private Map<Recognizer, NerProcessor> processors = new HashMap<>();
    private Map<String, Counter<NerRequestMessage>> requests = new HashMap<>();

    public NerService(
        NerResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        KafkaTemplate<Integer, String> kafka) {
        this.channel = channel.nerResponsesChannel();
        this.processorFactory = processorFactory;
        this.kafka = kafka;
    }

    private Recognizer getRecognizer(String recognizerId) {
        if (recognizerId != null) {
            recognizerId = recognizerId.trim();
            if (recognizerId.equals("default")) {
                return Recognizer.getDefault();
            }else {
                try {
                    return Recognizer.valueOf(recognizerId);
                }catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }else {
            return Recognizer.getDefault();
        }
    }

    private NerProcessor getProcessor(Recognizer recognizer) {
        NerProcessor processor;
        if (this.processors.containsKey(recognizer)) {
            processor = this.processors.get(recognizer);
        }else {
            try {
                processor = this.processorFactory.getProcessor(recognizer);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(recognizer, processor);
            }else {
                System.err.println("NerProcessor not ready: " + processor.getRecognizer().toString());
                log.error("NerProcessor not ready: " + processor.getRecognizer().toString());
                return null;
            }
        }

        log.info("NerProcessor ready: " + processor.getClass().toString());

        return processor;
    }

    private String getNewRequestTag() {
        return UUID.randomUUID().toString();
    }

    private void processRequest(NerRequestMessage request) {
        Recognizer recognizer = this.getRecognizer(request.getRecognizer());

        if (recognizer == null) {
            return;
        }

        NerProcessor processor = this.getProcessor(recognizer);

        if (processor == null) {
            return;
        }

        String tag = this.getNewRequestTag();
        this.requests.put(tag, new Counter<>(request, request.getTweets().length));
        processor.process(tag, request.getTweets());
    }

    private void sendResponse(NerProcessor processor, String tag, RecognizedTweet[] tweets) {
        if (!this.requests.containsKey(tag)) {
            log.debug("Request tagged '" + tag + "' expired");
            return;
        }

        Counter<NerRequestMessage> requestCounter = this.requests.get(tag);
        requestCounter.decrement(tweets.length);
        NerRequestMessage request = requestCounter.get();
        if (!requestCounter.hasMore()) {
            this.requests.remove(tag);
        }

        NerResponseMessage response = new NerResponseMessage();
        response.setRecognizer(processor.getRecognizer().toString());
        response.setTweets(tweets);
        response.setRequestId(tag);

        MessageBuilder<NerResponseMessage> messageBuilder = MessageBuilder
            .withPayload(response);

        if (request.getOutputTopic() != null) {
            messageBuilder.setHeader(KafkaHeaders.TOPIC, request.getOutputTopic());
            this.kafka.send(messageBuilder.build());
        }else {
            this.channel.send(messageBuilder.build());
        }

        log.info("Request Processed: {}.", tag);
    }

    @StreamListener(NerRequestsConsumerChannel.CHANNEL)
    public void onNewRequest(NerRequestMessage request) {
        log.info("Request Received: {}.", request.getRequestId());
        this.processRequest(request);
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, RecognizedTweet[] tweets) {
        if (!(processor instanceof NerProcessor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((NerProcessor)processor, tag, tweets);
    }
}
