package uwu.flauxy.module.impl.visuals;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
            for(Entity players : mc.theWorld.loadedEntityList){
                //if(players instanceof EntityPlayer){
                //    mc.theWorld.setLightFor(EnumSkyBlock.BLOCK, new BlockPos(players.posX, players.posY, players.posZ), 12);
                //}
            }
        }
    }
}
