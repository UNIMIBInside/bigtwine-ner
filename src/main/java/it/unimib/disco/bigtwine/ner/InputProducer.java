package it.unimib.disco.bigtwine.ner;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;

import java.io.IOException;
import java.io.Writer;

public interface InputProducer {

    void setWriter(Writer writer) throws IOException;
    Writer getWriter();
    void appendTweet(BasicTweet tweet) throws IOException;
    void appendTweets(BasicTweet[] tweets) throws IOException;
    String toString();

}
