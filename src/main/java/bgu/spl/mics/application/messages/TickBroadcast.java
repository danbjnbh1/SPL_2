package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private final int tick;
    private final int tickTime;

    public TickBroadcast(int tick, int tickTime) {
        this.tick = tick;
        this.tickTime = tickTime;
    }

    public int getTime() {
        return tick * tickTime;
    }
}
