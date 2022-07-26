package uwu.flauxy.module.impl.movement;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Phase", displayName = "Phase", key = Keyboard.KEY_H, cat = Category.Movement)
public class Phase extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Vclip");
    NumberSetting clipAmount = new NumberSetting("Blocks", -3, -5, 5, 1).setCanShow(m -> mode.is("Vclip"));

    public Phase(){
        addSettings(mode, clipAmount);
    }

    @Override
    public void onEvent(Event e){
        if(e instanceof EventMotion){
            switch(mode.getMode()){
                case "Vclip":{
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + clipAmount.getValue(), mc.thePlayer.posZ);
                    Wrapper.instance.log("Clipped to the position " + (mc.thePlayer.posY + clipAmount.getValue()));
                    this.toggle();
                    break;
                }
                case "Normal":{

                    mc.thePlayer.setPosition(mc.thePlayer.posX + (mc.thePlayer.motionX * 10), mc.thePlayer.posY, mc.thePlayer.posX + (mc.thePlayer.motionZ * 10));
                    break;
                }
            }
        }
    }

}
