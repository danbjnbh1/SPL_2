package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class DetectObjectsEvent implements Event<String> {
    private final List<DetectedObject> detectedObjects;
    private final int time;

    public DetectObjectsEvent(StampedDetectedObjects stampedDetectedObjects) {
        this.detectedObjects = stampedDetectedObjects.getDetectedObjects();
        this.time = stampedDetectedObjects.getTime();
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public int getTime() {
        return time;
    }
}
