package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private int currentTime;
    private STATUS status;
    private final List<StampedDetectedObjects> detectedObjectsList;
    CameraDataBase dataBase;

    public Camera(int id, int frequency, String key, CameraDataBase dataBase) {
        this.frequency = frequency;
        this.id = id;
        this.status = STATUS.UP;
        this.detectedObjectsList = dataBase.getCameraData(key);
        this.currentTime = 0;
    }

    public int getId() {
        return id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    private boolean isDone() {
        return currentTime - frequency >= detectedObjectsList.get(detectedObjectsList.size() - 1).getTime();
    }

    /**
     * Updates the current time of the camera and checks if the camera has completed
     * its work.
     * If the camera has completed its work, it sets the status to DOWN.
     *
     * @param currentTime the current time tick to update
     */
    public void updateTime(int currentTime) {
        this.currentTime = currentTime;
        if (isDone()) {
            this.status = STATUS.DOWN;
        }
    }

    public StampedDetectedObjects getDetectedObjectsByTime() {
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if (stampedDetectedObjects.getTime() + frequency == currentTime) {
                return stampedDetectedObjects;
            }
        }

        return null;
    }
}
