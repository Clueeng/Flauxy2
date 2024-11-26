package uwu.noctura.module.impl.combat;

import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_9;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.*;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.ghost.LegitSprint;
import uwu.noctura.module.impl.player.Scaffold;
import uwu.noctura.module.impl.player.Sprint;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.ui.star.StarParticle;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.WorldUtil;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.ColorUtils;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;
import uwu.noctura.utils.timer.Timer;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static uwu.noctura.utils.font.FontManager.getFont;
import static uwu.noctura.utils.render.ColorUtils.getHealthColor;

@ModuleInfo(name = "Killaura", displayName = "Killaura", key = Keyboard.KEY_R, cat = Category.Combat)
public class Killaura extends Module {

    private final ModeSetting cpsMode = new ModeSetting("CPS Mode", "Normal","Normal", "ButterFly");
    private final NumberSetting cps = new NumberSetting("CPS", 12, 1, 20, 0.001);
    public NumberSetting reach = new NumberSetting("Reach", 4.2, 2.5, 6, 0.1);
    private final BooleanSetting lockView = new BooleanSetting("Lock View", false);


    private final ModeSetting rotations = new ModeSetting("Rotations", "Instant", "Instant", "Verus", "None", "Legit");
    //ModeSetting autoblock = new ModeSetting("Autoblock", "Hold", "Hold", "Item Use");
    private final  ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post", "Mix");

    private final BooleanSetting nosprint = new BooleanSetting("No Sprint", false);
    private final NumberSetting noSprintDelay = new NumberSetting("Delay", 1, 1, 10, 1).setCanShow(m -> nosprint.getValue());

    private final BooleanSetting autoblock = new BooleanSetting("Autoblock", true);
    private final ModeSetting autoblockMode = new ModeSetting("Mode", "Hold", "Hold", "Fake").setCanShow(m -> autoblock.getValue());
    //ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post");
    private final BooleanSetting showTargets = new BooleanSetting("Show Targets", true);
    private final BooleanSetting raycast = new BooleanSetting("Raycast", true).setCanShow(m -> !rotations.is("None"));

    private final BooleanSetting players = new BooleanSetting("Players", true).setCanShow(m -> showTargets.getValue());
    private final BooleanSetting mobs = new BooleanSetting("Mobs", true).setCanShow(m -> showTargets.getValue());
    private final BooleanSetting animals = new BooleanSetting("Animals", true).setCanShow(m -> showTargets.getValue());
    private final BooleanSetting shop = new BooleanSetting("NCP's", false).setCanShow(m -> showTargets.getValue());
    private final BooleanSetting wall = new BooleanSetting("Through Walls", true);
    public BooleanSetting targethud = new BooleanSetting("TargetHUD", true);
    BooleanSetting movefix = new BooleanSetting("Move Fix", true);
    public ModeSetting targetHudMode = new ModeSetting("TargetHUD Mode", "Noctura", "Noctura", "Star", "Classic").setCanShow(m -> targethud.getValue());

    Timer timer = new Timer();
    public long lastAttack;

    int amountOfClicks = 0;
    int amountOfClicks2 = 0;
    float moveYaw, movePitch;

    public Killaura(){
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(100);
        setMoveH(72);
        setMoveW(145);
        addSettings(autoblockMode, type, rotations, lockView, raycast, cpsMode, cps, reach, autoblock, nosprint, noSprintDelay, movefix, wall, showTargets, players, mobs, animals, shop, targethud, targetHudMode);
    }

    public static boolean fakeBlock = false;

    public void onUpdate() {
        if(WorldUtil.shouldNotRun()) return;
        if(nosprint.getValue()) {
            mc.thePlayer.setSprinting(false);
        }
    }

    float tempX = 0;
    public Entity currentTarget;
    List<Entity> targets;
    int clicksTotal = 0;

