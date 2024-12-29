package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description,
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private final String id;
    private final String description;
    private final int time;
    private final List<CloudPoint> cloudPoints;

    public TrackedObject(String id, String description, int time, List<CloudPoint> cloudPoints) {
        this.id = id;
        this.description = description;
        this.time = time;
        this.cloudPoints = cloudPoints;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getTime() {
        return time;
    }

    public List<CloudPoint> getCloudPoints() {
        return cloudPoints;
    }
}
