package uwu.flauxy.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.NumberUtil;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "Killaura", displayName = "Killaura", key = Keyboard.KEY_R, cat = Category.Combat)
public class Killaura extends Module {

    NumberSetting cps = new NumberSetting("CPS", 12, 1, 20, 0.5);
    NumberSetting reach = new NumberSetting("Reach", 4.2, 2.5, 6, 0.1);


    ModeSetting rotations = new ModeSetting("Rotations", "Instant", "Instant", "Verus", "None");
    ModeSetting autoblock = new ModeSetting("Autoblock", "Hold", "Hold", "Item Use");
    ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post");

    BooleanSetting players = new BooleanSetting("Players", true);
    BooleanSetting mobs = new BooleanSetting("Mobs", true);
    BooleanSetting animals = new BooleanSetting("Animals", true);
    BooleanSetting shop = new BooleanSetting("Shopkeepers", false);
    Timer timer = new Timer();

    public Killaura(){
        addSettings(cps, reach, rotations, players, mobs, animals, shop);
    }

    public void onEvent(Event ev){
        if(ev instanceof  EventMotion){
            EventMotion event =(EventMotion)ev;
            if(shouldRun()){
                List<Entity> targets = (List<Entity>) this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
                targets = targets.stream().filter(entity -> ((EntityLivingBase) entity).getDistanceToEntity((EntityLivingBase) this.mc.thePlayer) < reach.getValue() && entity != this.mc.thePlayer && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0).collect((Collectors.toList()));
                targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity((Entity) this.mc.thePlayer)));
                targets = targets.stream().filter(EntityLivingBase.class::isInstance).collect((Collectors.toList()));

                if(!targets.isEmpty()){
                    Entity target = targets.get(0);
                    if(isValid(target)){
                        switch(autoblock.getMode()){
                            case "Hold":{
                                if(isHoldingSword()) mc.gameSettings.keyBindUseItem.pressed = true;
                                break;
                            }
                        }
                        switch(rotations.getMode()){
                            case "Verus":{
                                float yawGcd, pitchGcd;
                                yawGcd = ((getRotations(target)[0]) + NumberUtil.generateRandom(-12, 15)) * NumberUtil.generateRandomFloat(100, 115, 100);
                                pitchGcd = ((getRotations(target)[1]) + NumberUtil.generateRandom(-15, 3));
                                yaw(yawGcd, event);
                                pitch(pitchGcd, event);
                                break;
                            }
                            case "Instant":{
                                yaw(getRotations(target)[0], event);
                                pitch(getRotations(target)[1], event);
                                break;
                            }
                        }
                        if(timer.hasTimeElapsed(1000 / cps.getValue(), true)){
                            if(type.is("Post") && event.isPost()) attack(target);
                            if(type.is("Pre") && event.isPre()) attack(target);

                        }
                    }
                }
                if(targets.isEmpty() || !shouldRun()){
                    mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                }
            }
        }

    }

    public void yaw(float yaw, EventMotion e){
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
        e.setYaw(yaw);
    }
    public void pitch(float pitch, EventMotion e){
        mc.thePlayer.rotationPitchHead = pitch;
        e.setPitch(pitch);
    }

    public boolean shouldRun(){
        return mc.thePlayer.ticksExisted > 10 && mc.thePlayer != null && mc.theWorld != null;
    }

    public boolean isValid(Entity e){
        boolean finalValid = false;
        if(e instanceof EntityPlayer && players.getValue()){
            if((e.getName().equals("UPGRADES") || e.getName().equals("SHOP"))){
                finalValid = !shop.getValue();
            }else{
                finalValid = true;
            }
        }
        if(e instanceof EntityMob && mobs.getValue()){
            finalValid = true;
        }
        if(e instanceof EntityAnimal && animals.getValue()){
            finalValid = true;
        }
        return finalValid;
    }
    public float[] getRotations(Entity e) {
        double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX,
                deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        return new float[] {yaw, pitch};
    }

    public void attack(Entity target){
        mc.thePlayer.swingItem();
        PacketUtil.packetNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
    }

    public boolean isHoldingSword(){
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

}
