package uwu.flauxy.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

import java.util.UUID;

public class WorldUtil {

    public static boolean shouldNotRun(){
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.ticksExisted <= 5;
    }

    public static void attackFakePlayer() {
        Minecraft mc = Minecraft.getMinecraft();

        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(UUID.randomUUID(), "TROLLED"));

        PacketUtil.packetNoEvent(new C0APacketAnimation());
        PacketUtil.packetNoEvent(new C02PacketUseEntity(fakePlayer, C02PacketUseEntity.Action.ATTACK));
    }

}
