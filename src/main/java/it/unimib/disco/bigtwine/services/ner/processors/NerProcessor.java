package it.unimib.disco.bigtwine.services.ner.processors;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.services.ner.Recognizer;

public interface NerProcessor extends GenericProcessor<BasicTweet, RecognizedTweet> {

    Recognizer getRecognizer();

}
