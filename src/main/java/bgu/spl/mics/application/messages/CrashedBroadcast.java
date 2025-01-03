package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {
    private int crashedTime;

    public CrashedBroadcast(int crashedTime) {
        this.crashedTime = crashedTime;
    }

    public int getCrashedTime() {
        return crashedTime;
    }
}
