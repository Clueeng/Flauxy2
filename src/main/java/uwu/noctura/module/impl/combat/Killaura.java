package uwu.noctura.module.impl.combat;

import com.viaversion.viabackwards.api.data.MappedLegacyBlockItem;
import com.viaversion.viarewind.protocol.v1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.math.BlockPositionType1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.v1_9_1to1_9_3.packet.ServerboundPackets1_9_3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.commands.impl.CommandSetupCPS;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.*;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.player.Scaffold;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.WorldUtil;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;
import uwu.noctura.utils.timer.Timer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static uwu.noctura.utils.font.FontManager.getFont;
import static uwu.noctura.utils.render.ColorUtils.getGradientOffset;
import static uwu.noctura.utils.render.ColorUtils.getHealthColor;
import static uwu.noctura.utils.render.RenderUtil.drawFace;

@ModuleInfo(name = "Killaura", displayName = "Killaura", key = Keyboard.KEY_R, cat = Category.Combat)
public class Killaura extends Module {

    public static ArrayList<Long> dataClickOne = new ArrayList<>();
    public static ArrayList<Long> dataClickTwo = new ArrayList<>();
    public static ArrayList<Long> dataClickThree = new ArrayList<>();

    ModeSetting cpsMode = new ModeSetting("CPS Mode", "Normal","Normal", "Data Set");
    NumberSetting cps = new NumberSetting("CPS", 12, 1, 20, 0.001).setCanShow(m -> cpsMode.is("Normal"));
    public NumberSetting reach = new NumberSetting("Reach", 4.2, 2.5, 6, 0.1);


    ModeSetting rotations = new ModeSetting("Rotations", "Instant", "Instant", "Verus", "None", "Legit");
    //ModeSetting autoblock = new ModeSetting("Autoblock", "Hold", "Hold", "Item Use");
    ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post", "Mix");

    BooleanSetting nosprint = new BooleanSetting("No Sprint", false);
    NumberSetting noSprintDelay = new NumberSetting("Delay", 1, 1, 10, 1).setCanShow(m -> nosprint.getValue());

    BooleanSetting autoblock = new BooleanSetting("Autoblock", true);
    ModeSetting autoblockMode = new ModeSetting("Mode", "Hold", "Hold", "Item Use", "Fake", "Redesky", "Hypixel", "1.9").setCanShow(m -> autoblock.getValue());
    //ModeSetting type = new ModeSetting("Type", "Pre", "Pre", "Post");
    BooleanSetting showTargets = new BooleanSetting("Show Targets", true);
    BooleanSetting raycast = new BooleanSetting("Raycast", true).setCanShow(m -> !rotations.is("None"));

