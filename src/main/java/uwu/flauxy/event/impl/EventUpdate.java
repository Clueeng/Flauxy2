package uwu.flauxy.event.impl;

import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;

public class EventUpdate extends Event {
    public boolean isPre() {
        return this.getType().equals(EventType.PRE);
    }
}
