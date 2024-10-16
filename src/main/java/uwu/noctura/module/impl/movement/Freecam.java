package uwu.noctura.module.impl.movement;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.MoveUtils;

import java.awt.*;

@ModuleInfo( name = "Freecam", key = -1, cat = Category.Movement, displayName = "Free Cam")
public class Freecam extends Module {

    public ModeSetting mode = new ModeSetting("Mode","Bad","Bad", "Good");
    public BooleanSetting cancelBlock = new BooleanSetting("Cancel Place", true).setCanShow(m -> mode.is("Bad"));
    public BooleanSetting cancelMove = new BooleanSetting("Cancel Move", true).setCanShow(m -> mode.is("Bad"));
    public BooleanSetting cancelAttack = new BooleanSetting("Cancel Use Entity", true).setCanShow(m -> mode.is("Bad"));
    public NumberSetting freecamSpeed = new NumberSetting("Speed",1.0,0.25,1.5d,0.125);

    public Freecam(){
        addSettings(mode, cancelAttack, cancelBlock, cancelMove, freecamSpeed);
    }
    public double[] initPos = new double[3];
    public float[] initAngle = new float[2];
    boolean initGround = false;
    double initMotionX, initMotionY, initMotionZ;
    public static boolean isGhosting;

    EntityOtherPlayerMP playerCopy;

    @Override
    public void onDisable() {

        if(mc.thePlayer == null || mc.theWorld == null){
            return;
        }

        if(mode.is("Bad")){
            mc.thePlayer.onGround = initGround;
            mc.thePlayer.setLocationAndAngles(initPos[0], initPos[1], initPos[2], initAngle[0], initAngle[1]);
        }
        MoveUtils.stopMoving();
        mc.thePlayer.motionZ = initMotionZ;
        mc.thePlayer.motionX = initMotionX;
        mc.thePlayer.motionY = initMotionY;

        mc.theWorld.removeEntityFromWorld(-420);
        playerCopy = null;
    }

    @Override
    public void onEvent(Event e) {
        switch (mode.getMode()){
            case "Bad":{
                bad(e);
                break;
            }
            case "Good":{
                good(e);
                break;
            }
        }
    }

    public void good(Event e){
        if(e instanceof EventUpdate){
            MoveUtils.cancelMoveInputs();
            mc.thePlayer.posX += Math.sin(mc.thePlayer.rotationYaw) * 1;
            mc.thePlayer.posZ -= Math.cos(mc.thePlayer.rotationYaw) * 1;

        }
    }

    public void bad(Event e){
        if(mc.thePlayer == null ) {
            onDisable();
            this.toggle();
            return;
        }
        if(mc.thePlayer.ticksExisted < 20) {
            onDisable();
            this.toggle();
            return;
        }
        if(e instanceof EventSendPacket){
            EventSendPacket event = (EventSendPacket) e;
            Packet packet = event.getPacket();
            if((packet instanceof C00Handshake || packet instanceof C00PacketServerQuery
                    || packet instanceof C01PacketPing)){
                event.setCancelled(true);
            }
            if(cancelMove.isEnabled()){
                if(packet instanceof C03PacketPlayer) event.cancel();
            }
            if(cancelBlock.isEnabled()){
                if(packet instanceof C08PacketPlayerBlockPlacement) event.cancel();
            }
            if(cancelAttack.isEnabled()){
                if(packet instanceof C02PacketUseEntity) event.cancel();
            }
        }
        if(e instanceof EventRender2D){
            EventRender2D event = (EventRender2D) e;
            isGhosting = mc.thePlayer.getDistanceToEntity(playerCopy) > 6.0d;
            if(isGhosting){
                ScaledResolution sr = new ScaledResolution(mc);
                String line1 = "Placing blocks further than 6 blocks away from";
                String line2 = "your server position will result in ghost blocks";
                mc.fontRendererObj.drawStringWithShadow(line1,
                        sr.getScaledWidth() / 2f - (mc.fontRendererObj.getStringWidth(line1)/2f), 12, Color.red.getRGB());
                mc.fontRendererObj.drawStringWithShadow(line2,
                        sr.getScaledWidth() / 2f - (mc.fontRendererObj.getStringWidth(line2)/2f), 24, Color.red.getRGB());
            }
        }
        if(e instanceof EventMotion){
            EventMotion event = (EventMotion) e;
            if(mc.thePlayer == null || mc.theWorld == null){
                this.toggle();
            }


            boolean upwards = mc.thePlayer.movementInput.jump;
            boolean downwards = mc.thePlayer.movementInput.sneak;
            mc.thePlayer.motionY = mc.thePlayer.isCollidedVertically ? -0.0784000015258789 : 0;
            if(upwards){
                mc.thePlayer.motionY += freecamSpeed.getValue();
            }
            if(downwards){
                mc.thePlayer.motionY -= freecamSpeed.getValue();
            }
            if(MoveUtils.isWalking()){
                MoveUtils.strafe(freecamSpeed.getValue());
            }else{
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            }
        }
    }

    @Override
    public void onEnable() {
        initGround = mc.thePlayer.onGround;
        initMotionX = mc.thePlayer.motionX;
        initMotionY = mc.thePlayer.motionY;
        initMotionZ = mc.thePlayer.motionZ;
        initPos[0] = mc.thePlayer.posX;
        initPos[1] = mc.thePlayer.posY;
        initPos[2] = mc.thePlayer.posZ;
        initAngle[0] = mc.thePlayer.rotationYaw;
        initAngle[1] = mc.thePlayer.rotationPitch;

        playerCopy = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        playerCopy.copyDataFromOld(mc.thePlayer);
        playerCopy.rotationYawHead = mc.thePlayer.rotationYawHead;
        playerCopy.rotationPitchHead = mc.thePlayer.rotationPitchHead;
        mc.theWorld.addEntityToWorld(-420,playerCopy);
    }
}
