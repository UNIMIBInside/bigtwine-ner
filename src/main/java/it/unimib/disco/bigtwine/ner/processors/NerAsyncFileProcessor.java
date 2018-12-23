package it.unimib.disco.bigtwine.ner.processors;

import it.unimib.disco.bigtwine.commons.executors.AsyncExecutor;
import it.unimib.disco.bigtwine.commons.executors.AsyncFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.file.AsyncFileProcessor;
import it.unimib.disco.bigtwine.ner.parsers.OutputParser;
import it.unimib.disco.bigtwine.ner.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.ner.producers.InputProducer;
import it.unimib.disco.bigtwine.ner.producers.InputProducerBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class NerAsyncFileProcessor implements Processor, AsyncFileProcessor {

    protected AsyncFileExecutor executor;
    protected OutputParserBuilder outputParserBuilder;
    protected InputProducerBuilder inputProducerBuilder;
    protected FileAlterationMonitor fileMonitor;
    protected String outputDirectory;
    protected String processorId;
    protected String workingDirectory;
    protected String inputDirectory;
    protected boolean monitorFilesOnly;
    protected String monitorSuffixFilter;
    protected ProcessorListener processorListener;

    public NerAsyncFileProcessor(AsyncFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        this.setExecutor(executor);
        this.setInputProducerBuilder(inputProducerBuilder);
        this.setOutputParserBuilder(outputParserBuilder);
    }

    @Override
    public FileAlterationMonitor getFileMonitor() {
        return fileMonitor;
    }

    @Override
    public void setFileMonitor(FileAlterationMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
    }

    @Override
    public String getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    @Override
    public InputProducerBuilder getInputProducerBuilder() {
        return this.inputProducerBuilder;
    }

    @Override
    public void setInputProducerBuilder(InputProducerBuilder producerBuilder) {
        this.inputProducerBuilder = producerBuilder
            .setRecognizer(this.getRecognizer());
    }

    @Override
    public OutputParserBuilder getOutputParserBuilder() {
        return this.outputParserBuilder;
    }

    @Override
    public void setOutputParserBuilder(OutputParserBuilder outputParserBuilder) {
        this.outputParserBuilder = outputParserBuilder
            .setRecognizer(this.getRecognizer());
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof AsyncFileExecutor)) {
            throw new IllegalArgumentException("Unsupported executor type");
        }
        this.executor = (AsyncFileExecutor)executor;
    }

    @Override
    public AsyncExecutor getAsyncExecutor() {
        return this.executor;
    }

    @Override
    public AsyncFileExecutor getAsyncFileExecutor() {
        return this.executor;
    }

    @Override
    public String getInputDirectory() {
        return this.inputDirectory;
    }

    @Override
    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    @Override
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    @Override
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getProcessorId() {
        return this.processorId;
    }

    public boolean getMonitorFilesOnly() {
        return monitorFilesOnly;
    }

    @Override
    public void setMonitorFilesOnly(boolean monitorFilesOnly) {
        this.monitorFilesOnly = monitorFilesOnly;
    }

    @Override
    public String getMonitorSuffixFilter() {
        return monitorSuffixFilter;
    }

    @Override
    public void setMonitorSuffixFilter(String monitorSuffixFilter) {
        this.monitorSuffixFilter = monitorSuffixFilter;
    }

    @Override
    public void setListener(ProcessorListener listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean setupWorkingDirectory() {
        boolean res = true;
        for (String dir : new String[] {this.inputDirectory, this.outputDirectory}) {
            if (dir == null) continue;

            File dirf = (new File(dir));
            if (!dirf.exists()) {
                try {
                    res &= dirf.mkdirs();
                }catch (SecurityException e) {
                    return false;
                }
            }
        }

        return res;
    }

    @Override
    public boolean configureProcessor() {
        this.processorId = RandomStringUtils.randomAlphanumeric(16);
        this.inputDirectory = Paths.get(this.getWorkingDirectory(), this.getProcessorId(), "input").toString();
        this.outputDirectory = Paths.get(this.getWorkingDirectory(), this.getProcessorId(), "output").toString();

        if (!this.setupWorkingDirectory()) {
            return false;
        }

        this.getAsyncFileExecutor().setInputPath(this.inputDirectory);
        this.getAsyncFileExecutor().setOutputPath(this.outputDirectory);
        this.getAsyncExecutor().run();

        if (!this.configureFileMonitor()) {
            return false;
        }

        if (!this.startFileMonitor()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean process(String tag, BasicTweet[] tweets) {
        Path inputFilePath = Paths.get(this.getInputDirectory(), tag);
        File inputFile = new File(inputFilePath.toAbsolutePath().toString());
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(inputFile);
        } catch (IOException e) {
            return false;
        }

        InputProducer inputProducer = this.inputProducerBuilder
            .setRecognizer(this.getRecognizer())
            .setWriter(fileWriter)
            .build();

        if (inputProducer == null) {
            return false;
        }

        try {
            inputProducer.appendTweets(tweets);
            inputProducer.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void processOutputFile(File outputFile) {
        OutputParser outputParser = this.outputParserBuilder
            .setRecognizer(this.getRecognizer())
            .setInput(outputFile)
            .build();

        if (outputParser == null) {
            return;
        }

        String tag = FilenameUtils.removeExtension(outputFile.getName());
        RecognizedTweet[] tweets = outputParser.tweets();

        if (!tag.isEmpty() && this.processorListener != null && tweets != null) {
            this.processorListener.onProcessed(this, tag, tweets);
        }
    }
}
