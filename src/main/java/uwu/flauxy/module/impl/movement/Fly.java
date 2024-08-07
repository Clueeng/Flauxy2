package uwu.flauxy.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.*;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.event.impl.packet.EventPacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.player.Blink;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.*;
import uwu.flauxy.utils.timer.Timer;
import uwu.flauxy.utils.timer.TimerUtil;

import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;
import static org.lwjgl.input.Keyboard.isKeyDown;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {
    public Timer  timer = new Timer();
    double tempY = 0;
    public ArrayList<Packet> packets = new ArrayList<>();
    private LinkedList<Packet> packetsLinked = new LinkedList<>();
    public ConcurrentLinkedQueue<Packet> blinkpackets = new ConcurrentLinkedQueue<>();

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Vulcant", "Collision", "Test", "Funcraft", "ClueAC");
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
        addSettings(mode, CollisionNotSpeed, Collisionspeed, Collisiontimer, CollisionspoofY, Collisiondamage, testMode, funcraftSpeed, speed);
    }
    private double flySpeed = 0f;
    private double oldYaw, oldPitch;

    public static boolean exemptWebs = false;
    
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){

            this.setDisplayName("Flight " + EnumChatFormatting.WHITE + mode.getMode());
        }
        switch(mode.getMode()){
            case "ClueAC": {
                if(e instanceof EventMotion){
                    if(flyTicks <= 1 && !mc.thePlayer.isCollidedVertically) this.toggle();
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.42f;
                    }else{
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.23214d, mc.thePlayer.posZ);
                    }
                    flyTicks++;
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
                    if (mode.is("Collision") && !mc.thePlayer.isSneaking()) {
                        if (mc.thePlayer.isSneaking())
                            return;
                        if (ec.getBlock() instanceof net.minecraft.block.BlockAir && ec.getPosY() < mc.thePlayer.posY)
                            ec.setBoundingBox(AxisAlignedBB.fromBounds(ec.getPosX(), ec.getPosY(), ec.getPosZ(), ec.getPosX() + 1.0D, mc.thePlayer.posY, ec.getPosZ() + 1.0D));
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


    @Override
    public void onEnable() {
        exemptWebs = false;
        tempY = 0;
        flyTicks = 0;
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


    @Override
    public void onDisable() {
        disableValues();
        resetBlocks();
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
        mc.thePlayer.stepHeight = 0.5f;
        /*mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;*/
    }

}
