package bgu.spl.mics.application.objects;

import java.util.Map;

public class OutputData {
    private Statistics statistics;
    private Map<String, LandMark> landMarks;

    public OutputData(Statistics statistics, Map<String, LandMark> landMarks) {
        this.statistics = statistics;
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