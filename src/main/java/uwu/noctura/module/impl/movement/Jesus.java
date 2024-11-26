package uwu.noctura.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventCollide;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.utils.MoveUtils;

@ModuleInfo(name = "Jesus", displayName = "Jesus", key = -1, cat = Category.Movement)
public class Jesus extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Solid", "Solid", "NCP");

    public Jesus(){
        addSettings(mode);
    }
    boolean test;
    AxisAlignedBB liquid;
    boolean nextTick;
    int waterTick;

    @Override
    public void onEvent(Event event){
        switch (mode.getMode()){
            case "Solid":{
                if (event instanceof EventCollide) {
                    //mc.gameSettings.keyBindJump.pressed = false;
                    EventCollide ec = (EventCollide) event;
                    if (!mc.thePlayer.isSneaking()) {
                        if (ec.getBlock() instanceof net.minecraft.block.BlockLiquid && ec.getPosY() < mc.thePlayer.posY){
                            liquid = AxisAlignedBB.fromBounds(
                                    ec.getPosX(),
                                    ec.getPosY(),
                                    ec.getPosZ(),
                                    ec.getPosX() + 1.0D,
                                    ec.getPosY() + 1.0D,
                                    ec.getPosZ() + 1.0D);
                            ec.setBoundingBox(liquid);
                        }
                    }
                }
                break;
            }
            case "NCP":{
                if(event instanceof EventMotion){
                    BlockPos below = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - .3, mc.thePlayer.posZ);
                    boolean aboveWater = mc.theWorld.getBlockState(below).getBlock() instanceof BlockLiquid;

                    if(aboveWater){
                        test = true;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        if(mc.thePlayer.isInWater()){
                            mc.thePlayer.motionY = 0.17f;
                            waterTick = 0;
                        }else{
                        }
                    }
                    if(test){
                        waterTick++;
                        if(waterTick == 1){
                            MoveUtils.strafe(0.15);
                        }
                    }
                    if(mc.thePlayer.onGround) test = false;
                }
                break;
            }
        }

    }

}
