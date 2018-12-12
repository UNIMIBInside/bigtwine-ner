package it.unimib.disco.bigtwine.ner;

import javafx.util.Builder;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Writer;

public final class InputProducerBuilder implements Builder<InputProducer> {

    private Recognizer recognizer;

    private Writer writer;

    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    public Recognizer getRecognizer() {
        return recognizer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
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
                inputProducer = new RitterInputProducer();
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
