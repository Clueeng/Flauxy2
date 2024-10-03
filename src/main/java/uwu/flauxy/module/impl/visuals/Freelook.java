package uwu.flauxy.module.impl.visuals;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.utils.Wrapper;


@ModuleInfo(name = "Freelook", displayName = "Free Look", key = Keyboard.KEY_F, cat = Category.Visuals)
public class Freelook extends Module {

    float yaw, pitch;
    BooleanSetting fixOverflow = new BooleanSetting("Fix Overflow",true);

    public Freelook(){
        addSetting(fixOverflow);
    }

    @Override
    public void onEnable() {
        if(!Keyboard.isKeyDown(key) || mc.currentScreen != null){
            Wrapper.instance.log("This module requires you to hold the bound key to work");
        }
        if(fixOverflow.isEnabled()){
            yaw = mc.thePlayer.rotationYaw % 360;
            pitch = mc.thePlayer.rotationPitch % 360;
        }else{
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(ev.isPre()){
                mc.gameSettings.thirdPersonView = 1;
                mc.thePlayer.rotationYaw = mc.thePlayer.prevRotationYaw;
            }
            if(!Keyboard.isKeyDown(this.key)){
                mc.gameSettings.thirdPersonView = 0;
                this.toggle();
            }
        }
    }

    public float getYaw(){
        return this.isToggled() ? yaw : mc.thePlayer.rotationYaw;
    }
    public float getPitch(){
        return this.isToggled() ? pitch : mc.thePlayer.rotationPitch;
    }
    public boolean overrideMouse(){
        if(mc.inGameHasFocus && Display.isActive()){
            if(!this.toggled){
                return true;
            }
            mc.mouseHelper.mouseXYChange();
            float f1 = mc.gameSettings.mouseSensitivity + 0.2f;
            float f2 = (f1 * 2.0f * 1.0f);
            float f3 = (float) mc.mouseHelper.deltaX * f2;
            float f4 = (float) mc.mouseHelper.deltaY * f2;

            yaw += f3 * 0.15f;
            pitch -= f4 * 0.15f; // +/- = invert
            if(pitch > 90) pitch = 90;
            if(pitch < -90) pitch = -90;

        }
        return false;
    }


}
