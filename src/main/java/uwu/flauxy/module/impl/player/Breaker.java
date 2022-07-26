package uwu.flauxy.module.impl.player;

import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

@ModuleInfo(name = "Breaker", displayName = "Breaker", key = -1, cat = Category.Player)
public class Breaker extends Module {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){

        }
    }
}
