package uwu.noctura.module.impl.movement;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventCollide;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventStrafe;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.MoveUtils;
import uwu.noctura.utils.Wrapper;

@ModuleInfo(name = "Step", displayName = "Step", key = -1, cat = Category.Movement)
public class Step extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vulcan", "Vulcan", "Vanilla");
    public NumberSetting stepHeight = new NumberSetting("Height", 1,0.5,5,0.25);

    public Step(){
        addSettings(mode, stepHeight);
    }

    public float oldStep;
    public boolean release, isStepping;
    public int stepTick;

    @Override
    public void onEnable() {
        oldStep = mc.thePlayer.stepHeight;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = oldStep;
    }

    @Override
    public void onEvent(Event e) {
        switch (mode.getMode()){
            case "Vulcan":{
                if(e instanceof EventStrafe){
                    EventStrafe ev = (EventStrafe) e;
                }

                if(e instanceof EventMotion){
                    EventMotion em = (EventMotion) e;
                    if(mc.thePlayer.onGround){
                        stepTick = 0;
                    }
                    if(mc.thePlayer.isSneaking() || !(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0)){
                        if(release){
                            mc.thePlayer.motionY = 0.00001;
                            stepTick = 0;
                        }
                        release = false;
                        return;
                    }

                    if(em.isPre()){
                        stepTick++;
                        if(stepTick == 1){
                            mc.thePlayer.motionY = 0.42f;
                        }else if(stepTick == 2){
                            mc.thePlayer.motionY = 0.24813599859094637f;
                        }else{
                            mc.thePlayer.motionY = 0.24813599859094637f;
                        }
                    }

                    release = true;
                }
                break;
            }
            case "Vanilla":{
                mc.thePlayer.stepHeight = (float) stepHeight.getValue();
                break;
            }
        }
    }
}
