package bgu.spl.mics.application.services;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
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
            int currentTime = e.getTime();
            TrackedObjectsEvent trackedObjectsEvent = liDarWorkerTracker.generateTrackedObjectsEvent(currentTime);
            List<TrackedObject> trackedObjects = trackedObjectsEvent.getTrackedObjects();
            String error = getDetectedError(trackedObjects);

            if (error != null) {
                liDarWorkerTracker.setStatus(STATUS.ERROR);
                this.sendBroadcast(new CrashedBroadcast(error, this.getName()));
                terminate();
                return;
            }

            if (trackedObjects != null) {
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
            stop();
        });

    }

    String getDetectedError(List<TrackedObject> trackedObjects) {
        if (trackedObjects == null) {
            return null;
        }

        for (TrackedObject trackedObject : trackedObjects) {
            if (trackedObject.getId() == "ERROR") {
                return "Lidar " + trackedObject.getDescription();
            }
        }

        return null;
    }

}
