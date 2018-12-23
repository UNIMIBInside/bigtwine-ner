package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;

@FunctionalInterface
public interface ProcessorListener {
    void onProcessed(Processor processor, String tag, RecognizedTweet[] tweets);
}
