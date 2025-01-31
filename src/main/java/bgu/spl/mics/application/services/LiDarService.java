package bgu.spl.mics.application.services;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.ErrorOutputData;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and
 * process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker liDarWorkerTracker;
    private final StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();
    private final ErrorOutputData errorOutputData = ErrorOutputData.getInstance();

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service
     *                           will use to process data.
     * @param latch              the CountDownLatch used to synchronize the
     *                           initialization of services
     * 
     */
    public LiDarService(LiDarWorkerTracker liDarWorkerTracker, CountDownLatch latch) {
        super("LidarWorker" + liDarWorkerTracker.getId(), latch);
        this.liDarWorkerTracker = liDarWorkerTracker;
    }

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service
     *                           will use to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarWorkerTracker) {
        super("LidarWorker" + liDarWorkerTracker.getId());
        this.liDarWorkerTracker = liDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */

    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast e) -> {
            liDarWorkerTracker.updateTime(e.getTime());
            liDarWorkerTracker.updateStatus();
            List<TrackedObject> trackedObjects = liDarWorkerTracker.getCurrentTrackedObjects();
            String error = getDetectedError(trackedObjects);

            if (error != null) {
                liDarWorkerTracker.setStatus(STATUS.ERROR);
                errorOutputData.setError(error);
                errorOutputData.setFaultySensor(liDarWorkerTracker.getName());
                errorOutputData.setLastLiDarWorkerTrackerFrame(getName(), liDarWorkerTracker.getLastFrame());
                this.sendBroadcast(new CrashedBroadcast(e.getTime()));
                terminate();
                return;
            }

            if (trackedObjects != null && !trackedObjects.isEmpty()) {
                TrackedObjectsEvent trackedObjectsEvent = new TrackedObjectsEvent(trackedObjects);
                statisticalFolder.incrementTrackedObjects(trackedObjects.size());
                liDarWorkerTracker.setLastFrame(trackedObjects);
                this.sendEvent(trackedObjectsEvent);
            }

            if (liDarWorkerTracker.getStatus() == STATUS.DOWN) {
                stop();
            }

        });

        // Subscribe to DetectObjectsEvent
        this.subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent e) -> {
            liDarWorkerTracker.processDetectedObjects(e);
            // Complete the event with the detected objects
            complete(e, true);
        });

        this.subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast e) -> {
            if (e.getServiceClass() == TimeService.class) {
                stop();
            }
        });

        this.subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            errorOutputData.setLastLiDarWorkerTrackerFrame(liDarWorkerTracker.getName(),
                    liDarWorkerTracker.getLastFrame());
            stop();
        });

    }

    String getDetectedError(List<TrackedObject> trackedObjects) {
        if (trackedObjects == null) {
            return null;
        }

        for (TrackedObject trackedObject : trackedObjects) {
            if (trackedObject.getId().equals("ERROR")) {
                return "Sensor " + liDarWorkerTracker.getName() + " disconnected";
            }
        }

        return null;
    }

}
