package uwu.flauxy.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.NumberUtil;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.WorldUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.timer.Timer;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "Killaura", displayName = "Killaura", key = Keyboard.KEY_R, cat = Category.Combat)
public class Killaura extends Module {

    NumberSetting cps = new NumberSetting("CPS", 12, 1, 20, 0.001);
    NumberSetting reach = new NumberSetting("Reach", 4.2, 2.5, 6, 0.1);


    ModeSetting rotations = new ModeSetting("Rotations", "Instant", "Instant", "Verus", "None");
    //ModeSetting autoblock = new ModeSetting("Autoblock", "Hold", "Hold", "Item Use");
    ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post", "Mix");

    BooleanSetting nosprint = new BooleanSetting("No Sprint", false);
    NumberSetting noSprintDelay = new NumberSetting("Delay", 1, 1, 10, 1).setCanShow(m -> nosprint.getValue());

    BooleanSetting autoblock = new BooleanSetting("Autoblock", true);
    ModeSetting autoblockMode = new ModeSetting("Mode", "Hold", "Hold", "Item Use", "Fake").setCanShow(m -> autoblock.getValue());
    //ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post");
    BooleanSetting showTargets = new BooleanSetting("Show Targets", true);

    BooleanSetting players = new BooleanSetting("Players", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting mobs = new BooleanSetting("Mobs", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting animals = new BooleanSetting("Animals", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting shop = new BooleanSetting("Shopkeepers", false).setCanShow(m -> showTargets.getValue());
    BooleanSetting targethud = new BooleanSetting("TargetHUD", true);
    ModeSetting targetHudMode = new ModeSetting("TargetHUD Mode", "Basic", "Basic").setCanShow(m -> targethud.getValue());
    Timer timer = new Timer();


    public Killaura(){
        addSettings(cps, reach, rotations, autoblock, autoblockMode, nosprint, noSprintDelay, showTargets, players, mobs, animals, shop, type, targethud, targetHudMode);
    }

    public static boolean fakeBlock = false;

    public void onUpdate() {
        if(WorldUtil.shouldNotRun()) return;
        if(nosprint.getValue()) {
            if(mc.thePlayer.ticksExisted % noSprintDelay.getValue() == 0){
                mc.thePlayer.setSprinting(false);
            }else{
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    public Entity currentTarget;

    public void onEvent(Event ev){
        if(ev instanceof EventUpdate){
            // World
            World currWorld = mc.theWorld;
            if(mc.thePlayer.ticksExisted % 10 == 0){
                if(currWorld != mc.theWorld){
                    Wrapper.instance.log("Disabled aura");
                    this.toggle();
                }
            }
        }
        if(ev instanceof EventRender2D){
            EventRender2D event = (EventRender2D) ev;
            if(currentTarget != null){
                if(autoblock.getValue()){
                    ScaledResolution sr = new ScaledResolution(mc);
                    switch (targetHudMode.getMode()){
                        case "Basic":{
                            renderTargetHudBasic(ev, sr.getScaledWidth() / 2 + 35, sr.getScaledHeight() / 2 - 45);
                            break;
                        }
                    }
                }
            }
        }
        if(ev instanceof  EventMotion){
            EventMotion event =(EventMotion)ev;
            if(shouldRun()){
                List<Entity> targets = (List<Entity>) this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
                targets = targets.stream().filter(entity -> ((EntityLivingBase) entity).getDistanceToEntity((EntityLivingBase) this.mc.thePlayer) < 10 && entity != this.mc.thePlayer && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0).collect((Collectors.toList()));
                targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity((Entity) this.mc.thePlayer)));
                targets = targets.stream().filter(EntityLivingBase.class::isInstance).collect((Collectors.toList()));

                if(!targets.isEmpty()){
                    Entity target = targets.get(0);
                    currentTarget = target;
                    if(isValid(target, (float) reach.getValue())) {
                        fakeBlock = autoblockMode.is("Fake") && autoblock.getValue();
                        if(autoblock.getValue()){
                            switch(autoblockMode.getMode()){
                                case "Hold":{
                                    if(isHoldingSword()) mc.gameSettings.keyBindUseItem.pressed = true;
                                    break;
                                }
                            }
                        }

                        switch(rotations.getMode()){
                            case "Verus":{
                                double random = NumberUtil.generateRandomFloat(67565, 107450, 10000);
                                //Wrapper.instance.log(String.valueOf(random));
                                if(type.is("Pre") && event.isPre()){
                                    yaw((float) (getRotations(target)[0] + random + 1 + Math.random()), event);
                                    pitch((float) (getRotations(target)[1] + random + 1 + Math.random()), event);
                                }
                                break;
                            }
                            case "Instant":{
                                yaw(getRotations(target)[0], event);
                                pitch(getRotations(target)[1], event);
                                break;
                            }
                        }
                        if(timer.hasTimeElapsed(1000 / cps.getValue() + Math.random(), true)){
                            Criticals.isCrits = true;
                            if(type.is("Post")) attack(target);
                            if(type.is("Pre") && event.isPre()) attack(target);
                            if(type.is("Mix") && event.isPre() || event.isPost()) attack(target);
                        }
                    }else{
                        targets.remove(target);
                        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                        Killaura.fakeBlock = false;
                    }
                }
                if(targets.isEmpty() || !shouldRun() || mc.thePlayer.getDistanceToEntity(currentTarget) > reach.getValue()){
                    mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                    Killaura.fakeBlock = false;
                }
            }
        }

    }

    @Override
    public void onDisable() {
        Killaura.fakeBlock = false;
        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
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

    public void attack(Entity target){
        mc.thePlayer.swingItem();
        PacketUtil.packetNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
    }

    public boolean isHoldingSword(){
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
    float thing = 0f;

    public void renderTargetHudBasic(Event e, int x, int y){
        if(e instanceof EventRender2D){
            Entity target = currentTarget;
            if(currentTarget != null){
                float maxDist = 6;
                if(((EntityLivingBase)currentTarget).getHealth() <= 0 || !isValid(currentTarget, maxDist + 5)) return;
                float offset = mc.thePlayer.getDistanceToEntity(target) - maxDist;
                if(mc.thePlayer.getDistanceToEntity(target) <= maxDist + 5){
                    if(currentTarget instanceof EntityPlayer){
                        drawHead( (AbstractClientPlayer)target, (x + 4) / thing, (y + 4) / thing, 44 / thing, 44 / thing);
                    }else{
                        GuiInventory.drawEntityOnScreen(x + 20, y + 50, 25, target.rotationYaw, 0, (EntityLivingBase) target);
                    }
                    Gui.drawRect(x / thing, y / thing, (x + 192) / thing, (y +52) / thing, new Color(0, 0, 0, 90).getRGB());
                    TTFFontRenderer tFont = Flauxy.INSTANCE.fontManager.getFont("auxy " + (21 - offset));
                    tFont.drawStringWithShadow(currentTarget.getDisplayName().getFormattedText(), (x + 52) / thing, (y + 4) / thing, new Color(250, 250, 250, 255).getRGB());
                    Gui.drawRect((x + 52) / thing, (y + 32) / thing, ((x + 52) + (( ((EntityLivingBase)target).getHealth() / ((EntityLivingBase)target).getMaxHealth() ) * 132)) / thing, (y + 48) / thing, new Color(164, 36, 36, 225).getRGB());
                }
                if(offset >= 1){
                    if(thing <= offset){
                        thing+=0.0042f;
                    }else{
                        thing -= 0.0042f;
                    }
                }else{
                    thing = 1;
                }
            }
        }
    }

    public static void drawHead(final AbstractClientPlayer target, final float x, final float y, final float width, final float height) {
        final ResourceLocation skin = target.getLocationSkin();
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1, 1, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }

}
