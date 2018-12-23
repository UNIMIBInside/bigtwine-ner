package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.ner.Recognizer;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;

public interface Processor {

    InputProducerBuilder getInputProducerBuilder();
    void setInputProducerBuilder(InputProducerBuilder producerBuilder);
    OutputParserBuilder getOutputParserBuilder();
    void setOutputParserBuilder(OutputParserBuilder outputParserBuilder);
    Recognizer getRecognizer();
    String getProcessorId();
    void setExecutor(Executor executor);
    Executor getExecutor();
    void setListener(ProcessorListener listener);

    boolean configureProcessor();
    boolean process(String tag, BasicTweet[] tweets);

}
