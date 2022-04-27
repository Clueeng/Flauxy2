package uwu.flauxy.module.impl.movement;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Motion", "Motion", "NCP", "Verus");
    public ModeSetting ncpMode = new ModeSetting("NCP Mode", "Funcraft","Funcraft").setCanShow(m -> mode.is("NCP"));
    public NumberSetting speed = new NumberSetting("Speed", 0.4, 0.2, 5, 0.05).setCanShow(m -> mode.is("Motion") || (mode.is("NCP") && ncpMode.is("Funcraft")) );
    public NumberSetting timer = new NumberSetting("Timer", 1, 1, 2, 0.05).setCanShow(m -> mode.is("NCP") && ncpMode.is("Funcraft"));

    float speedFC = 0f;
    public Fly(){
        addSettings(mode, ncpMode, speed, timer);
    }

    @Override
    public void onEvent(Event e){
        if(e instanceof EventMove){
            EventMove event = (EventMove) e;
            if(mode.is("NCP") && ncpMode.is("Funcraft")){
                speed.setMaximum(0.9);
                speed.setValue(speed.getValue() > speed.getMaximum() ? speed.getMaximum() : speed.getValue());
            }
            if(!mode.is("NCP")){
                speed.setMaximum(5);
            }
            switch(mode.getMode()){
                case "NCP":{
                    switch(ncpMode.getMode()){
                        case "Funcraft":{
                            mc.timer.timerSpeed = (float) timer.getValue();
                            if(mc.thePlayer.isCollidedVertically){
                                event.setY(mc.thePlayer.motionY = 0.42f);
                                speedFC = (float) speed.getValue();
                                MoveUtils.strafe(speedFC);
                            }else{
                                event.setY(mc.thePlayer.motionY = -1e-14);
                                mc.thePlayer.jumpMovementFactor = 0F;
                                MoveUtils.strafe();
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.jumpMovementFactor = 0.02F;
        mc.thePlayer.speedInAir = 0.02F;

    }
}
