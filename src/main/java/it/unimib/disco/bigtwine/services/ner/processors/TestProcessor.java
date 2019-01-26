package it.unimib.disco.bigtwine.services.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.ner.Recognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestProcessor implements NerProcessor {

    public static final Recognizer recognizer = Recognizer.test;

    private ProcessorListener<RecognizedTweet> processorListener;

    @Override
    public Recognizer getRecognizer() {
        return recognizer;
    }

    @Override
    public String getProcessorId() {
        return "test-recognizer";
    }

    @Override
    public void setExecutor(Executor executor) {

    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void setListener(ProcessorListener<RecognizedTweet> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean configureProcessor() {
        return true;
    }

    @Override
    public boolean process(String tag, BasicTweet item) {
        return this.process(tag, new BasicTweet[]{item});
    }

    @Override
    public boolean process(String tag, BasicTweet[] items) {
        List<RecognizedTweet> tweets = new ArrayList<>();
        for (BasicTweet tweet : items) {
            RecognizedTweet rt = new RecognizedTweet(tweet.getId(), tweet.getText());
            List<RecognizedTweet.Entity> entities = new ArrayList<>();
            int count = new Random().nextInt(4);
            for (int i = 0; i < count; ++i)  {
                entities.add(new RecognizedTweet.Entity(
                    "testvalue",
                    "testlabel",
                    1.0f
                ));
            }
            rt.setEntities(entities.toArray(new RecognizedTweet.Entity[0]));
            tweets.add(rt);
        }
        this.processorListener.onProcessed(this, tag, tweets.toArray(new RecognizedTweet[0]));
        return true;
    }
}
