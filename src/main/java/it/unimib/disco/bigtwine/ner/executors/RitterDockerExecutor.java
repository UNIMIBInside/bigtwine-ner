package it.unimib.disco.bigtwine.ner.executors;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import it.unimib.disco.bigtwine.commons.executors.AsyncFileExecutor;
import it.unimib.disco.bigtwine.commons.executors.DockerExecutor;
import it.unimib.disco.bigtwine.commons.executors.FileExecutor;

import java.util.*;

public class RitterDockerExecutor extends DockerExecutor implements AsyncFileExecutor {
    public static final String DOCKER_IMAGE = "bigtwine-tool-ner";

    private Map<String, Object> conf;
    private String inputPath;
    private String outputPath;

    protected RitterDockerExecutor(String dockerImage) {
        super(dockerImage);
    }

    public RitterDockerExecutor() {
        this(DOCKER_IMAGE);
    }

    @Override
    public String getExecutorId() {
        return "docker-ritter";
    }

    @Override
    public Map<String, Object> getExecutorConf() {
        return this.conf;
    }

    @Override
    public void setExecutorConf(Map<String, Object> conf) {
        this.conf = conf;
    }

    @Override
    protected List<String> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    @Override
    public String getInputPath() {
        return inputPath;
    }

    @Override
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    @Override
    protected CreateContainerCmd createContainer(String image, List<String> args) {
        return super.createContainer(image, args)
            .withHostConfig(HostConfig.newHostConfig().withBinds(
                new Bind(this.getInputPath(), new Volume("/data/input")),
                new Bind(this.getOutputPath(), new Volume("/data/output"))
            ));
    }
}
