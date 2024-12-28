package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> detectedObjectsList;
    CameraDataBase dataBase;

    public Camera(int id, int frequency, String key, CameraDataBase dataBase) {
        this.frequency = frequency;
        this.id = id;
        this.status = STATUS.UP;
        this.detectedObjectsList = dataBase.getCameraData(key);
    }

    public int getId() {
        return id;
    }

    // ! Check if need this status methods
    public void setStatus(STATUS status) {
        this.status = status;
    }

    // ! Check if need this status methods
    public STATUS getStatus() {
        return status;
    }

    public StampedDetectedObjects getDetectedObjectsByTime(int time) {
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if (stampedDetectedObjects.getTime() + frequency == time) {
                return stampedDetectedObjects;
            }
        }
        return null;
    }
}
