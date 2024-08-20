package uwu.flauxy.module.impl.other;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "CreativeFlySpeed", displayName = "Creative", cat = Category.Other, key = -1)
public class CreativeFlySpeed extends Module {

    public NumberSetting flySpeed = new NumberSetting("Speed",0.3f,0.1f,1.0f,0.05f);

    public CreativeFlySpeed() {
        addSettings(flySpeed);
    }

    float oldSpeed;

    @Override
    public void onEnable() {
        oldSpeed = 0.2f;
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                mc.thePlayer.capabilities.setFlySpeed((float) flySpeed.getValue());
            }else{
                mc.thePlayer.capabilities.setFlySpeed(0.05f);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.setFlySpeed(0.05f);

    }
}
