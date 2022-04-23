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
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Speed", displayName = "Speed", key = Keyboard.KEY_X, cat = Category.Movement)
public class Speed extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1);


    public Speed(){
        addSettings(mode, speed);
    }

    @Override
    public void onEvent(Event ev){
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){
                case "Vanilla":{
                    MoveUtils.strafe(speed.getValue());
                    if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                        mc.thePlayer.jump();
                    }
                    if (!mc.thePlayer.isMoving()) {
                        MoveUtils.motionreset();
                    }
                }
            }
        }
    }

}
