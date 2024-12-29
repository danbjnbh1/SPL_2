package bgu.spl.mics.application.services;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;
    private final AtomicInteger terminationCounter;
    private final int totalSensorsNum;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam, int totalSensorsNum) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.terminationCounter = new AtomicInteger(0);
        this.totalSensorsNum = totalSensorsNum;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        this.subscribeEvent(TrackedObjectsEvent.class, (TrackedObjectsEvent e) -> {
            List<TrackedObject> trackedObjects = e.getTrackedObjects();
            fusionSlam.processTrackedObjects(trackedObjects);
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast b) -> {
            int count = terminationCounter.incrementAndGet();
            if (count >= totalSensorsNum) {
                // fusionSlam.createOutputJson();
                stop();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            stop();
        });

        this.subscribeEvent(PoseEvent.class, (PoseEvent e) -> {
            Pose pose = e.getPose();
            fusionSlam.addPose(pose);
        });

        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast e) -> {
            // ! check what to do here
        });
    }
}
