package uwu.noctura.module.impl.movement;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventCollide;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.MoveUtils;

@ModuleInfo(name = "Step", displayName = "Step", key = -1, cat = Category.Movement)
public class Step extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vulcan", "Vulcan", "Vanilla");
    public NumberSetting stepHeight = new NumberSetting("Height", 1,0.5,5,0.25);

    public Step(){
        addSettings(mode, stepHeight);
    }

    public float oldStep;

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
                if(e instanceof EventMotion){
                    EventMotion em = (EventMotion) e;
                    if(mc.thePlayer.isSneaking() || !(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0))return;
                    mc.thePlayer.motionY = 0.4199;
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
