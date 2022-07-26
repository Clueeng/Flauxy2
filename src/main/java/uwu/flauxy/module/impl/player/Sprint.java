package uwu.flauxy.module.impl.player;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

@ModuleInfo(name = "Sprint", displayName = "Sprint", key = Keyboard.KEY_B, cat = Category.Player)
public class Sprint extends Module {


    @Override
    public void onEvent(Event ev){
        if(ev instanceof EventMotion){
            mc.thePlayer.setSprinting(mc.thePlayer.isMoving() && mc.thePlayer.moveForward > 0);
        }
    }

}
