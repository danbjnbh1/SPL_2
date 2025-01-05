package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.getLandmarks().clear(); // Clear landmarks before each test
        fusionSlam.getPoses().clear(); // Clear poses before each test
    }

    @Test
    public void testProcessTrackedObjects_AddNewLandMark() {
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(1.0, 1.0));
        cloudPoints.add(new CloudPoint(2.0, 2.0));

        TrackedObject trackedObject = new TrackedObject("1", "Test Object", 1, cloudPoints);
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);

        // Add a corresponding Pose
        Pose pose = new Pose(1, 0.0, 0.0, 0.0);
        fusionSlam.processPose(pose);

        fusionSlam.processTrackedObjects(trackedObjects);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(1, landmarks.size(), "There should be one landmark");
        assertEquals("1", landmarks.get(0).getId(), "The landmark ID should be '1'");
        assertEquals("Test Object", landmarks.get(0).getDescription(),
                "The landmark description should be 'Test Object'");
    }

    @Test
    public void testProcessTrackedObjects_UpdateExistingLandMark() {
        List<CloudPoint> initialCloudPoints = new ArrayList<>();
        initialCloudPoints.add(new CloudPoint(1.0, 1.0));
        initialCloudPoints.add(new CloudPoint(2.0, 2.0));

        LandMark initialLandMark = new LandMark("1", "Test Object", initialCloudPoints);
        fusionSlam.getLandmarks().add(initialLandMark);

        List<CloudPoint> newCloudPoints = new ArrayList<>();
        newCloudPoints.add(new CloudPoint(3.0, 3.0));
        newCloudPoints.add(new CloudPoint(4.0, 4.0));

        TrackedObject trackedObject = new TrackedObject("1", "Test Object", 1, newCloudPoints);
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);

        // Add a corresponding Pose
        Pose pose = new Pose(1, 0.0, 0.0, 0.0);
        fusionSlam.processPose(pose);

        fusionSlam.processTrackedObjects(trackedObjects);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(1, landmarks.size(), "There should still be one landmark");
        assertEquals("1", landmarks.get(0).getId(), "The landmark ID should still be '1'");
        assertEquals("Test Object", landmarks.get(0).getDescription(),
                "The landmark description should still be 'Test Object'");

        List<CloudPoint> updatedCloudPoints = landmarks.get(0).getCoordinates();
        assertEquals(2, updatedCloudPoints.size(), "The landmark should have two cloud points");
        assertEquals(2.0, updatedCloudPoints.get(0).getX(), 0.001,
                "The first cloud point X coordinate should be updated");
        assertEquals(2.0, updatedCloudPoints.get(0).getY(), 0.001,
                "The first cloud point Y coordinate should be updated");
        assertEquals(3.0, updatedCloudPoints.get(1).getX(), 0.001,
                "The second cloud point X coordinate should be updated");
        assertEquals(3.0, updatedCloudPoints.get(1).getY(), 0.001,
                "The second cloud point Y coordinate should be updated");
    }

    @Test
    public void testProcessTrackedObjects_AddToWaitingList() {
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(1.0, 1.0));
        cloudPoints.add(new CloudPoint(2.0, 2.0));

        TrackedObject trackedObject = new TrackedObject("1", "Test Object", 2, cloudPoints);
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);

        // Do not add a corresponding Pose

        fusionSlam.processTrackedObjects(trackedObjects);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(0, landmarks.size(), "There should be no landmarks");

        List<TrackedObject> waitingPoseTrackedObjects = fusionSlam.getWaitingPoseTrackedObjects();
        assertEquals(1, waitingPoseTrackedObjects.size(), "There should be one tracked object in the waiting list");
        assertEquals("1", waitingPoseTrackedObjects.get(0).getId(), "The tracked object ID should be '1'");
        assertEquals("Test Object", waitingPoseTrackedObjects.get(0).getDescription(),
                "The tracked object description should be 'Test Object'");
    }


    // @Test
    // void testProcessTrackedObjects_AddNewLandmarks() {
    // // Arrange
    // List<CloudPoint> cloudPoints1 = new ArrayList<>();
    // cloudPoints1.add(new CloudPoint(1, 1));
    // cloudPoints1.add(new CloudPoint(2, 2));

    // List<CloudPoint> cloudPoints2 = new ArrayList<>();
    // cloudPoints2.add(new CloudPoint(3, 3));
    // cloudPoints2.add(new CloudPoint(4, 4));

    // TrackedObject trackedObject1 = new TrackedObject("1", "Car", 5,
    // cloudPoints1);
    // TrackedObject trackedObject2 = new TrackedObject("2", "Bike", 5,
    // cloudPoints2);

    // List<TrackedObject> trackedObjects = new ArrayList<>();
    // trackedObjects.add(trackedObject1);
    // trackedObjects.add(trackedObject2);

    // // Act
    // fusionSlam.processTrackedObjects(trackedObjects);

    // // Assert
    // List<LandMark> landmarks = fusionSlam.getLandmarks();
    // assertEquals(2, landmarks.size(), "Two landmarks should be added.");
    // assertEquals("1", landmarks.get(0).getId(), "First landmark ID should
    // match.");
    // assertEquals("2", landmarks.get(1).getId(), "Second landmark ID should
    // match.");
    // }

    // @Test
    // void testProcessTrackedObjects_UpdateExistingLandmark() {
    // // Arrange
    // List<CloudPoint> initialCloudPoints = new ArrayList<>();
    // initialCloudPoints.add(new CloudPoint(1, 1));
    // initialCloudPoints.add(new CloudPoint(2, 2));

    // LandMark existingLandMark = new LandMark("1", "Car", initialCloudPoints);
    // fusionSlam.getLandmarks().add(existingLandMark);

    // List<CloudPoint> newCloudPoints = new ArrayList<>();
    // newCloudPoints.add(new CloudPoint(3, 3));
    // newCloudPoints.add(new CloudPoint(4, 4));

    // TrackedObject trackedObject = new TrackedObject("1", "Car", 5,
    // newCloudPoints);
    // List<TrackedObject> trackedObjects = new ArrayList<>();
    // trackedObjects.add(trackedObject);

    // // Act
    // fusionSlam.processTrackedObjects(trackedObjects);

    // // Assert
    // List<LandMark> landmarks = fusionSlam.getLandmarks();
    // assertEquals(1, landmarks.size(), "Only one landmark should exist.");
    // List<CloudPoint> updatedCloudPoints = landmarks.get(0).getCoordinates();
    // assertEquals(2, updatedCloudPoints.size(), "The landmark should have updated
    // cloud points.");
    // assertEquals(2, updatedCloudPoints.get(0).getX(), 0.001, "First updated point
    // X should be averaged.");
    // assertEquals(2, updatedCloudPoints.get(0).getY(), 0.001, "First updated point
    // Y should be averaged.");
    // }

}