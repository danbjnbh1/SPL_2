package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private final List<DetectedObject> detectedObjects;
    private final int time;

    public StampedDetectedObjects(List<DetectedObject> DetectedObjects, int Time) {
        this.detectedObjects = DetectedObjects;
        this.time = Time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public int getTime() {
        return time;
    }
}
