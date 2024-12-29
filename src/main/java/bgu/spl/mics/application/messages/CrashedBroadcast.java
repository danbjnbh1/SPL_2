package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {
    private String serviceName;
    private String error;

    public CrashedBroadcast(String error, String serviceName) {
        this.serviceName = serviceName;
        this.error = error;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getError() {
        return error;
    }
}
