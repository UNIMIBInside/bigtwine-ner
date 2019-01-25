package it.unimib.disco.bigtwine.services.ner.producers;

import it.unimib.disco.bigtwine.commons.models.BasicTweet;
import it.unimib.disco.bigtwine.commons.producers.GenericInputProducer;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public interface InputProducer extends GenericInputProducer<BasicTweet> {

}
