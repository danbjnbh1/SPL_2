package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

public class Configuration {
    private Cameras cameras;
    private LidarWorkers lidarWorkers;
    private String poseJsonFile;
    private int tickTime;
    private int duration;

    // Getters and setters
    public Cameras getCameras() {
        return cameras;
    }

    public void setCameras(Cameras cameras) {
        this.cameras = cameras;
    }

    public LidarWorkers getLidarWorkers() {
        return lidarWorkers;
    }

    public void setLidarWorkers(LidarWorkers lidarWorkers) {
        this.lidarWorkers = lidarWorkers;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static class Cameras {
        private List<CameraConfiguration> camerasConfigurations;
        private String cameraDatasPath;

        // Getters and setters
        public List<CameraConfiguration> getCamerasConfigurations() {
            return camerasConfigurations;
        }

        public void setCamerasConfigurations(List<CameraConfiguration> camerasConfigurations) {
            this.camerasConfigurations = camerasConfigurations;
        }

        public String getCameraDatasPath() {
            return cameraDatasPath;
        }

        public void setCameraDatasPath(String cameraDatasPath) {
            this.cameraDatasPath = cameraDatasPath;
        }

        public static class CameraConfiguration {
            private int id;
            private int frequency;
            private String cameraKey;

            // Getters and setters
            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getFrequency() {
                return frequency;
            }

            public void setFrequency(int frequency) {
                this.frequency = frequency;
            }

            public String getCameraKey() {
                return cameraKey;
            }

            public void setCameraKey(String cameraKey) {
                this.cameraKey = cameraKey;
            }
        }
    }

    public static class LidarWorkers {
        private List<LidarConfiguration> lidarConfigurations;
        private String lidarsDataPath;

        // Getters and setters
        public List<LidarConfiguration> getLidarConfigurations() {
            return lidarConfigurations;
        }

        public void setLidarConfigurations(List<LidarConfiguration> lidarConfigurations) {
            this.lidarConfigurations = lidarConfigurations;
        }

        public String getLidarsDataPath() {
            return lidarsDataPath;
        }

        public void setLidarsDataPath(String lidarsDataPath) {
            this.lidarsDataPath = lidarsDataPath;
        }

        public static class LidarConfiguration {
            private int id;
            private int frequency;

            // Getters and setters
            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getFrequency() {
                return frequency;
            }

            public void setFrequency(int frequency) {
                this.frequency = frequency;
            }
        }
    }

    public static class ConfigurationParser {
        public static Configuration parseConfiguration(String filePath) throws IOException {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(filePath)) {
                return gson.fromJson(reader, Configuration.class);
            }
        }
    }

}