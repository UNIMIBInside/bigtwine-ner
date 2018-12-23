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
    private String defaultRecognizer = ApplicationDefaults.defaultRecognizer;
    private final Executors executors = new Executors();
    private final Processors processors = new Processors();

    public static class Executors {

        private final RitterDocker ritterDocker = new RitterDocker();

        public static class RitterDocker {
            private boolean classify = ApplicationDefaults.Executors.RitterDocker.classify;

            public boolean isClassify() {
                return classify;
            }

            public boolean getClassify() {
                return classify;
            }

            public void setClassify(boolean classify) {
                this.classify = classify;
            }
        }

        public RitterDocker getRitterDocker() {
            return ritterDocker;
        }
    }

    public static class Processors {

        private final Ritter ritter = new Ritter();

        public static class Ritter {
            private String workingDirectory = ApplicationDefaults.Processors.Ritter.workingDirectory;
            private boolean useTmpWorkingDirectory = ApplicationDefaults.Processors.Ritter.useTmpWorkingDirectory;
            private String fileMonitorSuffixFilter = ApplicationDefaults.Processors.Ritter.fileMonitorSuffixFilter;

            public String getWorkingDirectory() {
                return workingDirectory;
            }

            public void setWorkingDirectory(String workingDirectory) {
                this.workingDirectory = workingDirectory;
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
        }

        public Ritter getRitter() {
            return this.ritter;
        }
    }

    public String getDefaultRecognizer() {
        return defaultRecognizer;
    }

    public void setDefaultRecognizer(String defaultRecognizer) {
        this.defaultRecognizer = defaultRecognizer;
        Recognizer recognizer = Recognizer.valueOf(defaultRecognizer);
        Recognizer.setDefault(recognizer);
    }

    public Executors getExecutors() {
        return this.executors;
    }

    public Processors getProcessors() {
        return this.processors;
    }
}
