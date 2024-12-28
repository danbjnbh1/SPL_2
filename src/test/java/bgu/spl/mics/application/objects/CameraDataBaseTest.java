package bgu.spl.mics.application.objects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraDataBaseTest {

    private static final String CAMERA_DATA_PATH = "src/test/resources/camera_data.json";

    @BeforeEach
    public void setUp() throws IOException {
        // Create test data
        Map<String, List<List<StampedDetectedObjects>>> testData = new HashMap<>();
        testData.put("camera1", Arrays.asList(
                Arrays.asList(
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_2", "Wall")
                        ), 1),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall"),
                                new DetectedObject("Door_1", "Door"),
                                new DetectedObject("Wall_2", "Wall")
                        ), 2),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("Wall_4", "Wall"),
                                new DetectedObject("Chair_Base_1", "Chair Base")
                        ), 3),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Chair_Base_1", "Chair Base"),
                                new DetectedObject("Circular_Base_1", "Circular Base")
                        ), 4),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Door_1", "Door"),
                                new DetectedObject("Circular_Base_1", "Circular Base")
                        ), 5),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("furniture_1", "A White Wooden Closet"),
                                new DetectedObject("furniture_2", "A Drawer unit")
                        ), 6),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("furniture_1", "A White Wooden Closet"),
                                new DetectedObject("furniture_2", "A Drawer unit"),
                                new DetectedObject("furniture_3", "Bed")
                        ), 7),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Chair_Base_2", "A Blue Chair with wooden legs"),
                                new DetectedObject("ERROR", "GLaDOS has repurposed the robot to conduct endless cake-fetching tests. Success is a lie."),
                                new DetectedObject("furniture_3", "Bed")
                        ), 8),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Bin", "A bin")
                        ), 9),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("Wall_4", "Wall")
                        ), 10),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Circular_Base_1", "Circular Base")
                        ), 11),
                        new StampedDetectedObjects(Arrays.asList(), 12),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall"),
                                new DetectedObject("Wall_2", "Wall")
                        ), 13)
                )
        ));
        testData.put("camera2", Arrays.asList(
                Arrays.asList(
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall")
                        ), 1),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("Circular_Base_1", "Circular Base")
                        ), 2),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall"),
                                new DetectedObject("Circular_Base_1", "Circular Base")
                        ), 3),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall"),
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("Wall_4", "Wall")
                        ), 4),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall"),
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("Chair_Base_1", "Chair Base")
                        ), 5),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Door_2", "Door"),
                                new DetectedObject("Wall_5", "Wall"),
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("furniture_3", "Bed")
                        ), 6),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Door_2", "Door"),
                                new DetectedObject("Wall_5", "Wall"),
                                new DetectedObject("Wall_3", "Wall")
                        ), 7),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_4", "Wall"),
                                new DetectedObject("Wall_3", "Wall")
                        ), 8),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_6", "Wall"),
                                new DetectedObject("furniture_4", "A Drawer unit"),
                                new DetectedObject("Chair_Base_2", "A Blue Chair with wooden legs"),
                                new DetectedObject("Table_Base_1", "A Wooden Table"),
                                new DetectedObject("curtain_1", "A gray curtain")
                        ), 9),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("furniture_3", "Bed"),
                                new DetectedObject("furniture_4", "A Drawer unit"),
                                new DetectedObject("Chair_Base_2", "A Blue Chair with wooden legs"),
                                new DetectedObject("Bin", "A bin")
                        ), 10),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Chair_Base_1", "Chair Base"),
                                new DetectedObject("Wall_4", "Wall"),
                                new DetectedObject("Wall_3", "Wall"),
                                new DetectedObject("Wall_1", "Wall")
                        ), 11),
                        new StampedDetectedObjects(Arrays.asList(
                                new DetectedObject("Wall_1", "Wall"),
                                new DetectedObject("Wall_2", "Wall"),
                                new DetectedObject("Door_1", "Door"),
                                new DetectedObject("Chair_Base_1", "Chair Base"),
                                new DetectedObject("Circular_Base_1", "Circular Base")
                        ), 12),
                        new StampedDetectedObjects(Arrays.asList(), 13)
                )
        ));
    }

    @Test
    public void testParseCameras() {
        CameraDataBase cameraDataBase = new CameraDataBase(CAMERA_DATA_PATH);
        assertNotNull(cameraDataBase.getCameraData("camera1"));
        assertEquals(13, cameraDataBase.getCameraData("camera1").size());
        assertEquals(13, cameraDataBase.getCameraData("camera2").size());
    }

    @Test
    public void testGetCameraData() {
        CameraDataBase cameraDataBase = new CameraDataBase(CAMERA_DATA_PATH);
        List<StampedDetectedObjects> camera1Data = cameraDataBase.getCameraData("camera1");
        assertNotNull(camera1Data);
        assertEquals(13, camera1Data.size());

        StampedDetectedObjects firstEntry = camera1Data.get(0);
        assertEquals(1, firstEntry.getTime());
        assertEquals(1, firstEntry.getDetectedObjects().size());
        assertEquals("Wall_2", firstEntry.getDetectedObjects().get(0).getId());

        StampedDetectedObjects secondEntry = camera1Data.get(1);
        assertEquals(2, secondEntry.getTime());
        assertEquals(3, secondEntry.getDetectedObjects().size());
        assertEquals("Wall_1", secondEntry.getDetectedObjects().get(0).getId());
        assertEquals("Door_1", secondEntry.getDetectedObjects().get(1).getId());
        assertEquals("Wall_2", secondEntry.getDetectedObjects().get(2).getId());

        List<StampedDetectedObjects> camera2Data = cameraDataBase.getCameraData("camera2");
        assertNotNull(camera2Data);
        assertEquals(13, camera2Data.size());

        StampedDetectedObjects thirdEntry = camera2Data.get(0);
        assertEquals(1, thirdEntry.getTime());
        assertEquals(1, thirdEntry.getDetectedObjects().size());
        assertEquals("Wall_1", thirdEntry.getDetectedObjects().get(0).getId());

        StampedDetectedObjects fourthEntry = camera2Data.get(1);
        assertEquals(2, fourthEntry.getTime());
        assertEquals(2, fourthEntry.getDetectedObjects().size());
        assertEquals("Wall_3", fourthEntry.getDetectedObjects().get(0).getId());
        assertEquals("Circular_Base_1", fourthEntry.getDetectedObjects().get(1).getId());
    }
}