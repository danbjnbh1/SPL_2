package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 */
public class FusionSlam {
    private final List<LandMark> landmarks = new ArrayList<>();
    private final List<Pose> poses = new ArrayList<>();

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public void addPose(Pose pose) {
        poses.add(pose);
    }

    public void processTrackedObjects(List<TrackedObject> trackedObjects) {
        for (TrackedObject trackedObject : trackedObjects) {
            if (isKnownObject(trackedObject)) {
                // updateObject(trackedObject);
            } else {
                // addObject(trackedObject);
            }
        }
    }

    private boolean isKnownObject(TrackedObject trackedObject) {
        for (LandMark landmark : landmarks) {
            if (landmark.getId().equals(trackedObject.getId())) {
                return true;
            }
        }
        return false;
    }

    private List<CloudPoint> transformToGlobalCoordinates(List<CloudPoint> localCoordinates) {
        return null;
    }
}
