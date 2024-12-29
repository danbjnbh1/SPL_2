package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GPSIMUTest {

    private GPSIMU gpsimu;
    private static final String POSE_DATA_PATH = "src/test/resources/pose_data.json";

    @BeforeEach
    public void setUp() {
        gpsimu = new GPSIMU(POSE_DATA_PATH);
        gpsimu.updateTime(1);
    }

    @Test
    public void testParsePoses() {
        List<Pose> poses = gpsimu.parsePoses(POSE_DATA_PATH);
        assertNotNull(poses);
        assertFalse(poses.isEmpty());
        assertEquals(20, poses.size());

        Pose firstPose = poses.get(0);
        assertEquals(1, firstPose.getTime());
        assertEquals(0.0, firstPose.getX());
        assertEquals(0.0, firstPose.getY());
        assertEquals(0.0, firstPose.getYaw());

        Pose lastPose = poses.get(19);
        assertEquals(20, lastPose.getTime());
        assertEquals(10.5, lastPose.getX());
        assertEquals(9.0, lastPose.getY());
        assertEquals(206.26, lastPose.getYaw());
    }

    @Test
    public void testGetPose() {
        Pose pose = gpsimu.getPose();
        assertNotNull(pose);
        assertEquals(1, pose.getTime());
        assertEquals(0.0, pose.getX());
        assertEquals(0.0, pose.getY());
        assertEquals(0.0, pose.getYaw());
    }

    @Test
    public void testSetCurrentTime() {
        gpsimu.updateTime(2);
        Pose pose = gpsimu.getPose();
        assertNotNull(pose);
        assertEquals(2, pose.getTime());
        assertEquals(-3.2076, pose.getX());
        assertEquals(0.0755, pose.getY());
        assertEquals(-87.48, pose.getYaw());
    }
}