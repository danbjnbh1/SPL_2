package bgu.spl.mics.application.objects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LidarDataBaseTest {

    private static LiDarDataBase lidarDataBase;

    @BeforeAll
    public static void setUp() {
        // Initialize the LiDarDataBase instance with the path to the JSON file
        LiDarDataBase.init("src/test/resources/lidar_data.json");
        lidarDataBase = LiDarDataBase.getInstance();
    }

    @Test
    public void testSingletonInstance() {
        // Ensure that the singleton instance is not null
        assertNotNull(lidarDataBase);
    }

    @Test
    public void testParseCloudPoints() {
        // Ensure that the cloudPoints list is not null and has the expected size
        List<StampedCloudPoints> cloudPoints = lidarDataBase.getCloudPoints();
        assertNotNull(cloudPoints);
        assertEquals(13, cloudPoints.size()); // Assuming the JSON file has 13 entries
    }

    @Test
    public void testFirstCloudPoint() {
        // Ensure that the first cloud point has the expected values
        StampedCloudPoints firstPoint = lidarDataBase.getCloudPoints().get(0);
        assertEquals(2, firstPoint.getTime());
        assertEquals("Wall_1", firstPoint.getId());
        assertEquals(2, firstPoint.getCloudPoints().size());
        // assertArrayEquals(new double[]{0.1176, 3.6969, 0.104}, firstPoint.getCloudPoints().get(0));
    }
}