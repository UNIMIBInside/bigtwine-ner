package it.unimib.disco.bigtwine.ner;

import javafx.util.Builder;

import java.io.*;

public final class OutputParserBuilder implements Builder<OutputParser> {

    private Recognizer recognizer;

    private Reader reader;

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    public Recognizer getRecognizer() {
        return recognizer;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void setInput(String string) {
        this.reader = new StringReader(string);
    }

    public void setInput(File file) {
        try {
            this.reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            this.reader = null;
        }
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
