package uwu.noctura.module.impl.player;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventPostMotionUpdate;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.BlinkUtil;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "Noslow", displayName = "No Slow", key = -1, cat = Category.Player)
public class Noslow extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP", "Reduced", "RedeSky", "Item Switch");
    NumberSetting redudeX = new NumberSetting("Reduce X", 0, 0, 100, 1).setCanShow(m -> mode.is("Reduced"));
    NumberSetting redudeZ = new NumberSetting("Reduce Z", 0, 0, 100, 1).setCanShow(m -> mode.is("Reduced"));

    public Noslow(){
        addSettings(mode, redudeX, redudeZ);
    }

    boolean blockStop;
    ConcurrentLinkedQueue<Packet> blinks = new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            this.setArrayListName("Noslow " + EnumChatFormatting.WHITE + "" + mode.getMode());
        }
        switch(mode.getMode()){
            case "Hypixel":{
                hypixel(e);
                break;
            }
            case "Item Switch":{
                if(e instanceof EventMotion){
                    EventMotion event = (EventMotion) e;
                    if(mc.thePlayer == null) return;
                    if(mc.thePlayer.getHeldItem() == null) return;
                    if(!shouldBeNoslowing()) return;
                    if(!(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)){
                        return;

                    }
                    if ((mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) && event.isPre()) {
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }
                break;
            }
            case "RedeSky":{
                if(e instanceof EventUpdate){
                    if(shouldBeNoslowing()){
                        placement();
                        blockStop = false;
                    }else{
                        if(!blockStop){
                            release();
                            blockStop = true;
                        }
                    }
                }

                break;
            }
            case "Reduced":{
                if(e instanceof EventUpdate){
                    if(shouldBeNoslowing()){
                        float factorX = (float) redudeX.getValue() / 100;
                        float factorZ = (float) redudeZ.getValue() / 100;
                        mc.thePlayer.motionX *= factorX;
                        mc.thePlayer.motionZ *= factorZ;

                    }
                }
                break;
            }

            case "NCP":{
                if(e instanceof EventMotion){
                    if(mc.thePlayer.isBlocking()){
                        PacketUtil.sendSilentPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                }
                if(e instanceof EventPostMotionUpdate){
                    if(mc.thePlayer.isBlocking()){
                        PacketUtil.sendSilentPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                }
                break;
            }
        }
    }
    boolean chokePackets;
    int blockTick = 0;
    int blockTicks, blinkTicks;
    boolean startBlink;
    ConcurrentLinkedQueue<Packet> blinked = new ConcurrentLinkedQueue<>();
    private void hypixel(Event ev) {
        // thanks David "error" Azura :pray:
        if(ev instanceof EventMotion){
            EventMotion e = (EventMotion) ev;
            if(isUsingSword()){
                if(e.isPre()){
                    PacketUtil.sendSilentPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    PacketUtil.sendSilentPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }else{
                    //PacketUtil.sendSilentPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, mc.thePlayer.getPosition().add(0, -1, 0), EnumFacing.DOWN));
                }
            }
        }
        if(ev instanceof EventSendPacket){
            EventSendPacket e = (EventSendPacket) ev;
            if(e.getPacket() instanceof C09PacketHeldItemChange){
                blockTick = mc.thePlayer.ticksExisted;
            }
            if(startBlink){
                e.setCancelled(true);
                blinked.add(e.getPacket());
            }else{
                PacketUtil.sendAll(blinked);
            }
        }
    }

    public boolean isUsingSword() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
    public boolean isUsingBow() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
    }
    public boolean isConsumingFood() {
        return mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);
    }
    public boolean shouldBeNoslowing(){
        return ( isUsingSword() || isConsumingFood() || isUsingBow() );
    }

    public void release(){
        PacketUtil.sendSilentPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }
    public void placement(){
        PacketUtil.sendSilentPacket(new C08PacketPlayerBlockPlacement((mc.thePlayer.getHeldItem())));
    }




}
