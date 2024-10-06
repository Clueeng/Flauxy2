package uwu.noctura.module.impl.player;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

@ModuleInfo(name = "Breaker", displayName = "Breaker", key = -1, cat = Category.Player)
public class Breaker extends Module {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){

        }
    }
}
