package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private final AtomicInteger systemRuntime;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;

    static class StatisticalFolderHolder {
        static final StatisticalFolder INSTANCE = new StatisticalFolder();
    }

    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.INSTANCE;
    }

    private StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    public void incrementSystemRuntime() {
        this.systemRuntime.incrementAndGet();
    }

    // Increment the number of detected objects
    public void incrementDetectedObjects(int count) {
        this.numDetectedObjects.addAndGet(count);
    }

    // Increment the number of tracked objects
    public void incrementTrackedObjects(int count) {
        this.numTrackedObjects.addAndGet(count);
    }

    // Increment the number of landmarks
    public void setLandmarksCount(int count) {
        this.numLandmarks.set(count);
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }
}
