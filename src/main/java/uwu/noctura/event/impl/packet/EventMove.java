package uwu.noctura.event.impl.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uwu.noctura.event.Event;

@Getter
@Setter
@AllArgsConstructor
public final class EventMove extends Event {
    private double x, y, z;
}