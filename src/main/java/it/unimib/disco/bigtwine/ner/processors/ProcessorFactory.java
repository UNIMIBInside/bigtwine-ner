package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.AsyncFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.processors.file.AsyncFileProcessor;
import it.unimib.disco.bigtwine.commons.processors.file.FileProcessor;
import it.unimib.disco.bigtwine.config.ApplicationProperties;
import it.unimib.disco.bigtwine.ner.Recognizer;
import it.unimib.disco.bigtwine.ner.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;
import org.springframework.beans.factory.FactoryBean;

import java.io.IOException;
import java.nio.file.Files;

public class ProcessorFactory implements FactoryBean<Processor> {

    private Recognizer recognizer;
    private ExecutorFactory executorFactory;
    private ApplicationProperties.Processors processorsProps;

    public ProcessorFactory(ApplicationProperties.Processors processorsProps, ExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
        this.processorsProps = processorsProps;
    }

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    public Recognizer getRecognizer() {
        return this.recognizer;
    }

    private RitterProcessor getRitterProcessor() throws Exception {
        Executor executor = this.executorFactory.getExecutor(recognizer);

        if (!(executor instanceof AsyncFileExecutor))
            throw new RuntimeException("Invalid configuration: Ritter processor requires an AsyncFileExecutor.");

        RitterProcessor processor = new RitterProcessor(
            (AsyncFileExecutor)executor,
            InputProducerBuilder.getDefaultBuilder(),
            OutputParserBuilder.getDefaultBuilder());

        final boolean useTmpWD = this.processorsProps.getRitter().getUseTmpWorkingDirectory();
        String wd = this.processorsProps.getRitter().getWorkingDirectory();
        final String suffixFilter = this.processorsProps.getRitter().getFileMonitorSuffixFilter();

        if (useTmpWD || wd == null) {
            wd = Files.createTempDirectory("ner").toString();
        }

        processor.setWorkingDirectory(wd);


        if (suffixFilter != null) {
            processor.setMonitorFilesOnly(true);
            processor.setMonitorSuffixFilter(suffixFilter);
        }

        return processor;
    }

    public Processor getProcessor() throws Exception {
        if (this.recognizer == null) {
            throw new IllegalArgumentException("recognizer not set");
        }

        switch (recognizer) {
            case ritter:
                return this.getRitterProcessor();
            default:
                return null;
        }
    }

    public Processor getProcessor(Recognizer recognizer) throws Exception {
        this.setRecognizer(recognizer);
        return this.getProcessor();
    }

    public Processor getDefaultProcessor() throws Exception {
        return this.getProcessor(Recognizer.getDefault());
    }

    @Override
    public Processor getObject() throws Exception {
        return this.getProcessor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.recognizer == null) {
            return null;
        }

        switch (recognizer) {
            case ritter:
                return RitterProcessor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
