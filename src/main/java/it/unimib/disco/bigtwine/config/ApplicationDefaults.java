package it.unimib.disco.bigtwine.config;

public interface ApplicationDefaults {
    String defaultRecognizer = "ritter";

    interface Executors {
        interface RitterDocker {
            boolean classify = true;
        }
    }

    interface Processors {
        interface Ritter {
            String workingDirectory = "/tmp/ner";
            boolean useTmpWorkingDirectory = false;
            String fileMonitorSuffixFilter = null;
        }
    }
}
