package it.unimib.disco.bigtwine.executors;

import it.unimib.disco.bigtwine.commons.executors.DockerExecutor;
import it.unimib.disco.bigtwine.ner.executors.RitterDockerExecutor;
import org.junit.Test;

public class RitterDockerExecutorTest {

    @Test
    public void testContainerRun() {
        RitterDockerExecutor executor = new RitterDockerExecutor();
        executor.setInputPath("/Users/Fausto/Desktop/ner/input");
        executor.setOutputPath("/Users/Fausto/Desktop/ner/output");
        executor.run();
    }
}