    public void onEvent(Event ev){
        if(ev instanceof EventStrafe){
            EventStrafe e = (EventStrafe) ev;
            if(mc.thePlayer == null || mc.theWorld == null) {
                this.toggle();
                return;
            }
            if(mc.thePlayer.ticksExisted < 10) this.toggle();

            if(currentTarget != null && mc.thePlayer.ticksExisted > 10){
                if(isValid(currentTarget, (float) reach.getValue()) && movefix.isEnabled()) {
                    e.setYaw(getRotations(currentTarget)[0]);
                }
            }
        }
        if(ev instanceof EventUpdate){
            if(mc.thePlayer.ticksExisted < 10) this.toggle();

            this.setArrayListName("Killaura " + EnumChatFormatting.WHITE + type.getMode());
            // World

            if(!mobs.getValue()){
                if(currentTarget instanceof EntityMob){
                    targets.remove(currentTarget);
                }
            }

            if(currentTarget != null){
                if(mc.thePlayer.getDistanceToEntity(currentTarget) > 15){
                    currentTarget = null;
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
            if(currentTarget != null && (!(mc.currentScreen instanceof GuiChat))){
                if(targethud.isEnabled() && currentTarget instanceof EntityLivingBase){
                    switch (targetHudMode.getMode()){
                        case "Noctura":{
                            renderNocturaHud(getMoveX(), getMoveY(), currentTarget);
                            break;
                        }
                        case "Star":{
                            renderStarTargetHud(getMoveX(), getMoveY(), (EntityLivingBase) currentTarget);
                            break;
                        }
                        case "Classic":{
                            renderClassicTargetHud(getMoveX(), getMoveY(), (EntityLivingBase) currentTarget);
                            break;
                        }
                    }
                }

                GlStateManager.color(1f, 1f, 1f);
            }
        }

        if(ev instanceof  EventMotion){
            EventMotion event = (EventMotion)ev;
            if(shouldRun()){
                targets = this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());

                targets = targets.stream().filter(entity -> entity.getDistanceToEntity(this.mc.thePlayer) < 10 && entity != this.mc.thePlayer && (!(entity instanceof EntityArmorStand)) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0).collect((Collectors.toList()));
                targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(this.mc.thePlayer)));
                targets = targets.stream().filter(EntityLivingBase.class::isInstance).collect((Collectors.toList()));

                if(!targets.isEmpty()){
                    Entity target = targets.get(0);
                    currentTarget = target;
                    if(isValid(target, (float) reach.getValue())) {
                        fakeBlock = autoblockMode.is("Fake") && autoblock.getValue();
                        if(autoblock.getValue()){
                            switch(autoblockMode.getMode()){
                                case "Hold":{
                                    if(isHoldingSword()){
                                        mc.gameSettings.keyBindUseItem.pressed = true;
                                    }
                                    break;
                                }
                            }
                        }

                        switch(rotations.getMode()){
                            case "Verus":{
                                if(type.is("Pre") && event.isPre()){
                                    float smoothnessX = 30;
                                    float smoothnessY = 30;
                                    if(event.getYaw() < getRotations(target)[0]){
                                        float yaw = event.getYaw() + smoothnessX;
                                        moveYaw = yaw;
                                        yaw(yaw, event);
                                    }
                                    if(event.getYaw() > getRotations(target)[0]){
                                        float yaw = event.getYaw() + smoothnessX;
                                        moveYaw = yaw;
                                        yaw(yaw, event);
                                    }
                                    if(event.getPitch() < getRotations(target)[1]){
                                        float pitch = event.getPitch() + smoothnessY;
                                        pitch(pitch,event);
                                    }
                                    if(event.getPitch() > getRotations(target)[1]){
                                        float pitch = event.getPitch() - smoothnessY;
                                        pitch(pitch,event);
                                    }
                                    //yaw((float) (getRotations(target)[0] + random + 1 + Math.random()), event);
                                    //pitch((float) (getRotations(target)[1] + random + 1 + Math.random()), event);
                                }
                                break;
                            }
                            case "Legit":{
                                float[] targetRot = getRotations(target);
                                float targetYaw = targetRot[0];
                                float targetPitch = targetRot[1];

                                float[] gcd = applyGCD(targetYaw, targetPitch, event.getPrevYaw(), event.getPrevPitch());

                                if(Math.abs(event.getYaw() - gcd[0]) > 2.3){
                                    yaw(gcd[0], event);
                                    moveYaw = gcd[0];
                                }
                                if(Math.abs(event.getPitch() - gcd[1]) > 4.3){
                                    pitch(gcd[1], event);
                                }
                                break;
                            }
                            case "Instant":{
                                moveYaw = getRotations(target)[0];
                                yaw(getRotations(target)[0], event);
                                pitch(getRotations(target)[1], event);
                                break;
                            }
                        }
                        double firstRandom = new Random().nextDouble();
                        double secondRandom = new Random().nextDouble();
                        float randomness = (float) Math.max(-3, Math.min(((firstRandom - (firstRandom/2)) * cps.getValue()) / (cps.getValue() * (secondRandom - (secondRandom/2))), 3));
                        float msTime = (float) (1000f / (cps.getValue() + randomness));
                        long clicks = 0;
                        switch (cpsMode.getMode()){
                            case "Normal":{
                                clicks = (long) (1000 / cps.getValue() + Math.random());
                                break;
                            }
                            case "ButterFly":{
                                clicks = (long) (amountOfClicks > 2 ? msTime - (msTime / 2.5f - (new Random().nextDouble() * 4.5f)) : msTime * 1.1f);
                                if(amountOfClicks2 % 10 == 0){
                                    clicks += (long) (new Random().nextDouble() * 350);
                                }

                                break;
                            }
                        }
                        if(timer.hasTimeElapsed(clicks, true)){
                            if(autoblock.getValue()){
                                switch(autoblockMode.getMode()){
                                    case "Hold":{
                                        if(isHoldingSword()){
                                            mc.gameSettings.keyBindUseItem.pressed = false;
                                        }
                                        break;
                                    }
                                }
                            }
                            lastAttack = System.currentTimeMillis();
                            clicksTotal++;
                            if (target instanceof EntityPlayer) {
                                if (!mc.thePlayer.isInvisibleToPlayer((EntityPlayer) target) && !wall.isEnabled())
                                    return;
                            }else{
                                targets.remove(target);
                            }
                            if(event.isPre()){
                                attack(target, event);
                            }
                            if(((EntityLivingBase)currentTarget).hurtTime > 3){
                                attacked = System.currentTimeMillis();
                            }
                            amountOfClicks++;
                            amountOfClicks2++;
                        }
                        if(amountOfClicks > 3){
                            amountOfClicks = 0;
                        }
                    }else{
                        targets.remove(target);
                        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                        Killaura.fakeBlock = false;
                    }
                }else{
                    currentTarget = null;
                }
                if(targets.isEmpty() || !shouldRun() || mc.thePlayer.getDistanceToEntity(currentTarget) > reach.getValue()){
                    mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                    Killaura.fakeBlock = false;
                }
            }
        }

    }

    private void renderClassicTargetHud(float x, float y, EntityLivingBase entity) {
        setMoveX(x);
        setMoveY(y);

        int height = 52;
        int width = 148;
        setMoveH(height);
        setMoveW(width);

        int bg = new Color(0, 0, 0, 140).getRGB();
        Gui.drawRect(x, y, x + width, y + height, bg);

        GlStateManager.resetColor();
        GlStateManager.color(1f,1f,1f,1f);
        GuiInventory.drawEntityOnScreen((int) x + 12, (int) y + 36,  16, 0, 0, entity);
        mc.fontRendererObj.drawString("Name: " + entity.getName(), (int) (x + 12 + 16), (int) (y + 6), -1);
    }

    long attacked;
    private ArrayList<StarParticle> stars = new ArrayList<>();
    Color hurtTimeColor = new Color(-1);

    public void renderStarTargetHud(float x, float y, EntityLivingBase entityToRender) {
        setMoveX(x);
        setMoveY(y);

        int height = 52;
        int width = 130;
        setMoveW(width);
        setMoveH(height);
        if(entityToRender.hurtTime >= 9){
            boolean unison = false;
            if(unison){
                hurtTimeColor = ColorUtils.randomGray();
            }
            if(!unison){
                for(StarParticle s : stars){
                    s.setColorInstant(ColorUtils.randomGray().getRGB());
                    s = s.setColor(ColorUtils.randomGray());
                }
            }
        }
        for (StarParticle star : stars) {
            int g = (int) uwu.noctura.utils.MathHelper.lerp(0.03, new Color(star.getColor()).getGreen(), 255);
            int b = (int) uwu.noctura.utils.MathHelper.lerp(0.03, new Color(star.getColor()).getBlue(), 255);
            int r = (int) uwu.noctura.utils.MathHelper.lerp(0.03, new Color(star.getColor()).getRed(), 255);
            star.setColorInstant(new Color(r, g, b, MathHelper.clamp_int((int)(star.alpha * 255), 0, 100)).getRGB());
            star.update((int) x, (int) y, (int) x + width, (int) y + height, stars);
            star.render();
        }
        GlStateManager.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.drawUnfilledRectangle(x, y, x + width, y + height, 0, -1);
        Gui.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 90).getRGB());
        GL11.glDisable(GL11.GL_BLEND);

        TTFFontRenderer tfont = Noctura.INSTANCE.getFontManager().getFont("Good 12");
        //tfont.drawStringWithShadow(entityToRender.getName(), x + 4, y + 4, -1);
        mc.fontRendererObj.drawStringWithShadow(entityToRender.getName(), x + 4, y + 4, -1);

        // health bar
        RenderUtil.drawUnfilledRectangle(x + 12, y + 36, x + width - 12, y + height - 8, 0, -1);
        // (width - 12) * percent
        float percent = entityToRender.getHealth() / entityToRender.getMaxHealth();
        tempX = (float) uwu.noctura.utils.MathHelper.lerp(0.04, tempX, (width - 12) * percent); // total width of the health bar depending on the health * percent
        Gui.drawRect(x + 13, y + 37, Math.max(x + tempX - 1, x + 13), y + height - 8 - 1, Color.red.getRGB());

        String hp = WorldUtil.wrappedHealth(entityToRender) + "hp";
        float healthX = x + 4 + ((width - 12) / 2f) - (tfont.getWidth(hp) / 2f);
        tfont.drawStringWithShadow(hp, healthX, y + height - 14, -1);
    }

    private int getHurtTimeColor(EntityLivingBase entity) {
        int hurtTime = entity.hurtTime;
        int maxHurtTime = entity.maxHurtTime;
        float factor = Math.min(1.0f, (float) hurtTime / maxHurtTime);
        int red = (int) (255 * factor);
        int green = (int) (255 * (1 - factor));
        int blue = (int) (255 * (1 - factor));
        return (red << 16) | (green << 8) | blue;
    }

    float cachedX, cachedY, cachedW, cachedH;

    float animation = 0.0f;
    public void renderNocturaHud(float x, float y, Entity entityToRender) {

        TTFFontRenderer font = getFont("Good", 18);
        if(!isValid(entityToRender, 6.0f))return;
        String target = entityToRender.getName();
        float health = ((EntityLivingBase)entityToRender).getHealth();
        float maxHealth = ((EntityLivingBase)entityToRender).getMaxHealth();
        float percentHealth = (health / maxHealth) * 100.0f;
        int armorPoints = ((EntityLivingBase) entityToRender).getTotalArmorValue();
        int playerArmorPoints = mc.thePlayer.getTotalArmorValue();
        animation = (float) uwu.noctura.utils.MathHelper.lerp(0.1f, animation, (percentHealth / 100f));

        float endX = x + 130;
        float endY = y + 36;
        setMoveH(36); setMoveW(130);
        cachedH = getMoveH(); cachedW = getMoveW(); cachedX = getMoveX(); cachedY = getMoveY();

        Gui.drawRect(x, y, endX, endY, new Color(0, 0, 0, 110).getRGB());
        RenderUtil.drawRoundedRect2(x + 4, y + 22, x - 4 - (animation * (x - endX)), endY - 4, 4, getHealthColor((EntityLivingBase) entityToRender));

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(x
                ,y,endX, endY);

        boolean advantage = playerArmorPoints > armorPoints;
        font.drawString(target, x + 4, y + 2, -1);
        font.drawString(advantage ? "Armor Advantage" : "Armor Disadvantage", x + 4, y + 12, advantage ? new Color(20, 200, 100).getRGB() : new Color(220, 20, 80).getRGB());
        GaussianBlur.renderBlur(8f);

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();

        RenderUtil.drawRoundedRect2(x + 4, y + 22, x - 4 - (animation * (x - endX)), endY - 4, 4, getHealthColor((EntityLivingBase) entityToRender));
        font.drawString(target, x + 4, y + 2, -1);
        font.drawString(advantage ? "Armor Advantage" : "Armor Disadvantage", x + 4, y + 12, advantage ? new Color(20, 200, 100).getRGB() : new Color(220, 20, 80).getRGB());

        RenderUtil.drawUnfilledRectangle(x, y-1, endX, endY, 1, new Color(0, 0, 0).getRGB(), 2);
    }

    public void modernBlock(){
        final PacketWrapper block = PacketWrapper.create(ServerboundPackets1_9.USE_ITEM, Noctura.INSTANCE.userConnection);
        block.write(Types.VAR_INT, 1);

        try {
            block.sendToServer(Protocol1_9To1_8.class); // Protocol class names are: server -> client version
        } catch (Exception e) {
            // Packet sending failed
            throw new RuntimeException(e);
        }
    }

    public void legitAim(Entity target, EventMotion e) {
        if (target == null) return;
        float[] targetRotations = getRotations(target);
        float targetYaw = targetRotations[0] % 360;
        float targetPitch = targetRotations[1] % 360;
        float currentYaw = mc.thePlayer.rotationYaw % 360;
        float currentPitch = mc.thePlayer.rotationPitch % 360;
        float yawDifference = targetYaw - currentYaw;
        if (yawDifference < -180) {
            yawDifference += 360;
        } else if (yawDifference > 180) {
            yawDifference -= 360;
        }
        float pitchDifference = targetPitch - currentPitch;
        if (pitchDifference < -180) {
            pitchDifference += 360;
        } else if (pitchDifference > 180) {
            pitchDifference -= 360;
        }
        float yawSpeed = 0.95f;
        float pitchSpeed = 0.95f;
        float deltaYaw = yawDifference / yawSpeed;
        float deltaPitch = pitchDifference / pitchSpeed;
        mc.mouseHelper.deltaX = (int) deltaYaw;
        mc.mouseHelper.deltaY = (int) deltaPitch;
        e.setYaw(mc.mouseHelper.deltaX);
        e.setPitch(mc.mouseHelper.deltaY * -1);
    }

    public float[] applyGCD(float yaw, float pitch, float prevYaw, float prevPitch){
        final float deltaPitch = MathHelper.wrapAngleTo180_float(pitch - prevPitch),
                deltaYaw = MathHelper.wrapAngleTo180_float(yaw - prevYaw);
        final double d = 0.5 * (double)0.6f + (double)0.2f, e = Math.pow(d, 3), f = e * 8.0;
        final float cursorDeltaX = Math.round(deltaYaw / f / 0.15f * 8) / 8f,
                cursorDeltaY = Math.round(deltaPitch / f / 0.15f * 8) / 8f;
        final double i = cursorDeltaX * f, j = cursorDeltaY * f;
        return new float[]{prevYaw + (float) (i * 0.15f), MathHelper.clamp_float(prevPitch + (float) (j * 0.15f), -90, 90)};
    }
    /*
    public Rotation fixed(final Rotation prev) {
        final float deltaPitch = MathHelper.wrapDegrees(this.pitch - prev.pitch),
                deltaYaw = MathHelper.wrapDegrees(this.yaw - prev.yaw);
        final double d = 0.5 * (double)0.6f + (double)0.2f, e = Math.pow(d, 3), f = e * 8.0;
        final float cursorDeltaX = Math.round(deltaYaw / f / 0.15f * 8) / 8f,
                cursorDeltaY = Math.round(deltaPitch / f / 0.15f * 8) / 8f;
        final double i = cursorDeltaX * f, j = cursorDeltaY * f;

        return new Rotation(
                prev.yaw + (float) (i * 0.15f),
                Math.clamp(prev.pitch + (float) (j * 0.15f), -90, 90)
        );
    }
     */

    private void hypixelBlock(Event ev){

    }

    @Override
    public void onDisable() {
        Killaura.fakeBlock = false;
        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
        Noctura.INSTANCE.getModuleManager().getModule(LegitSprint.class).setToggled(true);
    }

    public void yaw(float yaw, EventMotion e){
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
        e.setYaw(yaw);
        if(lockView.isEnabled()){
            mc.thePlayer.rotationYaw = yaw;
        }
    }
    public void pitch(float pitch, EventMotion e){
        mc.thePlayer.rotationPitchHead = pitch;
        e.setPitch(pitch);
        if(lockView.isEnabled()){
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    public boolean shouldRun(){
        return mc.thePlayer.ticksExisted > 10 && mc.thePlayer != null && mc.theWorld != null
                && !Noctura.INSTANCE.getModuleManager().getModule(Scaffold.class).isToggled();
    }

    public boolean isValid(Entity e, float reach){
        boolean finalValid = false;
        if(mc.theWorld == null) return false;
        if(mc.thePlayer == null) return false;
        if(mc.thePlayer.ticksExisted < 10) return false;
        if(e == null) return false;
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

    public static boolean isLookingAtEntity(EntityPlayer player, Entity target, double maxDistance, EventMotion em) {
        Vec3 eyePosition = player.getPositionEyes(1.0F);
        AxisAlignedBB targetBB = target.getEntityBoundingBox();
        Vec3 lookVector = player.getLook(1.0F, em.getYaw(), em.getPitch(), em.getPrevYaw(), em.getPrevPitch());
        Vec3 endPosition = eyePosition.addVector(lookVector.xCoord * maxDistance, lookVector.yCoord * maxDistance, lookVector.zCoord * maxDistance);
        MovingObjectPosition result = targetBB.calculateIntercept(eyePosition, endPosition);
        if(result == null){
            System.out.println("null");
        }
        return result != null;
    }

    @Override
    public void onEnable() {
        int height = 52;
        int width = 130;
        if(
        Noctura.INSTANCE.getModuleManager().getModule(LegitSprint.class).isToggled() && nosprint.isEnabled()){
            Noctura.INSTANCE.getModuleManager().getModule(LegitSprint.class).toggle();
        }
        StarParticle starTemplate = new StarParticle(getMoveX(), getMoveY()).setSize(3f).setAlphaChangeRate(0.001f);
        RenderUtil.generateStars(80, stars, (int)getMoveX(), (int)getMoveY(), (int)getMoveX()+width, (int)getMoveX()+height, starTemplate, -0.125f, 0.125f, -0.125f, 0.125f);
    }

    public void attack(Entity target, EventMotion em){
        if(autoblock.isEnabled()){
            if(autoblockMode.is("Hold") && mc.thePlayer.getItemInUseDuration() <= 1 && isHoldingSword()){
                return;
            }
        }

        boolean ray = !raycast.isEnabled() || isLookingAtEntity(mc.thePlayer, target, reach.getValue(), em);
        mc.thePlayer.swingItem();
        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        if(ray){
            Criticals.isCrits = true;
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        }
    }
    public boolean isHoldingSword(){
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
    float thing = 0f;

    public void renderTargetHudBasic(Event e, float x, float y){
        if(e instanceof EventRender2D){
            GlStateManager.color(1f, 1f, 1f);
            Entity target = currentTarget;


            setMoveH(36);
            setMoveW(130);
            cachedH = getMoveH();
            cachedW = getMoveW();
            cachedX = getMoveX();
            cachedY = getMoveY();


            if(currentTarget != null){
                float maxDist = 6;
                if(((EntityLivingBase)currentTarget).getHealth() <= 0 || !isValid(currentTarget, maxDist + 5)) return;
                float offset = mc.thePlayer.getDistanceToEntity(target) - maxDist;
                if(mc.thePlayer.getDistanceToEntity(target) <= maxDist + 5){
                    if(currentTarget instanceof EntityPlayer){
                        drawHead( (AbstractClientPlayer)target, (x + 4) / thing, (y + 4) / thing, 44 / thing, 44 / thing);
                    }else{
                        GuiInventory.drawEntityOnScreen((int) (x + 20), (int) (y + 50), 25, target.rotationYaw, 0, (EntityLivingBase) target);
                    }
                    Gui.drawRect(x / thing, y / thing, (x + 192) / thing, (y +52) / thing, new Color(0, 0, 0, 90).getRGB());
                    TTFFontRenderer tFont = Noctura.INSTANCE.fontManager.getFont("auxy " + (21 - offset));
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

    public static Vec3 vec(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp_double(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp_double(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp_double(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }
}
