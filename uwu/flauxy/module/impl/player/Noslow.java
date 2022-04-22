package uwu.flauxy.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import uwu.flauxy.event.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

@ModuleInfo(name = "Noslow", displayName = "No Slow", key = -1, cat = Category.Player)
public class Noslow extends Module {

    @EventTarget
    public void onMotion(EventMotion event){

    }

}
