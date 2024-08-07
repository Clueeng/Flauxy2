package uwu.flauxy.module.impl.ghost;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "LegitSprint", displayName = "Legit Sprint", key = -1, cat = Category.Ghost)
public class LegitSprint extends Module {

    public LegitSprint(){
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            mc.gameSettings.keyBindSprint.pressed = true;
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
    }
}
