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
                if(event instanceof EventCollide){
                    EventCollide ec = (EventCollide) event;
                    if (!mc.thePlayer.isSneaking()) {
                        if (ec.getBlock() instanceof net.minecraft.block.BlockLiquid && ec.getPosY() < mc.thePlayer.posY){
                            test = true;
                            liquid = AxisAlignedBB.fromBounds(
                                    ec.getPosX(),
                                    ec.getPosY(),
                                    ec.getPosZ(),
                                    ec.getPosX() + 1.0D,
                                    ec.getPosY() + 1.0D,
                                    ec.getPosZ() + 1.0D);
                            ec.setBoundingBox(liquid);
                        }else{
                            test = false;
                        }
                    }
                }
                if(event instanceof EventSendPacket){
                    EventSendPacket es = (EventSendPacket) event;
                    if(test && liquid != null){
                        if(es.getPacket() instanceof C03PacketPlayer){
                            if(mc.thePlayer.boundingBox.intersectsWith(liquid)){
                                C03PacketPlayer pack = (C03PacketPlayer) es.getPacket();
                                nextTick = !nextTick;
                                if(nextTick){
                                    pack.y -= 0.001;
                                }
                            }
                        }
                    }
                }
                break;
            }
        }

    }

}
