package it.unimib.disco.bigtwine.ner.producers;

import it.unimib.disco.bigtwine.commons.csv.CSVFactory;
import it.unimib.disco.bigtwine.ner.Recognizer;
import javafx.util.Builder;

import java.io.IOException;
import java.io.Writer;

public class InputProducerBuilder implements Builder<InputProducer> {

    private Recognizer recognizer;

    private Writer writer;

    public static InputProducerBuilder getDefaultBuilder() {
        return new InputProducerBuilder();
    }

    public InputProducerBuilder setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
        return this;
    }

    public Recognizer getRecognizer() {
        return recognizer;
    }

    public InputProducerBuilder setWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public InputProducer build() {
        if (this.recognizer == null) {
            return null;
        }

        if (this.writer == null) {
            return null;
        }

        InputProducer inputProducer;
        switch (this.recognizer) {
            case ritter:
                inputProducer = new RitterInputProducer(CSVFactory.getFactory());
                break;
            default:
                return null;
        }

        try {
            inputProducer.setWriter(this.writer);
        } catch (IOException e) {
            return null;
        }

        return inputProducer;
    }
}
