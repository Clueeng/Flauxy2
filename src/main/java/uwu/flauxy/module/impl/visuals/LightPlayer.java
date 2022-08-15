package uwu.flauxy.module.impl.visuals;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.utils.Wrapper;

import java.util.ArrayList;

@ModuleInfo(name = "LightPlayer", displayName = "Light Player", key = -1, cat = Category.Visuals)
public class LightPlayer extends Module {
    ArrayList<BlockPos> pos = new ArrayList<>();
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            int lightValue = 12;
            World w = mc.thePlayer.worldObj;
            int oldValue = w.getLight(mc.thePlayer.getPosition(), true);
            BlockPos oldPos = mc.thePlayer.getPosition();
            pos.add(oldPos);
            for(BlockPos b : pos){
                w.setLightFor(EnumSkyBlock.BLOCK, oldPos, oldValue);
                Wrapper.instance.log("Set " + oldPos + " to a light value of " + oldValue);

            }
            pos.clear();
            w.setLightFor(EnumSkyBlock.BLOCK, mc.thePlayer.getPosition(), lightValue);
        }
    }
}
