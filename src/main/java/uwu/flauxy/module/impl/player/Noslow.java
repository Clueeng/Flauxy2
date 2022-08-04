package uwu.flauxy.module.impl.player;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventPostMotionUpdate;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.PacketUtil;

@ModuleInfo(name = "Noslow", displayName = "No Slow", key = -1, cat = Category.Player)
public class Noslow extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP", "Reduced");
    NumberSetting redudeX = new NumberSetting("Reduce X", 0, 0, 100, 1).setCanShow(m -> mode.is("Reduced"));
    NumberSetting redudeZ = new NumberSetting("Reduce Z", 0, 0, 100, 1).setCanShow(m -> mode.is("Reduced"));

    public Noslow(){
        addSettings(mode, redudeX, redudeZ);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            this.setDisplayName("Noslow " + EnumChatFormatting.WHITE + "Mode: " + mode.getMode());
        }
        switch(mode.getMode()){
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

    public boolean isUsingSword() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
    public boolean isConsumingFood() {
        return mc.thePlayer.isUsingItem() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);
    }
    public boolean shouldBeNoslowing(){
        return ( isUsingSword() || isConsumingFood() );
    }


}
