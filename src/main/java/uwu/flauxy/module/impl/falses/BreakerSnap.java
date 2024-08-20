package uwu.flauxy.module.impl.falses;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "BreakerSnap", cat = Category.False, key = -1, displayName = "Breaker Snap")
public class BreakerSnap extends Module {

    @Override
    public void onEnable() {
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(ev.isPre()){
                //mc.thePlayer.rotationYaw += 1E-6f;
                BlockPos looking =  mc.objectMouseOver.getBlockPos();
                BlockPos current = mc.thePlayer.playerLocation;
                if(looking != null && current != null){
                    double dist = looking.distanceSq(current.getX(),current.getY(),current.getZ());
                    Wrapper.instance.log(dist + "");
                }
            }
        }
    }
}
