package uwu.noctura.module.impl.player;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.utils.MoveUtils;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.timer.Timer;

import java.util.ArrayList;

@ModuleInfo(name = "Nofall", displayName = "No Fall", key = -1, cat = Category.Player)
public class Nofall extends Module {

    public static ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Test", "Spoof", "Legit", "BlocksMC");
    boolean can;
    BlockPos lastGround = BlockPos.ORIGIN;
    private Timer clutchTimer = new Timer();
    ArrayList<Packet> blinkPackets = new ArrayList<>();
    double fallY = 0;
    int tick = 0;

    public Nofall() {
        this.addSettings(mode);
    }

    public void onEvent(Event e) {

        if(mode.is("Test")){

            if(e instanceof EventMotion){
                EventMotion ev = (EventMotion) e;
                if(MoveUtils.distToGround() > 238) return;
                if(MoveUtils.distToGround() <= 3) return;
                if(mc.thePlayer.fallDistance > 4.0f){
                    if(mc.thePlayer.hurtTime > 0){
                        mc.thePlayer.motionY = -2.95;
                    }
                    can = true;
                }
                if(mc.thePlayer.onGround){
                    mc.timer.timerSpeed = 1f;
                }
                if(can){
                    if(tick > 0){
                        if(mc.thePlayer.motionY > -0.6){
                            mc.thePlayer.motionY = -0.09800000190734863;
                        }
                        //BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn
                        BlockPos positionin = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                        ItemStack held = mc.thePlayer.inventory.getItemStack();
                        if(ev.isPre())
                            PacketUtil.sendSilentPacket(new C08PacketPlayerBlockPlacement(positionin, 255, held, 0, 0, 0));

                        ev.setOnGround(true);
                    }
                    tick++;
                    if(tick > 2){
                        mc.timer.timerSpeed = 1f;
                        tick = 0;
                        can = false;
                        mc.thePlayer.fallDistance = 0;
                    }
                }
            }

        } // 8

        if(e instanceof EventSendPacket){
            EventSendPacket event = (EventSendPacket) e;
            switch (mode.getMode()){
                case "BlocksMC":{
                    if(can){
                        if(PacketUtil.isPacketBlinkPacket(event.getPacket()) || PacketUtil.isPacketPingSpoof(event.getPacket())){
                            event.setCancelled(true);
                            blinkPackets.add(event.getPacket());
                        }
                    }
                    break;
                }
            }
        }
        if (e instanceof EventMotion) {
            this.setArrayListName("Nofall " + EnumChatFormatting.WHITE + mode.getMode());
            EventMotion event = (EventMotion) e;
            switch(mode.getMode()){
                case "BlocksMC":{
                    if(MoveUtils.isOverVoid()){
                        can = false;
                        return;
                    }
                    if(mc.thePlayer.ticksExisted < 10){
                        can = false;
                    }
                    if(mc.thePlayer.onGround){
                        can = false;
                        while(!blinkPackets.isEmpty()){
                            System.out.println("beep: " + blinkPackets.size());
                            PacketUtil.sendSilentPacket(blinkPackets.get(0));
                            blinkPackets.remove(0);
                        }
                        if(Math.abs(fallY - mc.thePlayer.posY) > 3){ // means we fell and landed, update
                            fallY = mc.thePlayer.posY;
                        }else{ // means we're still at the top

                        }
                    }else{
                        if(mc.thePlayer.fallDistance > 2){
                            fallY = mc.thePlayer.posY;
                            can = true;
                            if(!blinkPackets.isEmpty()){
                                event.setY(mc.thePlayer.posY % (1 / .5D));
                                //event.setOnGround(true);
                                PacketUtil.sendSilentPacket(new C03PacketPlayer(true));
                            }
                        }
                    }
                    break;
                }

                case "Packet":{
                    if((int)mc.thePlayer.fallDistance % 3 == 0){
                        PacketUtil.packetNoEvent(new C03PacketPlayer(true));
                    }
                    break;
                }
                case "Spoof":{
                    if(mc.thePlayer.fallDistance >= 2.5){
                        event.setOnGround(true);
                        mc.thePlayer.fallDistance = 0;
                    }
                    break;
                }
            }
        }
    }

    public boolean posSame(BlockPos pos1, BlockPos pos2) {
        return pos1.getX() == pos2.getX() &&
                pos1.getY() == pos2.getY() &&
                pos1.getZ() == pos2.getZ();
    }



}
