package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final String id; // Unique ID of the worker
    private final int frequency; // Time interval at which the worker operates
    private STATUS status; // Current status of the worker (e.g., UP, DOWN)
    private List<TrackedObject> lastTrackedObjects; // Last tracked objects
    private final LiDarDataBase dataBase; // Reference to the LiDarDataBase

    /**
     * Constructor for the LiDarWorkerTracker.
     *
     * @param id        The unique ID of the LiDAR worker.
     * @param frequency The frequency at which the worker processes events.
     * @param dataPath  The data path to the LiDAR database.
     * @param dataBase  The LiDarDataBase instance.
     */
    public LiDarWorkerTracker(String id, int frequency, LiDarDataBase dataBase) {
        this.id = id;
        this.frequency = frequency;
        this.lastTrackedObjects = new ArrayList<>();
        this.status = STATUS.UP;
        this.dataBase = dataBase;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    /**
     * Generates TrackedObjectsEvent from the detected objects at the current time
     * tick.
     *
     * @param currentTime The current simulation time.
     * @return A TrackedObjectsEvent containing tracked objects, or null if no data
     *         is available.
     */
    public TrackedObjectsEvent generateTrackedObjectsEvent(int currentTime) {
        List<TrackedObject> objectToSends = new ArrayList<>();
        Iterator<TrackedObject> iterator = lastTrackedObjects.iterator();

        while (iterator.hasNext()) {
            TrackedObject trackedObject = iterator.next();
            if (trackedObject.getTime() + frequency <= currentTime) {
                objectToSends.add(new TrackedObject(trackedObject));
                iterator.remove();
            }
        }

        return new TrackedObjectsEvent(objectToSends);
    }

    public void processDetectedObjects(DetectObjectsEvent e) {
        StampedDetectedObjects stampedDetectedObjects = e.getDetectedObjects();

        int time = stampedDetectedObjects.getTime();
        List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjects();

        List<StampedCloudPoints> listOfStampedCloudPoints = dataBase.getListOfStampedCloudPointsByTime(time);

        for (DetectedObject detectedObject : detectedObjects) {
            for (StampedCloudPoints stampedCloudPoint : listOfStampedCloudPoints) {
                if (detectedObject.getId() == stampedCloudPoint.getId()) {
                    lastTrackedObjects.add(new TrackedObject(detectedObject.getId(), detectedObject.getDescription(),
                            stampedCloudPoint.getTime(), stampedCloudPoint.getCloudPoints()));
                }
            }

        }
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }
}
