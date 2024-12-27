package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final String key;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> detectedObjectsList;

    public Camera(int id, int frequency, String key, String dataPath) {
        this.frequency = frequency;
        this.id = id;
        this.key = key;
        this.status = STATUS.UP;
        this.detectedObjectsList = new ArrayList<StampedDetectedObjects>();
    }

    public int getId() {
        return id;
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