    BooleanSetting players = new BooleanSetting("Players", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting mobs = new BooleanSetting("Mobs", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting animals = new BooleanSetting("Animals", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting shop = new BooleanSetting("NCP's", false).setCanShow(m -> showTargets.getValue());
    BooleanSetting wall = new BooleanSetting("Through Walls", true);
    BooleanSetting targethud = new BooleanSetting("TargetHUD", true);
    BooleanSetting movefix = new BooleanSetting("Move Fix", true);
    ModeSetting targetHudMode = new ModeSetting("TargetHUD Mode", "Flaily", "Flaily", "Astolfo", "Rainbow", "Noctura").setCanShow(m -> targethud.getValue());
    Timer timer = new Timer();


    public Killaura(){
        addSettings(autoblockMode, type, rotations, raycast, cpsMode, cps, reach, autoblock, nosprint, noSprintDelay, movefix, wall, showTargets, players, mobs, animals, shop, targethud, targetHudMode);
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

    float tempX = 0;
    public Entity currentTarget;
    List<Entity> targets;


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
            EventRender2D event = (EventRender2D) ev;
            if(currentTarget != null){
                ScaledResolution sr = new ScaledResolution(mc);
                switch (targetHudMode.getMode()){
                    case "Noctura":{
                        renderNocturaHud(ev, sr.getScaledWidth() / 2f + 35, sr.getScaledHeight() / 2f - 22);
                        break;
                    }
                    case "Flaily":{
                        renderTargetHudBasic(ev, sr.getScaledWidth() / 2 + 35, sr.getScaledHeight() / 2 - 45);
                        break;
                    }
                    case "Rainbow": {
                        TTFFontRenderer tFont = Noctura.INSTANCE.fontManager.getFont("auxy " + (21));
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
                            float x = scaledWidth / 2.0F - 170.0F;
                            float y = scaledHeight / 2.0F - 25;
                            int color, xHealthbar, yHealthbar;
                            Color healthColor = Color.GREEN;
                            float health = target.getHealth();
                            float maxHealth = target.getMaxHealth();

                            if (health < maxHealth / 1f) healthColor = new Color(93, 234, 42);
                            if (health < maxHealth / 1.25f) healthColor = new Color(104, 219, 32);
                            if (health < maxHealth / 1.5f) healthColor = new Color(219, 185, 32);
                            if (health < maxHealth / 2) healthColor = new Color(219, 157, 32);
                            if (health < maxHealth / 3) healthColor = new Color(219, 116, 32);
                            if (health < maxHealth / 4) healthColor = new Color(219, 48, 32);
                            healthColor = new Color(healthColor.getRed(), healthColor.getGreen(), healthColor.getBlue(), (int)(1 * 255));
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
                            this.mc.fontRendererObj.drawStringWithShadow((Math.round((target.getHealth() / 2.0F) * 10.0D) / 10.0D) + "\u2764", (x + 16.0F), (y + 13.0F), (new Color(color)).darker().getRGB());
                            GL11.glPopMatrix();
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            GuiInventory.drawEntityOnScreen((int) x + 16, (int) y + 55, 25, target.rotationYaw, -target.rotationPitch, target);
                            xHealthbar = 30;
                            yHealthbar = 46;
                            add = 120.0F;
                            if(tempX < target.getHealth() / target.getMaxHealth() * add){
                                tempX+=0.48f;
                            }else{
                                tempX-=0.16f;
                            }
                            drawRect(x + xHealthbar, y + yHealthbar, add, 8.0F, (new Color(color)).darker().darker().darker());
                            drawRect(x + xHealthbar, y + yHealthbar, tempX, 8.0F, healthColor);
                            addX = (x + xHealthbar + target.getHealth() / target.getMaxHealth() * add);
                            for (index = 1; index < 5; index++) {
                                if (target.getEquipmentInSlot(index) == null) ;
                            }
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
                                case "Item Use":{
                                    if(isHoldingSword()){
                                        //mc.thePlayer.setItemInUse(mc.thePlayer.inventory.getCurrentItem(), mc.thePlayer.inventory.getCurrentItem().getItem().getMaxItemUseDuration(mc.thePlayer.inventory.getCurrentItem()));
                                    }
                                    break;
                                }
                                case "1.9":{
                                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Killaura", "1.9 Blocking is not working"));
                                    autoblockMode.setSelected("Fake");
                                    //modernBlock();
                                    break;
                                }
                                case "Hypixel":{
                                    //hypixelBlock(ev);
                                    if(isHoldingSword()){
                                        mc.gameSettings.keyBindUseItem.pressed = true;
                                    }
                                    break;
                                }
                                case "Hold":{
                                    if(isHoldingSword()){
                                        mc.gameSettings.keyBindUseItem.pressed = true;
                                        if (event.isPost()) {
                                            if (mc.thePlayer.swingProgressInt == -1) {
                                                PacketUtil.sendPacket(new C07PacketPlayerDigging(
                                                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                                            } else if (mc.thePlayer.swingProgressInt == 0) {
                                                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(
                                                        new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                                            }
                                        }
                                    }
                                    break;
                                }

                                //mc.thePlayer.setItemInUse(mc.thePlayer.inventory.getItemStack(), 1);
                            }
                        }

                        switch(rotations.getMode()){
                            case "Verus":{
                                //Wrapper.instance.log(String.valueOf(random));
                                if(type.is("Pre") && event.isPre()){
                                    float smoothnessX = 30;
                                    float smoothnessY = 30;
                                    if(event.getYaw() < getRotations(target)[0]){
                                        float yaw = event.getYaw() + smoothnessX;
                                        yaw(yaw, event);
                                    }
                                    if(event.getYaw() > getRotations(target)[0]){
                                        float yaw = event.getYaw() + smoothnessX;
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
                                /*
                                if (this.mc.inGameHasFocus && flag)
                                {
                                    this.mc.mouseHelper.mouseXYChange();
                                    float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                                    float f1 = f * f * f * 8.0F;
                                    float f2 = (float)this.mc.mouseHelper.deltaX * f1;
                                    float f3 = (float)this.mc.mouseHelper.deltaY * f1;
                                    this.smoothCamYaw = 0.0F;
                                    this.smoothCamPitch = 0.0F;`
                                    this.mc.thePlayer.setAngles(f2, f3);
                                }
                                 */
                                //legitAim(target, event);
                                float[] targetRot = getRotations(target);
                                float targetYaw = targetRot[0];
                                float targetPitch = targetRot[1];

                                float[] gcd = applyGCD(targetYaw, targetPitch, event.getPrevYaw(), event.getPrevPitch());
                                //event.setYaw(gcd[0]);
                                //event.setPitch(gcd[1]);
                                //if(!isLookingAtEntity(mc.thePlayer, target, reach.getValue(), event)){
                                yaw(gcd[0], event);
                                pitch(gcd[1], event);
                                //}

                                //yaw(targetYaw, event);
                                //pitch(targetPitch, event);
                                break;
                            }
                            case "Instant":{
                                yaw(getRotations(target)[0], event);
                                pitch(getRotations(target)[1], event);
                                break;
                            }
                        }
                        long clicks = 0;
                        switch (cpsMode.getMode()){
                            case "Data Set":{
                                int index = (int)Math.ceil(Math.random() * 29);
                                ArrayList<Long> allData = new ArrayList<>();
                                allData.addAll(dataClickOne);
                                allData.addAll(dataClickTwo);
                                allData.addAll(dataClickThree);
                                if(allData.size() <= 29 || CommandSetupCPS.runSetup){
                                    Wrapper.instance.log("Please setup the cps correctly using the .cps command");
                                    Wrapper.instance.log("Fallback to normal clicking");
                                    cpsMode.setSelected("Normal");
                                    return;
                                }
                                clicks = allData.get(index);
                                break;
                            }
                            case "Normal":{
                                clicks = (long) (1000 / cps.getValue() + Math.random());
                                break;
                            }
                        }
                        if(timer.hasTimeElapsed(clicks, true)){

                            if (target instanceof EntityPlayer) { // idk i need to check if player bc it will crash and im to lazy to fix or find the error idc rn
                                if (!mc.thePlayer.isInvisibleToPlayer((EntityPlayer) target) && !wall.isEnabled())
                                    return;
                            }else{
                                targets.remove(target);
                            }
                            if(type.is("Post")) attack(target, event);
                            if(type.is("Pre") && event.isPre()) attack(target, event);
                            if(type.is("Mix") && event.isPre() || event.isPost()) attack(target, event);
                        }else{
                            switch(autoblockMode.getMode()){
                                case "Redesky":{
                                    if(mc.thePlayer.getHeldItem() != null){
                                        if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword){
                                            if(mc.thePlayer.ticksExisted % 3 == 0){
                                                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 1);
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
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

    float animation = 0.0f;
    public void renderNocturaHud(Event ev, float x, float y) {

        TTFFontRenderer font = getFont("Good", 18);
        if(!isValid(currentTarget, 6.0f))return;
        String target = currentTarget.getName();
        float health = ((EntityLivingBase)currentTarget).getHealth();
        float maxHealth = ((EntityLivingBase)currentTarget).getMaxHealth();
        float percentHealth = (health / maxHealth) * 100.0f;
        int armorPoints = 0;
        if(currentTarget instanceof EntityLivingBase){
            armorPoints = ((EntityLivingBase)currentTarget).getTotalArmorValue();
        }
        int playerArmorPoints = mc.thePlayer.getTotalArmorValue();
        animation = (float) uwu.noctura.utils.MathHelper.lerp(0.1f, animation, (percentHealth / 100f));

        float endX = x + 130;
        float endY = y + 36;

        Gui.drawRect(x, y, endX, endY, new Color(0, 0, 0, 110).getRGB());

        //Gui.drawRect(x + 4, y + 22, x - 4 - (animation * (x - endX)), endY - 4, getHealthColor((EntityLivingBase) currentTarget));

        RenderUtil.drawRoundedRect2(x + 4, y + 22, x - 4 - (animation * (x - endX)), endY - 4, 4, getHealthColor((EntityLivingBase) currentTarget));

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(x
                ,y,endX, endY);

        boolean advantage = playerArmorPoints > armorPoints;
        font.drawString(target, x + 4, y + 2, -1);
        font.drawString(advantage ? "Armor Advantage" : "Armor Disadvantage", x + 4, y + 12, advantage ? new Color(20, 200, 100).getRGB() : new Color(220, 20, 80).getRGB());
        //Gui.drawRect(1, 1, 1, 1, new Color(0, 0, 0, 10).getRGB());
        GaussianBlur.renderBlur(8f);
        //Gui.drawRect(1, 1, 1, 1, new Color(0, 0, 0, 10).getRGB());

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();

        RenderUtil.drawRoundedRect2(x + 4, y + 22, x - 4 - (animation * (x - endX)), endY - 4, 4, getHealthColor((EntityLivingBase) currentTarget));
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
        float d = ((this.mc.gameSettings.mouseSensitivity) * 0.6F + 0.2F) * 1.1f;
        float e = d * d * d;
        //float f2 = (float)this.mc.mouseHelper.deltaX * f1;
        //float f3 = (float)this.mc.mouseHelper.deltaY * f1;
        float f = e * 8.0F;

        double yawSens = (yaw - prevYaw) * f;
        double pitchSens = (pitch - prevPitch) * f;

        return new float[]{(float) (prevYaw + yawSens), (float) (prevPitch + pitchSens)};
    }

    private void hypixelBlock(Event ev){

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

    public void attack(Entity target, EventMotion em){
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
