package it.unimib.disco.bigtwine.config;

import it.unimib.disco.bigtwine.ner.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NerConfiguration {

    private ApplicationProperties appProps;

    public NerConfiguration(ApplicationProperties appProps) {
         this.appProps = appProps;
    }

    @Bean
    public ProcessorFactory getProcessorFactory() {
        return new ProcessorFactory(this.appProps.getProcessors(), this.getExecutorFactory());
    }

    @Bean
    public ExecutorFactory getExecutorFactory() {
        return new ExecutorFactory(this.appProps.getExecutors());
    }

    @Bean
    public InputProducerBuilder getInputProducerBuilder() {
        return InputProducerBuilder.getDefaultBuilder();
    }

    @Bean
    public OutputParserBuilder getOutputParserBuilder() {
        return OutputParserBuilder.getDefaultBuilder();
    }

}
