package bgu.spl.mics.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ConfigurationTest {

    private static final String CONFIG_PATH = "src/test/resources/configuration_file.json";

    @Test
    public void testParseConfiguration() {
        Configuration config = Configuration.ConfigurationParser.parseConfiguration(CONFIG_PATH);
        assertNotNull(config);

        // Verify cameras
        Configuration.Cameras cameras = config.getCameras();
        assertNotNull(cameras);
        assertEquals(2, cameras.getCamerasConfigurations().size());
        assertEquals("./camera_data.json", cameras.getCameraDatasPath());

        Configuration.Cameras.CameraConfiguration camera1 = cameras.getCamerasConfigurations().get(0);
        assertEquals(1, camera1.getId());
        assertEquals(1, camera1.getFrequency());
        assertEquals("camera1", camera1.getCameraKey());

        Configuration.Cameras.CameraConfiguration camera2 = cameras.getCamerasConfigurations().get(1);
        assertEquals(2, camera2.getId());
        assertEquals(2, camera2.getFrequency());
        assertEquals("camera2", camera2.getCameraKey());

        // Verify lidar workers
        Configuration.Lidars lidarWorkers = config.getLidars();
        assertNotNull(lidarWorkers);
        assertEquals(2, lidarWorkers.getLidarConfigurations().size());
        assertEquals("./lidar_data.json", lidarWorkers.getLidarsDataPath());

        Configuration.Lidars.LidarConfiguration lidar1 = lidarWorkers.getLidarConfigurations().get(0);
        assertEquals(1, lidar1.getId());
        assertEquals(4, lidar1.getFrequency());

        Configuration.Lidars.LidarConfiguration lidar2 = lidarWorkers.getLidarConfigurations().get(1);
        assertEquals(2, lidar2.getId());
        assertEquals(2, lidar2.getFrequency());

        // Verify other configurations
        assertEquals("./pose_data.json", config.getPoseJsonFile());
        assertEquals(1, config.getTickTime());
        assertEquals(300, config.getDuration());
    }
}