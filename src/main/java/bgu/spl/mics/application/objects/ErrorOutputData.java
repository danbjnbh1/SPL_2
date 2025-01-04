package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorOutputData {
    private Statistics statistics;
    private String error;
    private String faultySensor;
    private Map<String, StampedDetectedObjects> lastCamerasFrame;
    private Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame;
    private List<Pose> poses;

    private static class ErrorOutputDataHolder {
        private static final ErrorOutputData instance = new ErrorOutputData();
    }

    public static ErrorOutputData getInstance() {
        return ErrorOutputDataHolder.instance;
    }

    public ErrorOutputData() {
        this.lastCamerasFrame = new ConcurrentHashMap<>();
        this.lastLiDarWorkerTrackersFrame = new ConcurrentHashMap<>();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void setLastCameraFrame(String cameraKey, StampedDetectedObjects detectedObjects) {
        lastCamerasFrame.putIfAbsent(cameraKey, detectedObjects);
    }

    public void setLastLiDarWorkerTrackerFrame(String lidarKey, List<TrackedObject> trackedObjects) {
        lastLiDarWorkerTrackersFrame.putIfAbsent(lidarKey, trackedObjects);
    }

    public void setPoses(List<Pose> poses) {
        this.poses = poses;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public static class Statistics {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private Map<String, LandMark> landmarks;

        public Statistics(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks, Map<String, LandMark> landmarks) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.landmarks = landmarks;
        }

        public int getSystemRuntime() {
            return systemRuntime;
        }

        public int getNumDetectedObjects() {
            return numDetectedObjects;
        }

        public int getNumTrackedObjects() {
            return numTrackedObjects;
        }

        public int getNumLandmarks() {
            return numLandmarks;
        }

        public Map<String, LandMark> getLandmarks() {
            return landmarks;
        }
    }
}