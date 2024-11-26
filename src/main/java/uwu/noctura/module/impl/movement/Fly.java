package uwu.noctura.module.impl.movement;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.*;
import uwu.noctura.event.impl.packet.EventMove;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.*;
import uwu.noctura.utils.timer.Timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.input.Keyboard.isKeyDown;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {
    public Timer  timer = new Timer();
    double tempY = 0;
    public ArrayList<Packet> packets = new ArrayList<>();
    private LinkedList<Packet> packetsLinked = new LinkedList<>();
    public ConcurrentLinkedQueue<Packet> blinkpackets = new ConcurrentLinkedQueue<>();

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "NCP Bow", "Collision", "Test", "Funcraft", "Vulcan", "Glide", "Hypixel", "Godseye");
    public ModeSetting glideMode = new ModeSetting("Mode","Chunk","Chunk", "Web");
    public BooleanSetting CollisionNotSpeed = new BooleanSetting("Keep Speed", true).setCanShow(m -> mode.is("Collision"));
    public NumberSetting Collisionspeed = new NumberSetting("Speed", 0.4, 0.1, 3, 0.05).setCanShow(m -> mode.is("Collision") && !CollisionNotSpeed.getValue());
    public NumberSetting Collisiontimer = new NumberSetting("Timer", 1, 0.1, 10, 0.025).setCanShow(m -> mode.is("Collision"));
    public BooleanSetting CollisionspoofY = new BooleanSetting("Spoof Y", true).setCanShow(m -> mode.is("Collision"));
    public BooleanSetting Collisiondamage = new BooleanSetting("Damage", true).setCanShow(m -> mode.is("Collision"));
    public ModeSetting testMode = new ModeSetting("Test Mode", "Test 1", "Test 1", "Test 2", "Test 3").setCanShow(m -> mode.is("Test"));
    public NumberSetting speed = new NumberSetting("Speed", 1, 0.05, 5, 0.05).setCanShow(m -> mode.is("Vanilla"));

    public NumberSetting funcraftSpeed = new NumberSetting("Speed", 0.8, 0.05, 2, 0.05).setCanShow(m  -> mode.is("Funcraft"));


    BlockPos oldPos;
    List<BlockPos> blockPosList = new ArrayList<>();

    boolean removeBlock = false;
    int flyTicks = 0, flyTicks2 = 0;


    public Fly() {
        addSettings(mode, glideMode, CollisionNotSpeed, Collisionspeed, Collisiontimer, CollisionspoofY, Collisiondamage, testMode, funcraftSpeed, speed);
    }
    private double flySpeed = 0f;
    private double oldYaw, oldPitch;

    public static boolean exemptWebs = false;
    
    public void onEvent(Event e) {
        if(mc.thePlayer == null)return;
        if(e instanceof EventUpdate){

            this.setArrayListName("Flight " + EnumChatFormatting.WHITE + mode.getMode());
        }
        switch(mode.getMode()){
            case "Godseye":{
                godseye(e);
                break;
            }
            case "NCP Bow":{
                if(e instanceof EventMotion){
                    EventMotion ev = (EventMotion)e;
                    switchToBowSlot();
                    shootBow(ev);
                    if(mc.thePlayer.hurtTime > 0){
                        wasHit = true;
                    }
                    if(go && !wasHit){
                        MoveUtils.cancelMoveInputs();
                        MoveUtils.stopMoving();
                    }
                    if(go && wasHit){
                        MoveUtils.enableMoveInputs();
                        flyTicks++;
                        if(mc.thePlayer.onGround && ev.isPre()){
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY += 0.23f;
                        }
                        if(flyTicks < 2){
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.24, mc.thePlayer.posZ);
                        }
                        mc.timer.timerSpeed = 0.75f;
                        MoveUtils.strafe(MoveUtils.getBaseSpeed() * 3.14);
                        if(flyTicks > 10){
                            this.onDisable();
                            toggle();
                        }
                    }
                }

                break;
            }
            case "Hypixel":{
                hypixel(e);
                break;
            }
            case "Glide":{
                switch (glideMode.getMode()){
                    case "Chunk":{
                        if(e instanceof EventMotion){
                            mc.thePlayer.motionY = -0.09800000190734863;
                        }
                        break;
                    }
                    case "Web":{
                        if(e instanceof EventMotion){
                            mc.thePlayer.motionY = -0.0784000015258789;
                        }
                        break;
                    }
                }

                break;
            }
            case "Vulcan": {
                int waitInAir = 12;
                if(e instanceof EventSendPacket){
                    EventSendPacket es = (EventSendPacket) e;
                    if(flyTicks % waitInAir > 0 && flyTicks % waitInAir < 10 && (PacketUtil.isPacketBlinkPacket(es.getPacket()) || PacketUtil.isPacketPingSpoof(es.getPacket()))){
                        packets.add(es.getPacket());
                        if(packets.size() < 12){
                            es.setCancelled(true);
                        }
                    }
                }

                if (e instanceof EventCollide) {
                    EventCollide ec = (EventCollide) e;
                    AxisAlignedBB air;
                    BlockPos below = new BlockPos(mc.thePlayer.posX, tempY - 1, mc.thePlayer.posZ);

                    if(ec.getPosX() == below.getX() && ec.getPosY() == below.getY() && ec.getPosZ() == below.getZ()){
                        air = AxisAlignedBB.fromBounds(
                                ec.getPosX(),
                                ec.getPosY(),
                                ec.getPosZ(),
                                ec.getPosX() + 1.0D,
                                ec.getPosY() + 1.0D,
                                ec.getPosZ() + 1.0D);
                        ec.setBoundingBox(air);
                    }
                }

                if(e instanceof EventMotion){
                    EventMotion ev = (EventMotion)e;
                    if(ev.isPre()){
                        flyTicks++;
                        dist += (float) MoveUtils.getMotion();
                    }
                    if(flyTicks % 10 == 0 && ev.isPre()){
                        mc.thePlayer.swingItem();
                        PacketUtil.sendSilentPacket(new
                                C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.22d, mc.thePlayer.posZ), 1, mc.thePlayer.getHeldItem(),
                                0,0,0));
                    }
                    if(mc.gameSettings.keyBindJump.pressed){
                        tempY = mc.thePlayer.posY;
                    }
                    if(mc.thePlayer.onGround && ev.isPre()){
                        mc.thePlayer.jump();
                        //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.22d, mc.thePlayer.posZ);
                        //mc.thePlayer.getHeldItem()
                        MoveUtils.strafe(MoveUtils.getMotion() * 0.98f);
                    }
                }
                break;
            }
            case "Verus": {
                if(e instanceof EventMotion){
                    EventMotion em = (EventMotion) e;
                    em.setOnGround(true);
                    mc.thePlayer.motionY = 0;
                }
                break;
            }
            case "Vanilla":{
                if(e instanceof EventUpdate){
                    EventUpdate ev = (EventUpdate) e;
                    mc.thePlayer.motionY = 0;
                    if(mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer.motionY += 0.42f * speed.getValue();
                    }
                    if(mc.gameSettings.keyBindSneak.pressed){
                        mc.thePlayer.motionY -= 0.42f * speed.getValue();
                    }
                    if(mc.thePlayer.isMoving()){
                        MoveUtils.strafe(speed.getValue());
                    }else{
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                    }

                }
                break;
            }

            case "Funcraft":{
                if(e instanceof EventMotion){
                    if(flyTicks < 2){
                        mc.timer.timerSpeed = 0.5f;
                        flySpeed = speed.getValue() * 2f;
                        //MoveUtils.damage(MoveUtils.Bypass.VANILLA);
                        mc.thePlayer.motionY = 0.42f;
                    }else{
                        MoveUtils.strafe(Math.max(flySpeed,0.33));
                        flySpeed -= flySpeed / 269   ;
                        if(mc.thePlayer.ticksExisted % 2 == 0){
                            mc.timer.timerSpeed = 1f;
                            mc.thePlayer.motionY = 0.03f;
                        }else{
                            mc.thePlayer.motionY = -0.03f;
                        }
                    }
                    flyTicks++;
                }
                break;
            }
            case "Test":{
                switch(testMode.getMode()){
                    case "Test 1":{
                        if(e instanceof EventMove){
                            flyTicks++;
                            EventMove ev = (EventMove) e;
                            mc.timer.timerSpeed = 1f;

                            if(mc.thePlayer.onGround && flyTicks < 100) {
                                flyTicks = 100;
                            }else{
                                mc.thePlayer.onGround = false;
                                if(mc.thePlayer.isSneaking()){
                                    ev.setY(mc.thePlayer.posY - 0.5f);
                                    new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2.5D, mc.thePlayer.posZ, false);
                                    //mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 0.1f, mc.thePlayer.posY + 3.5D, mc.thePlayer.posZ, false));
                                    return;
                                }
                                if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && mc.currentScreen == null){
                                    oldYaw = mc.thePlayer.rotationYaw;
                                    oldPitch = mc.thePlayer.rotationPitch;
                                    MoveUtils.strafe(0);
                                    mc.gameSettings.keyBindForward.pressed = false;
                                    mc.gameSettings.keyBindBack.pressed = false;
                                    mc.gameSettings.keyBindLeft.pressed = false;
                                    mc.gameSettings.keyBindRight.pressed = false;
                                    mc.gameSettings.keyBindJump.pressed = false;
                                    ev.setY(mc.thePlayer.posY - 0.5f);
                                    PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.5D, mc.thePlayer.posZ, false));
                                    return;
                                }
                                //ev.setY(mc.thePlayer.posY);
                                //PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0D, mc.thePlayer.posZ, false));

                            }
                        }
                        if(e instanceof EventSendPacket){
                            EventSendPacket es = (EventSendPacket) e;
                            if(es.getPacket() instanceof C00PacketKeepAlive){
                                es.setPacket(null);
                            }
                        }
                        if (e instanceof EventCollide) {
                            mc.gameSettings.keyBindJump.pressed = false;
                            EventCollide ec = (EventCollide) e;
                            if (!mc.thePlayer.isSneaking()) {
                                if (mc.thePlayer.isSneaking())
                                    return;
                                if (ec.getBlock() instanceof net.minecraft.block.BlockAir && ec.getPosY() < mc.thePlayer.posY)
                                    ec.setBoundingBox(AxisAlignedBB.fromBounds(ec.getPosX(), (int)mc.thePlayer.posY, ec.getPosZ(), ec.getPosX() + 1.0D, (int)mc.thePlayer.posY, ec.getPosZ() + 1.0D));
                            }
                        }
                        break;
                    }
                    case "Test 2":{// vulcan anticheat-test.com
                        if(e instanceof EventMotion){
                            EventMotion ev = (EventMotion) e;
                            if(mc.thePlayer.motionY < -0.4){
                                mc.thePlayer.motionY = 0;
                            }
                        }
                        break;
                    }
                    case "Test 3":{// vulcan antich eat-test.com

                        if(e instanceof EventMotion){
                            EventMotion ev = (EventMotion) e;
                            if(mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && mc.thePlayer.ticksExisted % 2 == 00){
                                mc.timer.timerSpeed = 0.35f;
                                mc.thePlayer.motionY = 0.42;
                            }
                            double yUp = -0.22;
                            if(mc.gameSettings.keyBindJump.pressed){
                                yUp = -0.02;
                            }
                            if(mc.gameSettings.keyBindSneak.pressed){
                                yUp = -0.42;
                            }

                            if(mc.thePlayer.motionY < yUp){
                                mc.timer.timerSpeed = 0.15f;
                                mc.thePlayer.motionY = 0.22;
                                if(yUp == -0.02){
                                    mc.timer.timerSpeed = .80f;
                                }
                            }
                            if(!mc.gameSettings.keyBindJump.pressed){
                                if(mc.timer.timerSpeed < 1.1f && mc.timer.timerSpeed > 0.5){
                                    MoveUtils.strafe(mc.gameSettings.keyBindJump.pressed ? 0.71: 0.69 + (MoveUtils.getMotion() / 6));
                                    if(!mc.gameSettings.keyBindJump.pressed){
                                        mc.thePlayer.motionY = -0.01;
                                    }
                                }
                                mc.timer.timerSpeed = Math.min(2.7f, mc.timer.timerSpeed + 0.14f);
                            }else{

                                if(mc.timer.timerSpeed < 0.7f && mc.timer.timerSpeed > 0.5){
                                    MoveUtils.strafe(mc.gameSettings.keyBindJump.pressed ? 0.71: 0.69 + (MoveUtils.getMotion() / 6));
                                    if(!mc.gameSettings.keyBindJump.pressed){
                                        mc.thePlayer.motionY = -0.01;
                                    }
                                }
                                mc.timer.timerSpeed = Math.min(1.4f, mc.timer.timerSpeed + 0.13f);
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case "Collision":{
                if (e instanceof EventCollide) {
                    EventCollide ec = (EventCollide) e;
                    if (!mc.thePlayer.isSneaking()) {
                        AxisAlignedBB air;
                        BlockPos below = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY -1, mc.thePlayer.posZ);

                        if(ec.getPosX() == below.getX() && ec.getPosY() == below.getY() && ec.getPosZ() == below.getZ()){
                            air = AxisAlignedBB.fromBounds(
                                    ec.getPosX(),
                                    ec.getPosY(),
                                    ec.getPosZ(),
                                    ec.getPosX() + 1.0D,
                                    ec.getPosY() + 1.0D,
                                    ec.getPosZ() + 1.0D);
                            ec.setBoundingBox(air);
                        }
                    }
                }
                if(e instanceof EventMotion) {
                    if(!CollisionNotSpeed.getValue()){
                        MoveUtils.setSpeed(Collisionspeed.getValue());
                    }
                    mc.timer.timerSpeed = (float) Collisiontimer.getValue();
                    if (CollisionspoofY.isEnabled()) {
                        switch (flyTicks) {
                            case 0:
                                ((EventMotion) e).setY(-0.1D - 10D);
                                break;
                            case 1:
                                ((EventMotion) e).setY((float) (+0.1D - 10D));
                                break;
                            case 2:
                                flyTicks = 0;
                                break;
                        }
                        flyTicks++;
                    }
                }
                break;
            }
        }
    }

    boolean godseyeStopSending, godseyeStartChoking;
    ArrayList<Packet> godseyePackets =  new ArrayList<>();
    private void godseye(Event e) {
        if(e instanceof EventMotion){
            EventMotion ev = (EventMotion)e;
            switchToBowSlot();
            shootBow(ev);
            if(mc.thePlayer.hurtTime > 0){
                wasHit = true;
            }
            if(go && !wasHit){
                MoveUtils.cancelMoveInputs();
                MoveUtils.stopMoving();
            }
            if(go && wasHit){
                MoveUtils.enableMoveInputs();
                flyTicks++;
                shootBow(ev);
                mc.thePlayer.motionY *= 0.999;
                mc.timer.timerSpeed = 1.00f;
                if(flyTicks < 2){
                    mc.thePlayer.motionY += 0.54f;
                }
                if(flyTicks > 1 && flyTicks < 75){
                    if(mc.thePlayer.motionY < 0){
                        Wrapper.instance.log("a");
                    }
                }
                if(flyTicks > 75 || (mc.thePlayer.onGround && flyTicks > 30)){
                    //deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
                    Wrapper.instance.log(Math.round(dist) + " blocks traveled");
                    this.onDisable();
                    toggle();
                }
            }
        }
        if(e instanceof EventReceivePacket){
            EventReceivePacket es = (EventReceivePacket) e;
            if (es.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) es.getPacket();
                if (packet.getEntityID() == mc.thePlayer.getEntityId() && go) {
                    float yaw = mc.thePlayer.rotationYaw * ((float) Math.PI / 180F);
                    double motionMultiplier = 9.5;

                    int motionX = (int) (-Math.sin(yaw) * motionMultiplier * 8000);
                    int motionZ = (int) (Math.cos(yaw) * motionMultiplier * 8000);
                    packet.setMotionX(motionX);
                    packet.setMotionY((int) ((packet.getMotionY() / 100f) * (MoveUtils.standingOn(Blocks.slime_block) ? 4.5f : 3.5f)) * 100);
                    packet.setMotionZ(motionZ);

                    MoveUtils.strafe(0.2);
                }
            }
        }
    }

    int bowTick = 0;
    boolean go;
    boolean wasHit = false;
    public int oldItem, itemSpoofed;

    public void switchToBowSlot() {
        boolean already = false;
        if(mc.thePlayer.inventory.getCurrentItem() != null){
            if(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow){
                already = true;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null || already)
                continue;
            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBow) {
                oldItem = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = i;
                itemSpoofed = i;
            }
        }
    }
    public void switchBackToOld(){
        mc.thePlayer.inventory.currentItem = oldItem;
    }

    public void shootBow(EventMotion e){
        e.setPitch(-90f);
        bowTick++;
        if(bowTick > 1){
            if(mc.thePlayer.inventory.getCurrentItem() != null){
                ItemStack s = mc.thePlayer.inventory.getCurrentItem();
                if(s.getItem() instanceof ItemBow){
                    go = true;
                }
            }
            if(go){
                mc.gameSettings.keyBindUseItem.pressed = bowTick < 9;
            }
        }
    }

    int hypixelTicks = 0;
    int decreaseTicks = 0;
    boolean launch;
    private void hypixel(Event e) {
        if(e instanceof EventMotion){
            EventMotion em = (EventMotion) e;
            if(hypixelTicks > 3){
                if(mc.thePlayer.hurtTime > 8) launch = true;
                if(launch){
                    mc.timer.timerSpeed = 0.65f;
                    if(mc.thePlayer.motionY > 0.06 && flyTicks < 10){
                        mc.thePlayer.motionY = 1.198f;
                        MoveUtils.strafe(0.92f - (flyTicks / 20f));
                        flyTicks++;
                    }else{
                        decreaseTicks++;
                    }
                    if(decreaseTicks < 20){
                        if(decreaseTicks < 10){
                            mc.thePlayer.motionY = -0.03f;
                        }else{
                            if(decreaseTicks == 11){
                                if(em.isPre()){
                                    mc.thePlayer.jump();
                                }
                            }
                        }

                    }else{
                        this.onDisable();
                        this.toggle();
                    }
                }
            }
            if(hypixelTicks < 2){
                for(int i = 0; i < 9; i++) {
                    if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                        continue;
                    if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemFireball) {
                        mc.thePlayer.inventory.currentItem = i;
                        break;
                    }
                }
                if(mc.thePlayer.inventory.getCurrentItem() != null){
                    if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFireball)){
                        Wrapper.instance.log("Need a fireball");
                        mc.gameSettings.keyBindUseItem.pressed = false;
                        this.toggle();
                        return;
                    }
                }else{
                    Wrapper.instance.log("Need a fireball");
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    this.toggle();
                    return;
                }
            }else{
                em.setPitch(90f);
                mc.thePlayer.rotationPitchHead = 90f;
                if(hypixelTicks >= 4 && hypixelTicks <= 6){
                    mc.gameSettings.keyBindUseItem.pressed = true;
                }
                if(hypixelTicks > 6){
                    mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                }
            }
        }
        if(e instanceof EventUpdate){
            if(((EventUpdate) e).isPre()){
                hypixelTicks += 1;
            }
        }
    }


    @Override
    public void onEnable() {
        oldPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        exemptWebs = false;
        tempY = 0;
        dist = 0.0f;
        if(mode.is("Vulcan")){
            tempY = mc.thePlayer.posY;
        }
        go = false;
        bowTick = 0;
        godseyePackets.clear();
        flyTicks = 0;
        hypixelTicks = 0;
        decreaseTicks = 0;
        flyTicks2 = 0;
        blinkpackets.clear();
        switch(mode.getMode()){
            case "Collision":{
                if (Collisiondamage.isEnabled()) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY +3.25, mc.thePlayer.posZ, true));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                }
                break;
            }
        }

    }

    float dist = 0.0f;
    @Override
    public void onDisable() {
        wasHit = false;
        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
        disableValues();
        resetBlocks();
        decreaseTicks = 0;
        hypixelTicks = 0;
        if(mode.is("Vulcan")){

            //deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
            if(oldPos != null){
                Wrapper.instance.log((Math.round(dist * 100f) / 100f) + " blocks traveled");
            }
        }

        switch(mode.getMode()){

            case "Test": {
                switch (testMode.getMode()) {
                    case "Test 3": {
                        MoveUtils.strafe(MoveUtils.getMotion() * 0.2f);
                        break;
                    }
                }
                break;
            }
            case "Funcraft":{
                mc.thePlayer.motionX *= 0.25f;
                mc.thePlayer.motionZ *= 0.25f;
                break;
            }
        }
    }

    private int bowSlot() {
        return mc.thePlayer.getSlotByItem(Items.bow);
    }

    public void resumeWalk() {
        mc.gameSettings.keyBindForward.pressed = isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
    }

    public void stopWalk() {
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
    }


    public float getDirection() {
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float forward = Minecraft.getMinecraft().thePlayer.moveForward;
        float strafe = Minecraft.getMinecraft().thePlayer.moveStrafing;
        yaw += (float)(forward < 0.0f ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += (float)(forward < 0.0f ? -45 : (forward == 0.0f ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= (float)(forward < 0.0f ? -45 : (forward == 0.0f ? 90 : 45));
        }
        return yaw * ((float)Math.PI / 180);
    }

    public void resetBlocks(){
        if(!blockPosList.isEmpty()){

            for(BlockPos bp : blockPosList){
                mc.theWorld.setBlockToAir(bp);
            }
            blockPosList.clear();
        }
        if(oldPos != null){
            mc.theWorld.setBlockToAir(oldPos);
        }
        removeBlock = false;
        blockPosList.clear();
        removeBlock = false;
    }

    public void disableValues(){
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.capabilities.isCreativeMode = false;
        mc.thePlayer.capabilities.allowFlying = false;
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.speedInAir = 0.02f;
        mc.thePlayer.stepHeight = 0.6f;
        /*mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;*/
    }

}
