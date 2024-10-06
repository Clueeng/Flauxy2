package uwu.noctura.module.impl.falses;

import net.minecraft.util.BlockPos;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.Wrapper;

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
