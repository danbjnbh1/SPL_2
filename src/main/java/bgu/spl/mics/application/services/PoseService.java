package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ErrorOutputData;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    GPSIMU gpsimu;
    ErrorOutputData errorOutputData = ErrorOutputData.getInstance();

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     * @param latch  the CountDownLatch used to synchronize the initialization of services
     */
    public PoseService(GPSIMU gpsimu, CountDownLatch latch) {
        super("PoseService", latch);
        this.gpsimu = gpsimu;

    }

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;

    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast e) -> {
            gpsimu.updateTime(e.getTime());
            messageBus.sendEvent(new PoseEvent(gpsimu.getPose()));

            if (gpsimu.getStatus() == STATUS.DOWN) {
                stop();
            }
        });

        this.subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast e) -> {
            if (e.getServiceClass() == TimeService.class) {
                stop();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            errorOutputData.setPoses(gpsimu.getPosesUntilNow());
            stop();
        });
    };
}
