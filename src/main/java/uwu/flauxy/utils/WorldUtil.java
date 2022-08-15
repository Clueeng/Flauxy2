package uwu.flauxy.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.common.base.Predicate;
import uwu.flauxy.Flauxy;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.visuals.Chams;

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

    public static boolean isValidChams(EntityLivingBase e){
        Chams chams = Flauxy.INSTANCE.getModuleManager().getModule(Chams.class);
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
