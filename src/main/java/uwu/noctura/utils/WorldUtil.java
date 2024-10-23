package uwu.noctura.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
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

import java.text.DecimalFormat;
import java.util.*;

import java.util.function.BiPredicate;

import com.google.common.base.Predicate;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
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

    public static EnumChatFormatting quadColHealth(EntityLivingBase e){
        return e.getHealth() < 5 ? EnumChatFormatting.RED : e.getHealth() < 10 ? EnumChatFormatting.GOLD : e.getHealth() < 15 ? EnumChatFormatting.YELLOW : EnumChatFormatting.GREEN;
    }

    public static float wrappedHealth(EntityLivingBase entity){
        return Math.round(entity.getHealth() * 100.0f) / 100.0f;
    }


    // from lb lol
    public static List<BlockPos> searchBlocksInCuboid(float range, Vec3 eyes, BiPredicate<BlockPos, IBlockState> filter) {
        List<BlockPos> blocks = new ArrayList<>();

        int xRangeStart = (int) Math.floor(range + eyes.xCoord);
        int xRangeEnd = (int) Math.floor(-range + eyes.xCoord);
        int yRangeStart = (int) Math.floor(range + eyes.yCoord);
        int yRangeEnd = (int) Math.floor(-range + eyes.yCoord);
        int zRangeStart = (int) Math.floor(range + eyes.zCoord);
        int zRangeEnd = (int) Math.floor(-range + eyes.zCoord);

        for (int x = xRangeStart; x >= xRangeEnd; x--) {
            for (int y = yRangeStart; y >= yRangeEnd; y--) {
                for (int z = zRangeStart; z >= zRangeEnd; z--) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    IBlockState state = Minecraft.getMinecraft().theWorld.getBlockState(blockPos);
                    if (state == null) continue;
                    if (!filter.test(blockPos, state)) {
                        continue;
                    }

                    blocks.add(blockPos);
                }
            }
        }

        return blocks;
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
