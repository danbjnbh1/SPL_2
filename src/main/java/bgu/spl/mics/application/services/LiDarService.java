package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

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
            sendEvent(trackedObjectsEvent);
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
            terminate(); // ! Implement error handling
        });

    }
}
