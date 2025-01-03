package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OutputData {
    private static OutputData instance;
    private Statistics statistics;
    private Map<String, LandMark> landMarks;
    private String error;
    private String faultySensor;
    private Map<String, StampedDetectedObjects> lastCamerasFrame;
    private Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame;
    private List<Pose> poses;

    private static class OutputDataHolder {
        private static OutputData instance = new OutputData();
    }

    private OutputData() {
        lastCamerasFrame = new ConcurrentHashMap<>();
        lastLiDarWorkerTrackersFrame = new ConcurrentHashMap<>();
        poses = new ArrayList<>();
    }

    public static OutputData getInstance() {
        return OutputDataHolder.instance;
    }

    public void setLastCameraFrame(String cameraKey, StampedDetectedObjects detectedObjects) {
        lastCamerasFrame.putIfAbsent(cameraKey, detectedObjects);
    }

    public void setLastLiDarWorkerTrackerFrame(String liDarWorkerKey, List<TrackedObject> cloudPoints) {
        lastLiDarWorkerTrackersFrame.putIfAbsent(liDarWorkerKey, cloudPoints);
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void setPoses(List<Pose> poses) {
        this.poses = poses;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void setLandMarks(Map<String, LandMark> landMarks) {
        this.landMarks = landMarks;
    }

    public static class Statistics {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;

        public Statistics(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks) {
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
        }
    }
}