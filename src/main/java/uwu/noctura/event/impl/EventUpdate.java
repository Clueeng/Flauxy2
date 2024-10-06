package uwu.noctura.event.impl;

import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;

public class EventUpdate extends Event {
    public boolean isPre() {
        return this.getType().equals(EventType.PRE);
    }
}
