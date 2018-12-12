package it.unimib.disco.bigtwine.ner;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;


final public class RitterInputProducer implements InputProducer, Closeable {
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
    public void appendTweet(BasicTweet tweet) throws IOException {
        this.csvPrinter.printRecord(tweet.getId(), tweet.getText());
    }

    @Override
    public void appendTweets(BasicTweet[] tweets) throws IOException {
        for (BasicTweet tweet : tweets) {
            this.appendTweet(tweet);
        }
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
        this.buffer.close();
    }
}
