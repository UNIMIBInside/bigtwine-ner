package it.unimib.disco.bigtwine.services.ner.producers;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.services.ner.Recognizer;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringWriter;

public class RitterInputProducerTest {

    @Test
    public void testProduceSingle() throws Exception {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setRecognizer(Recognizer.ritter)
            .setWriter(writer)
            .build();

        BasicTweet tweet = new BasicTweet();
        tweet.setId("1");
        tweet.setText("prova");
        producer.append(tweet);

        producer.close();

        String output = writer.toString();

        assertEquals("1\tprova\r\n", output);
    }

    @Test
    public void testProduceMultiple() throws Exception {
        StringWriter writer = new StringWriter();
        InputProducer producer = InputProducerBuilder
            .getDefaultBuilder()
            .setRecognizer(Recognizer.ritter)
            .setWriter(writer)
            .build();

        BasicTweet tweet1 = new BasicTweet();
        tweet1.setId("1");
        tweet1.setText("prova1");
        BasicTweet tweet2 = new BasicTweet();
        tweet2.setId("2");
        tweet2.setText("prova2");
        producer.append(new BasicTweet[]{tweet1, tweet2});

        producer.close();

        String output = writer.toString();

        assertEquals("1\tprova1\r\n2\tprova2\r\n", output);
    }
}
