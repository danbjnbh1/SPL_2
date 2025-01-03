package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.OutputData;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private final StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();
    private final OutputData outputData = OutputData.getInstance();

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService" + camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast e) -> {
            int currentTime = e.getTime();
            camera.updateTime(currentTime);

            StampedDetectedObjects detectedObjectsToPublish = camera.getDetectedObjects();
            String error = getDetectedError(detectedObjectsToPublish);

            if (error != null) {
                camera.setStatus(STATUS.ERROR);
                outputData.setFaultySensor(camera.getName());
                outputData.setError(error);
                outputData.setLastCameraFrame(camera.getName(), camera.getLastFrame());
                this.sendBroadcast(new CrashedBroadcast(currentTime));
                terminate();
                return;
            }

            if (detectedObjectsToPublish != null) {
                statisticalFolder.incrementDetectedObjects(detectedObjectsToPublish.getDetectedObjects().size());
                camera.setLastFrame(detectedObjectsToPublish);
                this.sendEvent(new DetectObjectsEvent(detectedObjectsToPublish));
            }

            if (camera.getStatus() == STATUS.DOWN) {
                stop();
            }
        });

        this.subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast e) -> {
            if (e.getServiceClass() == TimeService.class) {
                stop();
            }
        });

        this.subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            outputData.setLastCameraFrame(camera.getName(), camera.getLastFrame());
            stop();
        });
    }

    String getDetectedError(StampedDetectedObjects detectedObjectsToPublish) {
        if (detectedObjectsToPublish == null) {
            return null;
        }

        for (DetectedObject detectedObject : detectedObjectsToPublish.getDetectedObjects()) {
            if (detectedObject.getId().equals("ERROR")) {
                return detectedObject.getDescription();
            }
        }

        return null;
    }

}
