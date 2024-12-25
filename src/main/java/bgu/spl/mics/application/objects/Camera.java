package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private static int idCounter = 0;
    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> detectedObjectsList;

    public Camera(int frequency) {
        this.id = ++Camera.idCounter;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.detectedObjectsList = new ArrayList<StampedDetectedObjects>();
    }

    public int getId() {
        return id;
    }

    public List<StampedDetectedObjects> getDetectedObjectsListByTime(int time) {
        List<StampedDetectedObjects> result = new ArrayList<StampedDetectedObjects>();
        for (StampedDetectedObjects stampedDetectedObjects : detectedObjectsList) {
            if (time - frequency <= stampedDetectedObjects.getTime() || stampedDetectedObjects.getTime() <= time) { //! implement getTime()
                result.add(stampedDetectedObjects);
            }
            return detectedObjectsList;
        }
    }
}
