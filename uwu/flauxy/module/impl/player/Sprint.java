package uwu.flauxy.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

@ModuleInfo(name = "Sprint", displayName = "Sprint", key = Keyboard.KEY_B, cat = Category.PLAYER)
public class Sprint extends Module {

    @EventTarget
    public void onMove(EventMotion ev){
        mc.thePlayer.setSprinting(true);
    }

}
