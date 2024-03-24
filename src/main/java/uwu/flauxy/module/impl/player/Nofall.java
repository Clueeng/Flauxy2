package uwu.flauxy.module.impl.player;

import net.minecraft.block.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import org.lwjgl.system.CallbackI;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.packet.EventPacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.BlockUtil;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

import java.util.Objects;

@ModuleInfo(name = "Nofall", displayName = "No Fall", key = -1, cat = Category.Player)
public class Nofall extends Module {

    public static ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Test", "Spoof", "Legit");
    boolean can;
    private Timer clutchTimer = new Timer();

    public Nofall() {
        this.addSettings(mode);
    }

    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            this.setDisplayName("Nofall " + EnumChatFormatting.WHITE + mode.getMode());
            EventMotion event = (EventMotion) e;
            switch(mode.getMode()){
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
                case "Test":{
                    if(mc.thePlayer.fallDistance > 4.5f){

                    }
                    break;
                }

                case "Legit":{
                    boolean hasClutched = false;
                    boolean hasChangedSlot = false;
                    float oldFallDistance = 0;
                    boolean shouldStart = mc.thePlayer.fallDistance > 3f;
                    if(shouldStart){
                        // Calculate how many blocks between now and next block under palye r???
                        BlockPos ground = null;
                        for(int y = (int) Math.floor(mc.thePlayer.posY); y > mc.thePlayer.posY - 10; y--){
                            BlockPos nowBlock = mc.thePlayer.playerLocation.add(0, y, 0);
                            if(!mc.theWorld.isAirBlock(nowBlock)){
                                ground = nowBlock;
                                break;
                            }else{
                                oldFallDistance = mc.thePlayer.fallDistance;
                            }
                        }
                        if(ground != null){
                            double clutchY = ground.add(0, 1, 0).getY();
                            // Inventory logic
                            boolean itemToClutch = false;
                            InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
                            for(int i = 0; i < 9; i++){
                                if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                                    continue;
                                Item currentItem = mc.thePlayer.inventory.getStackInSlot(i).getItem();
                                if(currentItem == Item.getItemFromBlock(Blocks.web) || currentItem == Items.water_bucket){
                                    itemToClutch = true;
                                }
                            }

                        }

                    }
                    Wrapper.instance.log(String.valueOf(oldFallDistance));
                    break;
                }
            }
        }
    }




}
