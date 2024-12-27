package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> poses;

    public GPSIMU(int currentTick, STATUS status, String path) {
        this.currentTick = currentTick;
        this.status = status;
        this.poses = parsePoses(path);
    }

    public List<Pose> parsePoses(String path) {
        Gson gson = new Gson();
        try (FileReader readrer = new FileReader(path)){
            Type type = new TypeToken<List<Pose>>() {}.getType();
            return gson.fromJson(readrer, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Pose getPose() {
        for (Pose pose : poses) {
            if (pose.getTime() == currentTick) {
                return pose;
            }
        }
        return null;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTick = currentTime;
    }
}
