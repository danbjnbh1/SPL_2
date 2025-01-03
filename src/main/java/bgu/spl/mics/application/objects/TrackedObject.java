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
    private final List<CloudPoint> coordinates;

    public TrackedObject(String id, String description, int time, List<CloudPoint> cloudPoints) {
        this.id = id;
        this.description = description;
        this.time = time;
        this.coordinates = cloudPoints;
    }

    public TrackedObject(TrackedObject other) {
        this.id = other.id;
        this.description = other.description;
        this.time = other.time;
        this.coordinates = other.coordinates;
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

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }
}
