package it.unimib.disco.bigtwine.config;

import io.github.jhipster.config.JHipsterDefaults;
import it.unimib.disco.bigtwine.ner.Recognizer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Properties specific to Ner.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String workingDirectory = "/tmp/ner";
    private String defaultRecognizer = "ritter";
    private boolean useTmpWorkingDirectory = false;
    private String fileMonitorSuffixFilter = null;
    private final ExecutorsConfs executorsConfs = new ExecutorsConfs();


    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getDefaultRecognizer() {
        return defaultRecognizer;
    }

    public void setDefaultRecognizer(String defaultRecognizer) {
        this.defaultRecognizer = defaultRecognizer;
        Recognizer recognizer = Recognizer.valueOf(defaultRecognizer);
        Recognizer.setDefault(recognizer);
    }

    public boolean getUseTmpWorkingDirectory() {
        return useTmpWorkingDirectory;
    }

    public void setUseTmpWorkingDirectory(boolean useTmpWorkingDirectory) {
        this.useTmpWorkingDirectory = useTmpWorkingDirectory;
    }

    public boolean isUseTmpWorkingDirectory() {
        return useTmpWorkingDirectory;
    }

    public String getFileMonitorSuffixFilter() {
        return fileMonitorSuffixFilter;
    }

    public void setFileMonitorSuffixFilter(String fileMonitorSuffixFilter) {
        this.fileMonitorSuffixFilter = fileMonitorSuffixFilter;
    }

    public ExecutorsConfs getExecutorsConfs() {
        return this.executorsConfs;
    }

    public static class ExecutorsConfs {

        private final Map<String, Object> ritter = new HashMap<>();

        public Map<String, Object> getRitter() {
            return this.ritter;
        }

        public Map<String, Object> getById(@NotNull String executorId) {
            if (executorId.equals("ritter")) {
                return this.getRitter();
            }else {
                return null;
            }
        }

    }
}
