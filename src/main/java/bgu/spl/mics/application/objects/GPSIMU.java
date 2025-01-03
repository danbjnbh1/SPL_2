package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> poses;

    public GPSIMU(String path) {
        this.currentTick = 0;
        this.status = STATUS.UP;
        this.poses = parsePoses(path);
    }

    public List<Pose> parsePoses(String path) {
        Gson gson = new Gson();
        try (FileReader readrer = new FileReader(path)) {
            Type type = new TypeToken<List<Pose>>() {
            }.getType();
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

    private boolean isDone() {
        return currentTick >= poses.get(poses.size() - 1).getTime();
    }

    public STATUS getStatus() {
        return status;
    }

    /**
     * Updates the current time of the GPSIMU and checks if it has completed
     * its work.
     * If it has completed its work, it sets the status to DOWN.
     *
     * @param currentTime the current time tick to update
     */
    public void updateTime(int currentTime) {
        this.currentTick = currentTime;
        if (isDone()) {
            this.status = STATUS.DOWN;
        }
    }

    public List<Pose> getPosesUntilNow() {
        return poses.subList(0, currentTick);
    }
}
