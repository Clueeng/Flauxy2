package uwu.noctura.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

import java.util.ArrayList;
import java.util.UUID;
import com.google.common.base.Predicate;
import uwu.noctura.Noctura;
import uwu.noctura.module.impl.visuals.Chams;

public class WorldUtil {
    public static List<EntityLivingBase> getLivingEntities(Predicate<EntityLivingBase> validator) {
        Minecraft mc = Minecraft.getMinecraft();
        List<EntityLivingBase> entities = new ArrayList<>();
        if(mc.theWorld == null) return entities;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase e = (EntityLivingBase) entity;
                if (validator.apply(e))
                    entities.add(e);
            }
        }
        return entities;
    }
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

    public static Entity spawnBoatAndInteract(){
        Minecraft mc = Minecraft.getMinecraft();
        EntityBoat boat = new EntityBoat(mc.theWorld, mc.thePlayer.posX - 2, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ);
        mc.theWorld.loadedEntityList.add(boat);
        mc.theWorld.spawnEntityInWorld(boat);
        return boat;
    }

    public static boolean isValidChams(EntityLivingBase e){
        Chams chams = Noctura.INSTANCE.getModuleManager().getModule(Chams.class);
        if(chams.animals.getValue() && e instanceof EntityAnimal){
            return true;
        }
        if(chams.mobs.getValue() && e instanceof EntityMob){
            return true;
        }
        if(chams.players.getValue() && e instanceof EntityPlayer){
            return true;
        }
        return false;
    }

    public static boolean isTeammate(EntityPlayer entityPlayer) {
        Minecraft mc = Minecraft.getMinecraft();
        if (entityPlayer != null) {
            String text = entityPlayer.getDisplayName().getFormattedText();
            String playerText = mc.thePlayer.getDisplayName().getFormattedText();
            if (text.length() < 2 || playerText.length() < 2) return false;
            if (!text.startsWith("\u00A7") || !playerText.startsWith("\u00A7")) return false;
            return text.charAt(1) == playerText.charAt(1);
        }
        return false;
    }

}
