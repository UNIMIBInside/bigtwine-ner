package it.unimib.disco.bigtwine.ner.executors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.ner.Recognizer;

public class ExecutorFactory {

    public static ExecutorFactory getFactory() {
        return new ExecutorFactory();
    }

    public Executor getExecutor(Recognizer recognizer) {
        switch (recognizer) {
            case ritter:
                return new RitterDockerExecutor();
            default:
                return null;
        }
    }

    public Executor getDefaultExecutor() {
        return this.getExecutor(Recognizer.getDefault());
    }
}
