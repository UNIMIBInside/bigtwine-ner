package it.unimib.disco.bigtwine.ner.producers;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public interface InputProducer extends Closeable, Flushable {

    void setWriter(Writer writer) throws IOException;
    Writer getWriter();
    void appendTweet(BasicTweet tweet) throws IOException;
    void appendTweets(BasicTweet[] tweets) throws IOException;
    String toString();

}
