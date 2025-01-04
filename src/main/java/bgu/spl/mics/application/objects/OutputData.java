package bgu.spl.mics.application.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OutputData {
    private Statistics statistics;
    private Map<String, LandMark> landMarks;

    private static class OutputDataHolder {
        private static final OutputData instance = new OutputData();
    }

    private OutputData() {
        statistics = new Statistics(0, 0, 0, 0);
        landMarks = new ConcurrentHashMap<>();
    }

    public static synchronized OutputData getInstance() {
        return OutputDataHolder.instance;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void setLandMarks(Map<String, LandMark> landMarks) {
        this.landMarks = landMarks;
    }


    public Statistics getStatistics() {
        return statistics;
    }

    public Map<String, LandMark> getLandMarks() {
        return landMarks;
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
    }
}