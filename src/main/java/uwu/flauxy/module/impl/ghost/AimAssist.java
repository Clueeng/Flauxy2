package uwu.flauxy.module.impl.ghost;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventFrame;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MathHelper;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@ModuleInfo(name = "AimAssist", key = -1, cat = Category.Ghost, displayName = "Aim Assist")
public class AimAssist extends Module {

    //NumberSetting strength = new NumberSetting("Strength",1.0,0.1,5,0.1);
    GraphSetting strength = new GraphSetting("Strength",1,1,0,5,0,5,0.5f,0.5f);
    BooleanSetting onlySword = new BooleanSetting("Sword Only",true);
    NumberSetting FOV = new NumberSetting("FOV",10,10,100,1);
    NumberSetting maxDistance = new NumberSetting("Distance",4.0,3.0,8.0,0.1);

    BooleanSetting drawFov = new BooleanSetting("DrawFov", true);
    BooleanSetting onClick = new BooleanSetting("On Click", true);
    BooleanSetting showTargets = new BooleanSetting("Show Targets", true);
    BooleanSetting players = new BooleanSetting("Players", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting mobs = new BooleanSetting("Mobs", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting animals = new BooleanSetting("Animals", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting shop = new BooleanSetting("NCP's", false).setCanShow(m -> showTargets.getValue());

    public AimAssist(){
        addSettings(strength, drawFov, FOV, maxDistance, onClick, onlySword, showTargets, players, mobs, animals, shop);
    }

    List<Entity> targets;


    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender2D){
            if(drawFov.isEnabled()){
                int middleX = new ScaledResolution(mc).getScaledWidth() / 2;
                int middleY = new ScaledResolution(mc).getScaledHeight() / 2;
                RenderUtil.drawUnfilledCircle(middleX + 1, middleY+1, (float) FOV.getValue() * 4f, new Color(255,255,255,30));
            }
        }
        if (e instanceof EventFrame) {
            Entity target = getClosest();
            if (target == null) {
                return;
            }
            if(onClick.getValue() && !Mouse.isButtonDown(0))return;
            if(onlySword.getValue() && mc.thePlayer.inventory.getCurrentItem() != null){
                if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword )){
                    return;
                }
            }

            float[] rotations = getRotations(target);
            float targetYaw = rotations[0] % 360;
            float targetPitch = rotations[1];
            float playerYaw = net.minecraft.util.MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
            float playerPitch = mc.thePlayer.rotationPitch;
            float yawDifference = Math.abs(playerYaw - targetYaw);
            float pitchDifference = targetPitch - playerPitch;
            if (Math.abs(yawDifference) > FOV.getValue() || Math.abs(pitchDifference) > FOV.getValue()) {
                return;
            }
            if (mc.thePlayer.getDistanceToEntity(target) > maxDistance.getValue()) {
                return;
            }

            //mc.thePlayer.rotationYaw = mc.thePlayer.rotationYaw % 360;
            //mc.thePlayer.rotationYaw = (float) MathHelper.lerp(0.1f,mc.thePlayer.rotationYaw,targetYaw % 360);
            if(mc.thePlayer.rotationYaw % 360 > targetYaw % 360){
                mc.thePlayer.rotationYaw -= (float) strength.getX();
            }
            if(mc.thePlayer.rotationYaw % 360 < targetYaw % 360){
                mc.thePlayer.rotationYaw += (float) strength.getX();
            }
            //mc.thePlayer.rotationYaw = (float) MathHelper.lerp(strength.getX() / 200f, mc.thePlayer.rotationYaw % 360, targetYaw % 360);
            //mc.thePlayer.rotationPitch += (float) MathHelper.lerp(lerpFactor, mc.thePlayer.rotationYaw, pitchDifference);
        }
    }

    public Entity getClosest(){
        if(shouldRun()) {
            targets = this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
            targets = targets.stream().filter(entity -> entity.getDistanceToEntity(this.mc.thePlayer) < 10 && entity != this.mc.thePlayer && (!(entity instanceof EntityArmorStand)) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0).collect((Collectors.toList()));
            targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(this.mc.thePlayer)));
            targets = targets.stream().filter(EntityLivingBase.class::isInstance).collect((Collectors.toList()));
            if(targets.isEmpty()) return null;
            return targets.get(0);
        }
        return null;
    }

    public boolean shouldRun(){
        return mc.thePlayer.ticksExisted > 10 && mc.thePlayer != null && mc.theWorld != null;
    }

    public boolean isValid(Entity e, float reach){
        boolean finalValid = false;
        if(mc.thePlayer.getDistanceToEntity(e) <= reach){
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
            if(e.ticksExisted < 15){
                finalValid = false;
            }
        }else{
            finalValid = false;
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
}
