package uwu.flauxy.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
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
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.NumberUtil;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
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

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Vulcant", "Collision", "Test", "Funcraft");
    public NumberSetting Collisionspeed = new NumberSetting("Speed", 0.4, 0.1, 3, 0.05).setCanShow(m -> mode.is("Collision"));
    public NumberSetting Collisiontimer = new NumberSetting("Timer", 1, 0.1, 10, 0.025).setCanShow(m -> mode.is("Collision"));
    public BooleanSetting CollisionspoofY = new BooleanSetting("Spoof Y", true).setCanShow(m -> mode.is("Collision"));
    public BooleanSetting Collisiondamage = new BooleanSetting("Damage", true).setCanShow(m -> mode.is("Collision"));
    public ModeSetting testMode = new ModeSetting("Test Mode", "Test 1", "Test 1", "Test 2", "Test 3").setCanShow(m -> mode.is("Test"));
    public NumberSetting speed = new NumberSetting("Speed", 1, 0.05, 5, 0.05).setCanShow(m -> mode.is("Vanilla"));

    public NumberSetting funcraftSpeed = new NumberSetting("Speed", 0.8, 0.05, 2, 0.05).setCanShow(m  -> mode.is("Funcraft"));


    BlockPos oldPos;
    List<BlockPos> blockPosList = new ArrayList<>();

    boolean removeBlock = false;
    int flyTicks = 0;

    public Fly() {
        addSettings(mode, Collisionspeed, Collisiontimer, CollisionspoofY, Collisiondamage, testMode, funcraftSpeed, speed);
    }
    private double flySpeed = 0f;
    
    public void onEvent(Event e) {
        switch(mode.getMode()){

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
                if(e instanceof EventMove){
                    EventMove em = (EventMove)e;
                    if(flyTicks >= 2){
                        em.setY(mc.thePlayer.motionY = -1e-14);
                    }
                }
                if(e instanceof EventUpdate){
                    double xDist = mc.thePlayer.prevPosX - mc.thePlayer.posX;
                    double zDist = mc.thePlayer.prevPosZ - mc.thePlayer.posZ;
                    double lastDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);

                    if(mc.thePlayer.isCollidedVertically){
                        mc.timer.timerSpeed = 1.4f;
                        flyTicks = 0;
                        //mc.thePlayer.motionY = 0.42F;
                        mc.thePlayer.motionY = 0.42F;
                        MoveUtils.strafe(0.49);
                        flySpeed = (float) funcraftSpeed.getValue();
                    }else{
                        if(mc.timer.timerSpeed > 1.7){
                            mc.timer.timerSpeed -= 0.08;
                        }
                        mc.thePlayer.jumpMovementFactor = 0F;
                        switch (flyTicks) {
                            case 0:
                                MoveUtils.strafe(flySpeed = (float) funcraftSpeed.getValue());
                                break;
                        }
                        if(flyTicks != 0){
                            flySpeed = lastDist - lastDist / 159;
                            MoveUtils.strafe(flySpeed);
                            if (flySpeed < 0.29) MoveUtils.strafe(0.29f);
                        }
                        flyTicks++;
                    }
                }
                break;
            }
            case "Test":{
                switch(testMode.getMode()){
                    case "Test 3":{
                        if(e instanceof EventMotion){
                            mc.timer.timerSpeed = 1.0f;
                            EventMotion em = (EventMotion) e;
                            mc.thePlayer.motionY = 0;
                            if(mc.thePlayer.isMoving()){
                                MoveUtils.strafe(flySpeed);
                                flySpeed = flySpeed > 0.23 ? flySpeed - (flySpeed / 159) : flySpeed;
                            }else{
                                mc.thePlayer.motionX = 0;
                                mc.thePlayer.motionZ = 0;
                            }
                            switch(flyTicks){
                                case 1:{
                                    if(!mc.thePlayer.onGround) this.toggle();
                                    mc.thePlayer.jump();
                                    flySpeed = 1.91f;
                                    MoveUtils.damage(MoveUtils.Bypass.VANILLA);
                                    break;
                                }
                            }
                            flyTicks++;
                        }
                        if(e instanceof EventUpdate){

                        }

                        break;
                    }
                    case "Test 2":{
                        /*if(e instanceof EventSendPacket){
                            int maxTick = Integer.MAX_VALUE; // Integer.MAX_VALUE for infinite blink
                            int tickDelay = 20;
                            EventSendPacket eventSendPacket = (EventSendPacket) e;
                            if(PacketUtil.isPacketBlinkPacket(eventSendPacket.getPacket()) && flyTicks >= 0 && flyTicks < maxTick){
                                packetsLinked.add(eventSendPacket.getPacket());
                                eventSendPacket.setCancelled(true);
                            }
                            if(flyTicks % tickDelay == 0 && flyTicks < maxTick){
                                for(int i = 0; i < packetsLinked.size() - 1; i++){
                                    PacketUtil.packetNoEvent(packetsLinked.get(i));
                                }
                                packetsLinked.clear();
                            }
                        }*/

                        PacketUtil.blink(packetsLinked, e, flyTicks, 4, Integer.MAX_VALUE);
                        if(e instanceof EventMotion){
                            mc.timer.timerSpeed = 0.7f;
                            EventMotion em = (EventMotion) e;
                            mc.thePlayer.motionY = -0.125f;
                            if(mc.thePlayer.isMoving()){
                                MoveUtils.strafe(speed.getValue());
                            }else{
                                mc.thePlayer.motionX = 0;
                                mc.thePlayer.motionZ = 0;
                            }
                            switch(flyTicks){
                                case 4:{
                                    //MoveUtils.damage(MoveUtils.Bypass.VERUS);
                                    break;
                                }
                            }
                            flyTicks++;
                        }
                        // zonecraft
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
                    MoveUtils.setSpeed(Collisionspeed.getValue());
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
        tempY = 0;
        flyTicks = 0;
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
