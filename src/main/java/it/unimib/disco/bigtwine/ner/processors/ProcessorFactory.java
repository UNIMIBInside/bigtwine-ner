package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.AsyncFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.ner.Recognizer;
import it.unimib.disco.bigtwine.ner.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;

public class ProcessorFactory {
    public static ProcessorFactory getFactory() {
        return new ProcessorFactory();
    }

    public Processor getProcessor(Recognizer recognizer) {
        switch (recognizer) {
            case ritter:
                Executor executor = ExecutorFactory
                    .getFactory()
                    .getExecutor(recognizer);

                if (!(executor instanceof AsyncFileExecutor))
                    throw new RuntimeException("Invalid executor for ritter processor");

                return new RitterProcessor(
                    (AsyncFileExecutor)executor,
                    new InputProducerBuilder(),
                    new OutputParserBuilder());
            default:
                return null;
        }
    }

    public Processor getDefaultProcessor() {
        return this.getProcessor(Recognizer.getDefault());
    }
}
