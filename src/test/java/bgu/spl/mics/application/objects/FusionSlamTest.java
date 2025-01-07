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
                assertEquals(1, waitingPoseTrackedObjects.size(),
                                "There should be one tracked object in the waiting list");
                assertEquals("1", waitingPoseTrackedObjects.get(0).getId(), "The tracked object ID should be '1'");
                assertEquals("Test Object", waitingPoseTrackedObjects.get(0).getDescription(),
                                "The tracked object description should be 'Test Object'");
        }
}