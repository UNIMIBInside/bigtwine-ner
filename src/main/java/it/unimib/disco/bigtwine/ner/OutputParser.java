package it.unimib.disco.bigtwine.ner;

import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;

import java.io.Reader;
import java.util.Iterator;

public interface OutputParser extends Iterator<RecognizedTweet> {
    Reader getReader();
    void setReader(Reader reader);
    RecognizedTweet[] tweets();
}
