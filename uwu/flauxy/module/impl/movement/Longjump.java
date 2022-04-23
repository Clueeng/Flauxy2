package uwu.flauxy.module.impl.movement;

import net.minecraft.util.MovementInput;
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

@ModuleInfo(name = "Longjump", displayName = "Longjump", key = Keyboard.KEY_G, cat = Category.Movement)
public class Longjump extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Funcraft");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1).setCanShow((m) -> mode.is("Verus"));

    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "Normal").setCanShow((m) -> mode.is("Verus"));


    public Longjump(){
        addSettings(mode, verusMode, speed);
    }

    int ticks = 0;
    public void onEnable() {
        switch(mode.getMode()){
            case "Verus":{
                ticks = 0;
                switch(verusMode.getMode()){
                    case "Damage":
                        MoveUtils.damage(MoveUtils.Bypass.VERUS);
                        break;
                    case "Normal":
                        MoveUtils.strafe(0.334f);
                        mc.thePlayer.jump();
                }
            }
        }
    }
    public void onDisable() {
        MoveUtils.motionreset();
        switch(mode.getMode()){
            case "Verus":{
                switch(verusMode.getMode()){
                    case "Normal":{
                        MoveUtils.strafe(0.42f);
                        break;
                    }
                }
                break;
            }
        }
    }
    @Override
    public void onEvent(Event ev){
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Damage":{
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                            }
                            int tik = 15;
                            if(ticks < tik){
                                MoveUtils.strafe(speed.getValue());
                                mc.thePlayer.motionY = 0.1;
                            }else{
                                if(ticks < tik+1){
                                    MoveUtils.strafe(0.15f);
                                }
                                Wrapper.instance.log(mc.thePlayer.fallDistance  + "");
                                if(((Math.round(mc.thePlayer.posY) * 100) / 100) == (int)mc.thePlayer.posY && mc.thePlayer.fallDistance > 0.75){
                                    MoveUtils.strafe(0.27f);
                                    mc.thePlayer.motionY = 0.42f;
                                    mc.thePlayer.fallDistance = 0;

                                }
                            }
                            ticks++;
                            break;
                        }
                        case "Normal":{
                            if(mc.thePlayer.ticksExisted % 4 == 0){
                                mc.thePlayer.motionY = 0.42F;
                                MoveUtils.strafe(0.34f);
                            }else{
                                MoveUtils.strafe(0.12f);
                            }
                            ticks++;
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

}
