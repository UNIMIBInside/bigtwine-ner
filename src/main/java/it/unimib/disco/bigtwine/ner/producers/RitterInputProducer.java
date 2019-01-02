package it.unimib.disco.bigtwine.ner.producers;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;


final public class RitterInputProducer implements InputProducer {
    private Writer writer;
    private BufferedWriter buffer;
    private CSVPrinter csvPrinter;

    public RitterInputProducer() {

    }

    public RitterInputProducer(Writer writer) throws IOException {
        this();
        this.setWriter(writer);
    }

    @Override
    public void setWriter(Writer writer) throws IOException {
        this.writer = writer;
        this.buffer = new BufferedWriter(writer);
        this.csvPrinter = new CSVPrinter(this.buffer, CSVFormat.DEFAULT.withDelimiter('\t'));
    }

    @Override
    public Writer getWriter() {
        return null;
    }

    @Override
    public void append(BasicTweet tweet) throws IOException {
        if (this.buffer == null) throw new AssertionError("A writer was not set");
        this.csvPrinter.printRecord(tweet.getId(), tweet.getText());
    }

    @Override
    public String toString() {
        try {
            this.close();
        }catch (IOException e) {
            return null;
        }

        return this.buffer.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.buffer == null) throw new AssertionError("A writer was not set");
        this.buffer.close();
    }

    @Override
    public void flush() throws IOException {
        if (this.buffer == null) throw new AssertionError("A writer was not set");
        this.buffer.flush();
    }
}
