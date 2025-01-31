package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final int id; // Unique ID of the worker
    private final String name;
    private final int frequency; // Time interval at which the worker operates
    private STATUS status; // Current status of the worker (e.g., UP, DOWN)
    private List<TrackedObject> lastTrackedObjects; // Last tracked objects
    private List<TrackedObject> lastFrame;
    private final LiDarDataBase dataBase; // Reference to the LiDarDataBase
    private int currentTime;

    /**
     * Constructor for the LiDarWorkerTracker.
     *
     * @param id        The unique ID of the LiDAR worker.
     * @param frequency The frequency at which the worker processes events.
     * @param dataPath  The data path to the LiDAR database.
     * @param dataBase  The LiDarDataBase instance.
     */
    public LiDarWorkerTracker(int id, int frequency, LiDarDataBase dataBase) {
        this.id = id;
        this.frequency = frequency;
        this.lastTrackedObjects = new ArrayList<>();
        this.lastFrame = new ArrayList<>();
        this.status = STATUS.UP;
        this.dataBase = dataBase;
        this.currentTime = 0;
        this.name = "LiDarWorkerTracker" + id;
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    /**
     * Generates a list of TrackedObjects from the detected objects at the current
     * time tick.
     * 
     * @return A list of TrackedObjects to publish.
     */
    public List<TrackedObject> getCurrentTrackedObjects() {
        List<TrackedObject> objectToSends = new ArrayList<>();
        Iterator<TrackedObject> iterator = lastTrackedObjects.iterator();
        while (iterator.hasNext()) {
            TrackedObject trackedObject = iterator.next();
            if (trackedObject.getTime() + frequency <= currentTime) {
                objectToSends.add(new TrackedObject(trackedObject));
                iterator.remove();
            }
        }

        return objectToSends;
    }

    public void updateTime(int currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * @inv: lastTrackedObjects != null && dataBase != null
     *       && dataBase.getNumOfConsumedCloudPoints().get() >= 0
     *       && dataBase.getCloudPoints().size() >=
     *       dataBase.getNumOfConsumedCloudPoints().get()
     *       && currentTime >= 0
     *
     * @PRE: e != null && e.getDetectedObjects() != null
     *       && e.getDetectedObjects().getDetectedObjects() != null
     *       &&
     *       dataBase.getListOfStampedCloudPointsByTime(e.getDetectedObjects().getTime())
     *       != null
     *
     * @POST: lastTrackedObjects.size() >= @PRE(lastTrackedObjects.size())
     *        && dataBase.getNumOfConsumedCloudPoints().get()
     *        >= @PRE(dataBase.getNumOfConsumedCloudPoints().get())
     */
    public void processDetectedObjects(DetectObjectsEvent e) {
        StampedDetectedObjects stampedDetectedObjects = e.getDetectedObjects();

        int time = stampedDetectedObjects.getTime();
        List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjects();
        List<StampedCloudPoints> listOfStampedCloudPoints = dataBase.getListOfStampedCloudPointsByTime(time);
        for (DetectedObject detectedObject : detectedObjects) {
            for (StampedCloudPoints stampedCloudPoint : listOfStampedCloudPoints) {
                if (detectedObject.getId().equals(stampedCloudPoint.getId())
                        || stampedCloudPoint.getId().equals("ERROR")) {
                    lastTrackedObjects.add(new TrackedObject(stampedCloudPoint.getId(), detectedObject.getDescription(),
                            stampedCloudPoint.getTime(), stampedCloudPoint.getCloudPoints()));
                    dataBase.incrementNumOfConsumedCloudPoints();
                }
            }

        }
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void updateStatus() {
        if (dataBase.getNumOfConsumedCloudPoints().get() == dataBase.getCloudPoints().size()) {
            status = STATUS.DOWN;
        }
    }

    public String getName() {
        return name;
    }

    public void setLastFrame(List<TrackedObject> lastFrame) {
        this.lastFrame = lastFrame;
    }

    public List<TrackedObject> getLastFrame() {
        return lastFrame;
    }
}
