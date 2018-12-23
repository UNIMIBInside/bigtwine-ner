package it.unimib.disco.bigtwine.service;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.messaging.NerRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.NerResponseMessage;
import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.file.AsyncFileProcessor;
import it.unimib.disco.bigtwine.commons.processors.file.FileProcessor;
import it.unimib.disco.bigtwine.config.ApplicationProperties;
import it.unimib.disco.bigtwine.messaging.NerRequestsConsumerChannel;
import it.unimib.disco.bigtwine.messaging.NerResponsesProducerChannel;
import it.unimib.disco.bigtwine.ner.Recognizer;
import it.unimib.disco.bigtwine.ner.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.processors.Processor;
import it.unimib.disco.bigtwine.ner.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.ner.processors.ProcessorListener;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class NerService implements ProcessorListener {

    private final Logger log = LoggerFactory.getLogger(NerService.class);

    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private ExecutorFactory executorFactory;
    private Map<Recognizer, Processor> processors = new HashMap<>();
    private ApplicationProperties appProps;

    public NerService(
        NerResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        ExecutorFactory executorFactory,
        InputProducerBuilder inputProducerBuilder,
        OutputParserBuilder outputParserBuilder,
        ApplicationProperties appProps) {
        this.channel = channel.nerResponsesChannel();
        this.processorFactory = processorFactory;
        this.executorFactory = executorFactory;
        this.appProps = appProps;

        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                NerRequestMessage msg = new NerRequestMessage();
                msg.setRecognizer("ritter");
                msg.setRequestId("a123");
                BasicTweet tweet1 = new BasicTweet();
                tweet1.setId("123");
                tweet1.setText("prova");
                msg.setTweets(new BasicTweet[] {tweet1});
                processRequest(msg);
            }
        }, 2, TimeUnit.SECONDS);


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

    private Processor getProcessor(Recognizer recognizer) {
        Processor processor;
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
                System.err.println("Processor not ready: " + processor.getRecognizer().toString());
                log.error("Processor not ready: " + processor.getRecognizer().toString());
                return null;
            }
        }

        log.info("Processor ready: " + processor.getClass().toString());

        return processor;
    }

    private void processRequest(NerRequestMessage request) {
        Recognizer recognizer = this.getRecognizer(request.getRecognizer());

        if (recognizer == null) {
            return;
        }

        Processor processor = this.getProcessor(recognizer);

        if (processor == null) {
            return;
        }

        processor.process(request.getRequestId(), request.getTweets());
    }

    private void sendResponse(Processor processor, String tag, RecognizedTweet[] tweets) {
        for (RecognizedTweet tweet : tweets) {
            System.out.println("Recognize tweet: " + tweet.getId());
        }
        NerResponseMessage response = new NerResponseMessage();
        response.setRecognizer(processor.getRecognizer().toString());
        response.setTweets(tweets);
        response.setRequestId(tag);
        this.channel.send(MessageBuilder.withPayload(response).build());
        log.info("Request Processed: {}.", tag);
    }

    @StreamListener(NerRequestsConsumerChannel.CHANNEL)
    public void onNewRequest(NerRequestMessage request) {
        log.info("Request Received: {}.", request.getRequestId());
        this.processRequest(request);
    }

    @Override
    public void onProcessed(Processor processor, String tag, RecognizedTweet[] tweets) {
        this.sendResponse(processor, tag, tweets);
    }
}
