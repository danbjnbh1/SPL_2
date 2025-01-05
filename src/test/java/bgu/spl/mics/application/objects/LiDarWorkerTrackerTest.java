package bgu.spl.mics.application.objects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

public class LiDarWorkerTrackerTest {

    private LiDarWorkerTracker workerTracker;
    private LiDarDataBase dataBase;

    @BeforeEach
    public void setUp() {
        // Initialize the LiDarDataBase instance with the path to the JSON file
        dataBase = LiDarDataBase.getInstance("src/test/resources/lidar_data.json");
        workerTracker = new LiDarWorkerTracker(1, 5, dataBase);
        workerTracker.getLastTrackedObjects().clear(); // Clear last tracked objects before each test
        dataBase.getNumOfConsumedCloudPoints().set(0); // Reset consumed cloud points before each test

        // Add some initial tracked objects
        List<CloudPoint> cloudPoints1 = new ArrayList<>();
        cloudPoints1.add(new CloudPoint(0.1176, 3.6969));
        cloudPoints1.add(new CloudPoint(0.11362, 3.6039));
        workerTracker.getLastTrackedObjects().add(new TrackedObject("Wall_1", "Wall", 2, cloudPoints1));

        List<CloudPoint> cloudPoints2 = new ArrayList<>();
        cloudPoints2.add(new CloudPoint(3.0451, -0.38171));
        cloudPoints2.add(new CloudPoint(3.0637, -0.17392));
        workerTracker.getLastTrackedObjects().add(new TrackedObject("Wall_3", "Wall", 4, cloudPoints2));

        List<CloudPoint> cloudPoints3 = new ArrayList<>();
        cloudPoints3.add(new CloudPoint(1.9834, -1.0048));
        cloudPoints3.add(new CloudPoint(1.7235, -0.71784));
        workerTracker.getLastTrackedObjects().add(new TrackedObject("Chair_Base_1", "Chair Base", 4, cloudPoints3));

        List<CloudPoint> cloudPoints4 = new ArrayList<>();
        cloudPoints4.add(new CloudPoint(-2.5367, -3.3341));
        cloudPoints4.add(new CloudPoint(1.7926, -3.6804));
        workerTracker.getLastTrackedObjects().add(new TrackedObject("Wall_4", "Wall", 6, cloudPoints4));

        List<CloudPoint> cloudPoints5 = new ArrayList<>();
        cloudPoints5.add(new CloudPoint(0.73042, -1.1781));
        cloudPoints5.add(new CloudPoint(0.49003, -1.1433));
        workerTracker.getLastTrackedObjects()
                .add(new TrackedObject("Circular_Base_1", "Circular Base", 6, cloudPoints5));
    }

    @Test
    public void testGenerateTrackedObjectsEvent() {
        // Generate the tracked objects event at current time 9
        workerTracker.updateTime(9);
        List<TrackedObject> trackedObjects = workerTracker.getCurrentTrackedObjects();

        // Ensure that the event is not null
        assertNotNull(trackedObjects);

        // Ensure that the event contains the expected tracked objects
        assertEquals(3, trackedObjects.size());

        TrackedObject trackedObject1 = trackedObjects.get(0);
        assertEquals("Wall_1", trackedObject1.getId());
        assertEquals("Wall", trackedObject1.getDescription());
        assertEquals(2, trackedObject1.getTime());
        assertEquals(2, trackedObject1.getCoordinates().size());
        assertEquals(0.1176, trackedObject1.getCoordinates().get(0).getX());
        assertEquals(3.6969, trackedObject1.getCoordinates().get(0).getY());

        TrackedObject trackedObject2 = trackedObjects.get(1);
        assertEquals("Wall_3", trackedObject2.getId());
        assertEquals("Wall", trackedObject2.getDescription());
        assertEquals(4, trackedObject2.getTime());
        assertEquals(2, trackedObject2.getCoordinates().size());
        assertEquals(3.0451, trackedObject2.getCoordinates().get(0).getX());
        assertEquals(-0.38171, trackedObject2.getCoordinates().get(0).getY());

        TrackedObject trackedObject3 = trackedObjects.get(2);
        assertEquals("Chair_Base_1", trackedObject3.getId());
        assertEquals("Chair Base", trackedObject3.getDescription());
        assertEquals(4, trackedObject3.getTime());
        assertEquals(2, trackedObject3.getCoordinates().size());
        assertEquals(1.9834, trackedObject3.getCoordinates().get(0).getX());
        assertEquals(-1.0048, trackedObject3.getCoordinates().get(0).getY());

        List<TrackedObject> remainLastTrackedObjects = workerTracker.getLastTrackedObjects();
        assertEquals(2, remainLastTrackedObjects.size());
        TrackedObject remainObject1 = remainLastTrackedObjects.get(0);
        assertEquals("Wall_4", remainObject1.getId());
        TrackedObject remainObject2 = remainLastTrackedObjects.get(1);
        assertEquals("Circular_Base_1", remainObject2.getId());

    }

