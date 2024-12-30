package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    
    private int time;
    private String id;
    private List<CloudPoint> cloudPoints;

    /**
     * Constructor for StampedCloudPoints.
     *
     * @param cloudPoints A 2D array of cloud points.
     * @param time The timestamp of the cloud points.
     */
    public StampedCloudPoints(List<CloudPoint> cloudPoints, int time, String id) {
        this.cloudPoints = cloudPoints;
        this.time = time;
        this.id = id;
    }

    /**
     * Returns the cloud points.
     *
     * @return The cloud points.
     */
    public List<CloudPoint> getCloudPoints() {
        return cloudPoints;
    }

    /**
     * Returns the timestamp of the cloud points.
     *
     * @return The timestamp of the cloud points.
     */
    public int getTime() {
        return time;
    }

    public String getId() {
        return id;
    }
}
