package bgu.spl.mics.application.services;

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
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast e) -> {
            terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast e) -> {
            terminate();
        });

        int currentTick = 1;
        while (currentTick <= duration) {
            try {
                Thread.sleep(tickTime * 200);
                System.out.println("Time service currentTick" + currentTick);
            } catch (InterruptedException e) {
                // ! Implement error handling
                e.printStackTrace();
            }
            sendBroadcast(new TickBroadcast(currentTick));
            statisticalFolder.incrementSystemRuntime();
            currentTick++;
        }
        sendBroadcast(new TerminatedBroadcast(TimeService.class));
        terminate();
    }
}
