package uwu.noctura.event;

import lombok.Getter;
import lombok.Setter;

public class Event<T> {

    @Getter @Setter
    public EventType type;
    @Getter @Setter
    public EventDirection direction;
    @Getter @Setter
    public boolean cancelled;

}