package uwu.flauxy.module.impl.player;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
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
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Nofall", displayName = "No Fall", key = -1, cat = Category.Player)
public class Nofall extends Module {

    public static ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Test", "Spoof");;
    boolean can;

    public Nofall() {
        this.addSettings(mode);
    }

    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            this.setDisplayName("Nofall " + EnumChatFormatting.WHITE + mode.getMode());
            EventMotion event = (EventMotion) e;
            switch(mode.getMode()){
                case "Packet":{
                    if(mc.thePlayer.fallDistance >= 3){
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




}
