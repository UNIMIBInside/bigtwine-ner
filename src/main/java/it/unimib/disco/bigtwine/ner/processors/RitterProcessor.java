package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.PerpetualFileExecutor;
import it.unimib.disco.bigtwine.ner.Recognizer;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;

public class RitterProcessor extends NerPerpetualFileProcessor {

    public static final Recognizer recognizer = Recognizer.ritter;

    public RitterProcessor(PerpetualFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        super(executor, inputProducerBuilder, outputParserBuilder);
    }

    @Override
    public Recognizer getRecognizer() {
        return recognizer;
    }
}
