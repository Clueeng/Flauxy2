package uwu.flauxy.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
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
import uwu.flauxy.utils.render.RenderUtil;
import uwu.flauxy.utils.timer.Timer;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static uwu.flauxy.utils.render.ColorUtils.getGradientOffset;
import static uwu.flauxy.utils.render.ColorUtils.getHealthColor;
import static uwu.flauxy.utils.render.RenderUtil.drawFace;

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
    BooleanSetting shop = new BooleanSetting("NCP's", false).setCanShow(m -> showTargets.getValue());
    BooleanSetting wall = new BooleanSetting("Through Walls", true);
    BooleanSetting targethud = new BooleanSetting("TargetHUD", true);
    ModeSetting targetHudMode = new ModeSetting("TargetHUD Mode", "Flaily", "Flaily", "Astolfo", "Rainbow").setCanShow(m -> targethud.getValue());
    Timer timer = new Timer();


    public Killaura(){
        addSettings(autoblockMode, type, rotations, cps, reach, autoblock, nosprint, noSprintDelay, wall, showTargets, players, mobs, animals, shop, targethud, targetHudMode);
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
    List<Entity> targets;


    public void onEvent(Event ev){
        if(ev instanceof EventUpdate){
            // World

            if(!mobs.getValue()){
                if(currentTarget instanceof EntityMob){
                    targets.remove(currentTarget);
                }
            }

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
                        case "Flaily":{
                            renderTargetHudBasic(ev, sr.getScaledWidth() / 2 + 35, sr.getScaledHeight() / 2 - 45);
                            break;
                        }
                        case "Rainbow": {
                            TTFFontRenderer tFont = Flauxy.INSTANCE.fontManager.getFont("auxy " + (21));
                            int FirstLetterColor =  getGradientOffset(new Color(255, 60, 234), new Color(27, 179, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (3 / (tFont.getHeight("A") + 6 ) / 2)).getRGB();
                            EntityLivingBase ent = (EntityLivingBase) currentTarget;
                            if (ent != null && ent.getHealth() != 0) {
                                float scaledWidth = (float) sr.getScaledWidth();
                                float scaledHeight = (float) sr.getScaledHeight();
                                if (/*ent instanceof EntityPlayer && */ent != null) {
                                    double hpPercentage = (ent.getHealth() / ent.getMaxHealth());
                                    //EntityPlayer player = (EntityPlayer) ent;
                                    EntityLivingBase player = (EntityLivingBase) ent;
                                    if (hpPercentage > 1.0D) {
                                        hpPercentage = 1.0D;
                                    } else if (hpPercentage < 0.0D) {
                                        hpPercentage = 0.0D;
                                    }
                                    RenderUtil.drawUnfilledRectangle( (scaledWidth / 2.0F - 200.0F) - 0.5, (scaledHeight / 2.0F - 42.0F) - 0.8, (scaledWidth / 2.0F - 200.0F + 40.0F + ((this.mc.fontRendererObj.getStringWidth(player.getName()) > 105) ? (this.mc.fontRendererObj.getStringWidth(player.getName()) - 10 + 0.5) : 105 + 0.5)), (scaledHeight / 2.0F - 2.0F) + 0.5, 2,  new Color(FirstLetterColor).getRGB());
                                    RenderUtil.drawRect2((scaledWidth / 2.0F - 200.0F), (scaledHeight / 2.0F - 42.0F), (scaledWidth / 2.0F - 200.0F + 40.0F + ((this.mc.fontRendererObj.getStringWidth(player.getName()) > 105) ? (this.mc.fontRendererObj.getStringWidth(player.getName()) - 10) : 105)), (scaledHeight / 2.0F - 2.0F),(new Color(0, 0, 0, 150)).getRGB());
                                    if(ent instanceof EntityPlayer){
                                        drawFace((int) scaledWidth / 2 - 196, (int) (scaledHeight / 2.0F - 38.0F), 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F, (AbstractClientPlayer) player);
                                    }
                                    tFont.drawStringWithShadow(player.getName(), (scaledWidth / 2.0F - 196.0F + 40.0F), (float) ((scaledHeight / 2.0F - 36.0F) + 1 - 0.5), -1);
                                    RenderUtil.drawRoundedRectangle((scaledWidth / 2.0F - 196.0F + 40.0F), (scaledHeight / 2.0F - 26.0F + 3), (float) ((scaledWidth / 2.0F - 196.0F + 40.0F) + hpPercentage * 1.25D * 70.0D), (scaledHeight / 2.0F - 14.0F), 2, getHealthColor(ent)); // hel bar
                                    //      FontManager.small.drawString(healthStr + "%", (float) ((scaledWidth / 2.0F - 196.0F + 40.0F) + hpPercentage * 1.25D * 70.0D), (float) ((scaledHeight / 2.0F - 36.0F) + 12.5), getHealthColor(ent));

                                }

                            }
                            break;
                        }

                        case "Astolfo": {
                            EntityLivingBase target = (EntityLivingBase) this.currentTarget;
                            if (target != null && target.getHealth() != 0) {


                                float scaledWidth = (float) sr.getScaledWidth();
                                float scaledHeight = (float) sr.getScaledHeight();
                                float x = scaledWidth / 2.0F - 70.0F;
                                float y = scaledHeight / 2.0F + 80.0F;
                                int color, xHealthbar, yHealthbar;
                                float add;
                                double addX;
                                int index;
                                color = (new Color(16734296)).getRGB();
                                drawRect(x - 1.0F, y + 2.0F, 155.0F, 57.0F, new Color(-1459157241, true));
                                this.mc.fontRendererObj.drawStringWithShadow(target.getName(), (x + 31.0F), (y + 6.0F), -1);
                                GL11.glPushMatrix();
                                GlStateManager.translate(x, y, 1.0F);
                                GL11.glScalef(2.0F, 2.0F, 2.0F);
                                GlStateManager.translate(-x, -y, 1.0F);
                                this.mc.fontRendererObj.drawStringWithShadow((Math.round((target.getHealth() / 2.0F) * 10.0D) / 10.0D) + "❤", (x + 16.0F), (y + 13.0F), (new Color(color)).darker().getRGB());
                                GL11.glPopMatrix();
                                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                                GuiInventory.drawEntityOnScreen((int) x + 16, (int) y + 55, 25, target.rotationYaw, -target.rotationPitch, target);
                                xHealthbar = 30;
                                yHealthbar = 46;
                                add = 120.0F;
                                drawRect(x + xHealthbar, y + yHealthbar, add, 8.0F, (new Color(color)).darker().darker().darker());
                                drawRect(x + xHealthbar, y + yHealthbar, target.getHealth() / target.getMaxHealth() * add, 8.0F, new Color(color));
                                addX = (x + xHealthbar + target.getHealth() / target.getMaxHealth() * add);
                                drawRect((float) (addX - 3.0D), y + yHealthbar, 3.0F, 8.0F, new Color(-1979711488, true));
                                for (index = 1; index < 5; index++) {
                                    if (target.getEquipmentInSlot(index) == null) ;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(ev instanceof  EventMotion){
            EventMotion event =(EventMotion)ev;
            if(shouldRun()){
                targets = (List<Entity>) this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());

                targets = targets.stream().filter(entity -> ((EntityLivingBase) entity).getDistanceToEntity((EntityLivingBase) this.mc.thePlayer) < 10 && entity != this.mc.thePlayer && (!(entity instanceof EntityArmorStand)) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0).collect((Collectors.toList()));
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

                            if (target instanceof EntityPlayer) { // idk i need to check if player bc it will crash and im to lazy to fix or find the error idc rn
                                if (!mc.thePlayer.isInvisibleToPlayer((EntityPlayer) target) && !wall.isEnabled())
                                    return;
                            }else{
                                targets.remove(target);
                            }

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
            GlStateManager.color(1f, 1f, 1f);
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


    public void drawRect(float x, float y, float w, float h, Color color) {
        Gui.drawRect(x, y, (x + w), (y + h), color.getRGB());
    }


}
