package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Configuration {
    @SerializedName("Cameras")
    private Cameras cameras;

    @SerializedName("LiDarWorkers")
    private Lidars lidars;

    @SerializedName("poseJsonFile")
    private String poseJsonFile;

    @SerializedName("TickTime")
    private int tickTime;

    @SerializedName("Duration")
    private int duration;

    public Cameras getCameras() {
        return cameras;
    }

    public Lidars getLidars() {
        return this.lidars;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public int getTickTime() {
        return tickTime;
    }

    public int getDuration() {
        return duration;
    }

    public static class Cameras {
        @SerializedName("CamerasConfigurations")
        private List<CameraConfiguration> camerasConfigurations;

        @SerializedName("camera_datas_path")
        private String cameraDatasPath;

        public List<CameraConfiguration> getCamerasConfigurations() {
            return camerasConfigurations;
        }

        public String getCameraDatasPath() {
            return cameraDatasPath;
        }

        public static class CameraConfiguration {
            @SerializedName("id")
            private int id;

            @SerializedName("frequency")
            private int frequency;

            @SerializedName("camera_key")
            private String cameraKey;

            public int getId() {
                return id;
            }

            public int getFrequency() {
                return frequency;
            }

            public String getCameraKey() {
                return cameraKey;
            }
        }
    }

    public static class Lidars {
        @SerializedName("LidarConfigurations")
        private List<LidarConfiguration> lidarConfigurations;

        @SerializedName("lidars_data_path")
        private String lidarsDataPath;

        public List<LidarConfiguration> getLidarConfigurations() {
            return lidarConfigurations;
        }

        public String getLidarsDataPath() {
            return lidarsDataPath;
        }

        public static class LidarConfiguration {
            @SerializedName("id")
            private int id;

            @SerializedName("frequency")
            private int frequency;

            public int getId() {
                return id;
            }

            public int getFrequency() {
                return frequency;
            }
        }
    }

    public static class ConfigurationParser {
        public static Configuration parseConfiguration(String filePath) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(filePath)) {
                return gson.fromJson(reader, Configuration.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}