package uwu.noctura.module.impl.other;

import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

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
