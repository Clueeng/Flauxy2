package uwu.noctura.module.impl.falses;

import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

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
