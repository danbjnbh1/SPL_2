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

    // ! check about what if qw get the objects before we have the pose, can
    // happen??
    public void processTrackedObjects(List<TrackedObject> trackedObjects) {
        for (TrackedObject trackedObject : trackedObjects) {
            LandMark existingLandMark = getLandMark(trackedObject);
            if (existingLandMark != null) {
                updateLandMark(trackedObject, existingLandMark);
            } else {
                addLandMark(trackedObject);
            }
        }
    }

    private LandMark getLandMark(TrackedObject trackedObject) {
        for (LandMark landmark : landmarks) {
            if (landmark.getId().equals(trackedObject.getId())) {
                return landmark;
            }
        }
        return null;
    }

    private void updateLandMark(TrackedObject trackedObject, LandMark landmark) {
        List<CloudPoint> newCloudPoints = getObjectGlobalPoints(trackedObject);
        List<CloudPoint> existingCloudPoints = landmark.getCoordinates();
        List<CloudPoint> updatedCloudPoints = new ArrayList<>();

        int minSize = Math.min(existingCloudPoints.size(), newCloudPoints.size());
        for (int i = 0; i < minSize; i++) {
            CloudPoint newPoint = newCloudPoints.get(i);
            CloudPoint existingPoint = existingCloudPoints.get(i);

            double avgX = (newPoint.getX() + existingPoint.getX()) / 2;
            double avgY = (newPoint.getY() + existingPoint.getY()) / 2;

            updatedCloudPoints.add(new CloudPoint(avgX, avgY));
        }

        // Add any new points that were not in the existing list
        if (newCloudPoints.size() > existingCloudPoints.size()) {
            updatedCloudPoints.addAll(newCloudPoints.subList(existingCloudPoints.size(), newCloudPoints.size()));
        }

        landmark.setCoordinates(updatedCloudPoints);

    }

    private void addLandMark(TrackedObject trackedObject) {
        List<CloudPoint> globalCloudPoints = getObjectGlobalPoints(trackedObject);

        LandMark landMark = new LandMark(trackedObject.getId(), trackedObject.getDescription(), globalCloudPoints);
        landmarks.add(landMark);
    }

    private CloudPoint transformToGlobalPoint(Pose pose, CloudPoint localPoint) {
        final double localX = localPoint.getX();
        final double localY = localPoint.getY();

        double yawRadians = Math.toRadians(pose.getYaw());
        double cos = Math.cos(yawRadians);
        double sin = Math.sin(yawRadians);

        final double globalX = cos * localX - sin * localY + pose.getX();
        final double globalY = sin * localX + cos * localY + pose.getY();
        return new CloudPoint(globalX, globalY);
    }

    private List<CloudPoint> getObjectGlobalPoints(TrackedObject trackedObject) {
        Pose pose = getPoseByTime(trackedObject.getTime());
        List<CloudPoint> localPoints = trackedObject.getCloudPoints();

        List<CloudPoint> globalPoints = new ArrayList<>();
        for (CloudPoint localPoint : localPoints) {
            globalPoints.add(transformToGlobalPoint(pose, localPoint));
        }
        return globalPoints;
    }

    private Pose getPoseByTime(int time) {
        for (Pose pose : poses) {
            if (pose.getTime() == time) {
                return pose;
            }
        }
        return null;
    }
}
