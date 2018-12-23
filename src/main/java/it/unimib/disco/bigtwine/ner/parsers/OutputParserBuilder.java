package it.unimib.disco.bigtwine.ner.parsers;

import it.unimib.disco.bigtwine.ner.Recognizer;
import javafx.util.Builder;

import java.io.*;

public class OutputParserBuilder implements Builder<OutputParser> {

    private Recognizer recognizer;

    private Reader reader;

    public static OutputParserBuilder getDefaultBuilder() {
        return new OutputParserBuilder();
    }

    public OutputParserBuilder setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
        return this;
    }

    public Recognizer getRecognizer() {
        return recognizer;
    }

    public Reader getReader() {
        return reader;
    }

    public OutputParserBuilder setReader(Reader reader) {
        this.reader = reader;
        return this;
    }

    public void setInput(String string) {
        this.reader = new StringReader(string);
    }

    public OutputParserBuilder setInput(File file) {
        try {
            this.reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            this.reader = null;
        }
        return this;
    }

    @Override
    public OutputParser build() {
        if (this.recognizer == null) {
            return null;
        }

        if (this.reader == null) {
            return null;
        }

        OutputParser outputParser;
        switch (this.recognizer) {
            case ritter:
                outputParser = new RitterOutputParser();
                break;
            default:
                return null;
        }

        outputParser.setReader(this.reader);

        return outputParser;
    }
}