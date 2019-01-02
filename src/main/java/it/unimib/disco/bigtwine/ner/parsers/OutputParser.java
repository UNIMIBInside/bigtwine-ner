package it.unimib.disco.bigtwine.ner.parsers;

import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.parsers.GenericOutputParser;

public interface OutputParser extends GenericOutputParser<RecognizedTweet> {
    default RecognizedTweet[] tweets() {
        return this.items();
    }
}
