package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.AsyncFileExecutor;
import it.unimib.disco.bigtwine.ner.Recognizer;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;

public class RitterProcessor extends NerAsyncFileProcessor {

    public static final Recognizer recognizer = Recognizer.ritter;

    public RitterProcessor(AsyncFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        super(executor, inputProducerBuilder, outputParserBuilder);
    }

    @Override
    public Recognizer getRecognizer() {
        return recognizer;
    }
}
