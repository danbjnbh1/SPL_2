package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    final int tickTime;
    final int duration;
    final StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     * @param latch    the CountDownLatch used to synchronize the initialization of
     *                 services
     */
    public TimeService(int TickTime, int Duration, CountDownLatch latch) {
        super("TimeService", latch);
        this.tickTime = TickTime;
        this.duration = Duration;
    }

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = TickTime;
        this.duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */
    @Override
    protected void initialize() {

        try {
            // Wait for all other services to complete their initialization
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast e) -> {
            if (e.getServiceClass() == FusionSlamService.class) {
                stop();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            System.out.println("TimeService received CrashedBroadcast");
            stop();
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast e) -> {
            int currentTick = e.getTime();
            if (currentTick < duration) {
                try {
                    Thread.sleep(tickTime * 1000);
                    System.out.println("Time service currentTick" + currentTick);
                } catch (InterruptedException ex) {
                    stop();
                }
                currentTick++;
                sendBroadcast(new TickBroadcast(currentTick));
                statisticalFolder.incrementSystemRuntime();

            } else {
                stop();
            }
        });

        //Start the timer
        try {
            Thread.sleep(tickTime * 1000);
        } catch (InterruptedException ex) {
            stop();
        }
        sendBroadcast(new TickBroadcast(1));
        statisticalFolder.incrementSystemRuntime();
    }
}