    // new tests

    @Test
    public void testProcessDetectedObjects_AddNewDetectedObject() {
        // Arrange
        List<DetectedObject> detectedObjects = new ArrayList<>();
        detectedObjects.add(new DetectedObject("1", "Test Object"));
        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(detectedObjects, 1);
        DetectObjectsEvent event = new DetectObjectsEvent(stampedDetectedObjects);

        // Preconditions
        assertNotNull(event, "The event should not be null");
        assertNotNull(event.getDetectedObjects(), "The detected objects should not be null");
        assertNotNull(event.getDetectedObjects().getDetectedObjects(),
                "The list of detected objects should not be null");
        assertNotNull(dataBase.getListOfStampedCloudPointsByTime(event.getDetectedObjects().getTime()),
                "The list of stamped cloud points should not be null");

        int initialLastTrackedObjectsSize = workerTracker.getLastTrackedObjects().size();
        int initialNumOfConsumedCloudPoints = dataBase.getNumOfConsumedCloudPoints().get();

        // Act
        workerTracker.processDetectedObjects(event);

        // Postconditions
        assertTrue(workerTracker.getLastTrackedObjects().size() >= initialLastTrackedObjectsSize,
                "The size of last tracked objects should not decrease");
        assertTrue(dataBase.getNumOfConsumedCloudPoints().get() >= initialNumOfConsumedCloudPoints,
                "The number of consumed cloud points should not decrease");
    }

    @Test
    public void testProcessDetectedObjects_UpdateExistingDetectedObject() {
        // Arrange
        List<DetectedObject> detectedObjects = new ArrayList<>();
        detectedObjects.add(new DetectedObject("1", "Test Object"));
        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(detectedObjects, 1);
        DetectObjectsEvent event = new DetectObjectsEvent(stampedDetectedObjects);

        // Add an existing tracked object
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(1.0, 1.0));
        TrackedObject trackedObject = new TrackedObject("1", "Test Object", 1, cloudPoints);
        workerTracker.getLastTrackedObjects().add(trackedObject);

        // Preconditions
        assertNotNull(event, "The event should not be null");
        assertNotNull(event.getDetectedObjects(), "The detected objects should not be null");
        assertNotNull(event.getDetectedObjects().getDetectedObjects(),
                "The list of detected objects should not be null");
        assertNotNull(dataBase.getListOfStampedCloudPointsByTime(event.getDetectedObjects().getTime()),
                "The list of stamped cloud points should not be null");

        int initialLastTrackedObjectsSize = workerTracker.getLastTrackedObjects().size();
        int initialNumOfConsumedCloudPoints = dataBase.getNumOfConsumedCloudPoints().get();

        // Act
        workerTracker.processDetectedObjects(event);

        // Postconditions
        assertTrue(workerTracker.getLastTrackedObjects().size() >= initialLastTrackedObjectsSize,
                "The size of last tracked objects should not decrease");
        assertTrue(dataBase.getNumOfConsumedCloudPoints().get() >= initialNumOfConsumedCloudPoints,
                "The number of consumed cloud points should not decrease");
    }
}