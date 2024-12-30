package bgu.spl.mics.application.objects;

import java.util.ArrayList;
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

    private final int id;  // Unique ID of the worker
    private final int frequency;  // Time interval at which the worker operates
    private int currentTime;  // Current simulation time
    private final String dataPath;  // Path to the worker's data in the database
    private STATUS status;  // Current status of the worker (e.g., UP, DOWN)
    private final List<StampedCloudPoints> stampedCloudPoints;  // Cloud points handled by this worker

    /**
     * Constructor for the LiDarWorkerTracker.
     *
     * @param id         The unique ID of the LiDAR worker.
     * @param frequency  The frequency at which the worker processes events.
     * @param dataPath   The data path to the LiDAR database.
     * @param dataBase   The LiDarDataBase instance.
     */
    public LiDarWorkerTracker(int id, int frequency, String dataPath) {
        this.id = id;
        this.frequency = frequency;
        this.dataPath = dataPath;
        this.status = STATUS.UP;
        this.currentTime = 0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getDataPath() {
        return dataPath;
    }

    public STATUS getStatus() {
        return status;
    }

    /**
     * Checks if the LiDAR worker has finished processing all its data.
     *
     * @return True if all data has been processed; false otherwise.
     */
    private boolean isDone() {
        return currentTime >= stampedCloudPoints.get(stampedCloudPoints.size() - 1).getTime() + frequency;
    }

    /**
     * Updates the current time of the LiDAR worker and checks if the worker has completed
     * its work. If the worker has completed its work, it sets the status to DOWN.
     *
     * @param currentTime The current simulation time.
     */
    public void updateTime(int currentTime) {
        this.currentTime = currentTime;
        if (isDone()) {
            this.status = STATUS.DOWN;
        }
    }

    /**
     * Retrieves a list of detected objects for the given time tick.
     *
     * @param currentTime The current simulation time.
     * @return A list of DetectedObjectsEvent for the specified time, or an empty list if none are found.
     */
    public List<DetectObjectsEvent> getDetectedObjectsByTime(int currentTime) {
        List<DetectObjectsEvent> detectedEvents = new ArrayList<>();
        for (StampedCloudPoints stampedCloudPoint : stampedCloudPoints) {
            if (stampedCloudPoint.getTime() + frequency == currentTime) {
                detectedEvents.add(new DetectObjectsEvent(stampedCloudPoint));
            }
        }
        return detectedEvents;
    }

    /**
     * Generates TrackedObjectsEvent from the detected objects at the current time tick.
     *
     * @param currentTime The current simulation time.
     * @return A TrackedObjectsEvent containing tracked objects, or null if no data is available.
     */
    public TrackedObjectsEvent generateTrackedObjectsEvent(int currentTime) {
        List<CloudPoint> trackedCloudPoints = new ArrayList<>();
        for (StampedCloudPoints stampedCloudPoint : stampedCloudPoints) {
            if (stampedCloudPoint.getTime() + frequency == currentTime) {
                trackedCloudPoints.addAll(stampedCloudPoint.getCloudPoints());
            }
        }
        if (trackedCloudPoints.isEmpty()) {
            return null;
        }
        return new TrackedObjectsEvent(id, trackedCloudPoints);
    }
}
