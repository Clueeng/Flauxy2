package uwu.flauxy.module.impl.movement;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;

@ModuleInfo(name = "Longjump", displayName = "Longjump", key = Keyboard.KEY_G, cat = Category.Movement)
public class Longjump extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Verus", "Verus");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1);


    public Longjump(){
        addSettings(mode, speed);
    }

    public void onEnable() {
        switch(mode.getMode()){
            case "Verus":{
                MoveUtils.damage(MoveUtils.Bypass.VERUS);
            }
        }
    }
    public void onDisable() {
        MoveUtils.motionreset();
    }
    @Override
    public void onEvent(Event ev){
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){
                case "Verus":{
                    MoveUtils.strafe(speed.getValue());
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    mc.thePlayer.motionY = 0.1;
                    break;
                }
            }
        }
    }

}
