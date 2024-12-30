package bgu.spl.mics.application.services;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker LiDarWorkerTracker;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LidarWorker" + LiDarWorkerTracker.getId());
        this.LiDarWorkerTracker = LiDarWorkerTracker;
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
            List<StampedDetectedObjects> detectedObjectsToPublish = LiDarWorkerTracker.getDetectedObjectsByTime(currentTime);
            // Process the detected objects and publish them if necessary
            detectedObjectsToPublish.forEach(detectedObject -> {
                // Add your logic to handle each detected object
                System.out.println("Detected object at time " + currentTime + ": " + detectedObject);
            });
        });

        // Subscribe to DetectObjectsEvent
        this.subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent e) -> {
            // Handle the DetectObjectsEvent
            String lidarKey = e.getLidarKey();
            List<StampedDetectedObjects> detectedObjects = LiDarWorkerTracker.detectObjects(lidarKey);
            // Process the detected objects
            detectedObjects.forEach(detectedObject -> {
                // Add your logic to handle each detected object
                System.out.println("Detected object for LiDAR key " + lidarKey + ": " + detectedObject);
            });
            // Complete the event with the detected objects
            complete(e, detectedObjects);
        });

        System.out.println(getName() + " initialized and ready to process events.");

        
        this.subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast e) -> {
            terminate(); // ! Implement error handling
        });

        this.subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            terminate(); // ! Implement error handling
        });
        
    }
}
