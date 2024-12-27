package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

public class TrackedObjectsEvent implements Event<String> {

    public List<TrackedObject> getTrackedObjects() {
        // OR: remove the null and implement this
        return null;
    }
}
