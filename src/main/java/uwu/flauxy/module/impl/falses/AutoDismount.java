package uwu.flauxy.module.impl.falses;

import net.minecraft.util.WeightedRandom;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "AutoDismount", key = -1, cat = Category.False, displayName = "Auto Dismount")
public class AutoDismount extends Module {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate)e;
            if(ev.getType().equals(EventType.PRE) || mc.currentScreen != null)return;
            mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
            if(mc.thePlayer.ridingEntity != null){
                mc.gameSettings.keyBindSneak.pressed = true;
            }
        }
    }
}
