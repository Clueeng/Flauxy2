package uwu.flauxy.module.impl.player;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Nofall", displayName = "No Fall", key = -1, cat = Category.Player)
public class Nofall extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "On Ground", "Verus");

    public Nofall(){
        addSettings(mode);
    }
    BlockPos oldPos;
    boolean removeBlock = false;
    private Timer timer = new Timer();

    @Override
    public void onEvent(Event ev){
        if(ev instanceof EventMotion){
            EventMotion event = (EventMotion)ev;
            switch(mode.getMode()){
                case "Verus":{
                    if(mc.thePlayer.fallDistance >= 1f){
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + (mc.thePlayer.motionX * 2), mc.thePlayer.posY - 1, mc.thePlayer.posZ + (mc.thePlayer.motionZ * 2));
                        if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir){
                            oldPos =  pos;
                            mc.theWorld.setBlock(pos, Blocks.barrier);
                            Wrapper.instance.log(pos + " " + mc.theWorld.getBlockState(pos));
                            mc.thePlayer.fallDistance = 0;
                        }
                    }
                    if(mc.thePlayer.fallDistance >= 0 && mc.thePlayer.fallDistance <= 0.5f && oldPos != null && mc.theWorld.getBlockState(oldPos).getBlock() instanceof BlockBarrier){
                        removeBlock = true;
                    }
                    if(removeBlock){
                        if(timer.hasTimeElapsed(200, true)){
                            mc.theWorld.setBlockToAir(oldPos);
                            removeBlock = false;
                        }
                    }
                    break;
                }
                case "Packet":{
                    if(mc.thePlayer.fallDistance > 2.5f) PacketUtil.packetNoEvent(new C03PacketPlayer(true));
                    break;
                }
                case "On Ground":{
                    event.onGround = true;
                    break;
                }
            }
        }
    }

}
