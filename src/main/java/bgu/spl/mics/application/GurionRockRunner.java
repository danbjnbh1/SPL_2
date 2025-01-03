package bgu.spl.mics.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Configuration.Cameras.CameraConfiguration;
import bgu.spl.mics.application.Configuration.Lidars.LidarConfiguration;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.CameraDataBase;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the
     *             path to the configuration file.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Hello World!");
            String configPath = args[0];

            Configuration config = Configuration.ConfigurationParser.parseConfiguration(configPath);

            // Get the absolute path of the configuration file
            Path configAbsolutePath = Paths.get(configPath).toAbsolutePath().getParent();

            // Build absolute paths for the other files
            String poseJsonFilePath = configAbsolutePath.resolve(config.getPoseJsonFile()).toString();
            String cameraDataPath = configAbsolutePath.resolve(config.getCameras().getCameraDatasPath()).toString();
            String lidarDataPath = configAbsolutePath.resolve(config.getLidars().getLidarsDataPath()).toString();

            GPSIMU gpsimu = new GPSIMU(poseJsonFilePath);
            MicroService poseService = new PoseService(gpsimu);

            List<MicroService> cameraServices = new ArrayList<>();
            CameraDataBase cameraDataBase = new CameraDataBase(cameraDataPath);
            for (CameraConfiguration cameraConfig : config.getCameras().getCamerasConfigurations()) {
                Camera camera = new Camera(cameraConfig.getId(), cameraConfig.getFrequency(),
                        cameraConfig.getCameraKey(), cameraDataBase);
                MicroService cameraService = new CameraService(camera);
                cameraServices.add(cameraService);
            }

            LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(lidarDataPath);
            List<MicroService> lidarServices = new ArrayList<>();
            for (LidarConfiguration lidarConfig : config.getLidars().getLidarConfigurations()) {
                LiDarWorkerTracker lidar = new LiDarWorkerTracker(lidarConfig.getId(), lidarConfig.getFrequency(),
                        lidarDataBase);
                MicroService lidarService = new LiDarService(lidar);
                lidarServices.add(lidarService);
            }

            int totalSensorsNum = cameraServices.size() + lidarServices.size() + 1; // +1 for GPSIMU
            MicroService fusionSlamService = new FusionSlamService(FusionSlam.getInstance(), totalSensorsNum);

            MicroService timeService = new TimeService(config.getTickTime(), config.getDuration());

            // Create a thread pool
            ExecutorService executorService = Executors
                    .newFixedThreadPool(cameraServices.size() + lidarServices.size() + 3); // +3 for GPSIMU, FusionSlam, and TimeService

            // Submit the pose service
            executorService.submit(poseService);

            // Submit the camera services
            for (MicroService cameraService : cameraServices) {
                executorService.submit(cameraService);
            }

            // Submit the LiDAR services
            for (MicroService lidarService : lidarServices) {
                executorService.submit(lidarService);
            }

            // Submit the FusionSlam service
            executorService.submit(fusionSlamService);

            // Submit the TimeService
            executorService.submit(timeService);

            // Shutdown the executor service and wait for all tasks to complete
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            System.out.println(StatisticalFolder.getInstance().getNumDetectedObjects());
            System.out.println(StatisticalFolder.getInstance().getNumLandmarks());
            System.out.println(StatisticalFolder.getInstance().getNumTrackedObjects());
            System.out.println(StatisticalFolder.getInstance().getSystemRuntime());
            OutputWriter.writeOutput(configPath);

            // TODO: Parse configuration file.
            // TODO: Initialize system components and services.
            // TODO: Start the simulation.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
