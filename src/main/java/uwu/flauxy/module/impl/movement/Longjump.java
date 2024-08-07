package uwu.flauxy.module.impl.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.player.Nofall;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

import java.util.LinkedList;

@ModuleInfo(name = "Longjump", displayName = "Longjump", key = Keyboard.KEY_G, cat = Category.Movement)
public class Longjump extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Funcraft", "Redesky", "BlocksMC", "Test");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1).setCanShow((m) -> mode.is("Verus"));

    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "Simple", "Normal").setCanShow((m) -> mode.is("Verus"));
    public ModeSetting redeMode = new ModeSetting("Redesky Mode", "Normal", "Normal", "Advanced", "Custom").setCanShow((m) -> mode.is("Redesky"));
    public BooleanSetting redePacketCancel = new BooleanSetting("Cancel some packets", true).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeCustomSpeedInAir = new NumberSetting("Speed in air", 0.02, 0.02, 0.04, 0.0025).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeCustomJumpMovement = new NumberSetting("Jump Move Factor", 0.02, 0.02, 0.04, 0.0025).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeMotionY = new NumberSetting("Motion Y", 1.0, 0.42, 2.4, 0.025).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeTimer = new NumberSetting("Timer", 1.0, 0.1, 1.2, 0.1).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeMotionMult = new NumberSetting("Motion Multiplier", 0.25, 0.2, 0.4, 0.1).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public BooleanSetting redeSlowingDown = new BooleanSetting("Slow Down", true).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeSlowDownFactor = new NumberSetting("Slow Down Factor", 0.94, 0.25, 0.99, 0.01).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeSlowingDown.getValue());

    public BooleanSetting redeBigJump = new BooleanSetting("Jump for longer", true).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeBigJumpMotion = new NumberSetting("Longer Jump Motion", 0.05, 0.01, 0.20, 0.05).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeBigJump.getValue());
    public NumberSetting redeBigJumpLength = new NumberSetting("Big jump Length", 1, 0, 4, 0.25).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeBigJump.getValue());
    public BooleanSetting redeFlagFall = new BooleanSetting("Flag Fall", true).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));
    public NumberSetting redeFlagFallDistance = new NumberSetting("Fall Distance", 1.5, 0.5, 2, 0.5).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeFlagFall.getValue());
    public NumberSetting redeFlagFallMotion = new NumberSetting("Fall Motion Y", 0.42, 0.05, 0.8, 0.05).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeFlagFall.getValue());
    public NumberSetting redeFlagFallJumpMove = new NumberSetting("Fall Move Factor", 0.02, 0.01, 0.04, 0.01).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeFlagFall.getValue());
    public NumberSetting redeFlagSpeedInAir = new NumberSetting("Fall Air Speed", 0.02, 0.01, 0.04, 0.01).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeFlagFall.getValue());
    public BooleanSetting groundFlag = new BooleanSetting("Ground Flag Fall", true).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom") && redeFlagFall.getValue());
    public BooleanSetting keepSpeed = new BooleanSetting("Keep Speed", true).setCanShow(m -> mode.is("Redesky") && redeMode.is("Custom"));

    boolean thingmotion = false;

    public boolean shouldWait;
    public int seconds = 0;
    public Timer timerWait = new Timer();
    double firstypos;
    LinkedList<Packet> packetsLinked = new LinkedList<>();

    public Longjump(){
        addSettings(mode, verusMode, speed, redeMode, redePacketCancel, redeCustomSpeedInAir, redeCustomJumpMovement, redeMotionY, redeTimer, redeMotionMult, redeSlowingDown, redeSlowDownFactor, redeBigJump,
                redeBigJumpMotion, redeBigJumpLength, redeFlagFall, redeFlagFallDistance, redeFlagFallJumpMove, redeFlagSpeedInAir, redeFlagFallMotion, groundFlag, keepSpeed

        );
    }
    boolean shouldBlink;

    int ticks = 0;
    public void onEnable() {
        thingmotion = true;
        ticks = 0;
        stage = 2;
        switch(mode.getMode()){
            case "Redesky":{
                if(!mc.thePlayer.onGround) this.toggle();
                switch(redeMode.getMode()){
                    case "Advanced":{
                        if(seconds > 0){
                            this.setToggled(false);
                        }else{
                            timerWait.reset();
                            seconds = 10;
                            shouldWait = true;
                        }
                        break;
                    }
                }
                break;
            }
            case "Verus":{
                ticks = 0;
                switch(verusMode.getMode()){
                    case "Damage":
                        MoveUtils.damage(MoveUtils.Bypass.VERUS);
                        break;
                    case "Normal":
                        firstypos = mc.thePlayer.posY;
                        MoveUtils.strafe(0.334f);
                        mc.thePlayer.jump();
                    case "Simple":{
                        MoveUtils.damage(MoveUtils.Bypass.VERUS);
                    }
                }
            }
            case "Test":{
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                break;
            }
        }
    }
    public void onDisable() {
        ticks = 0;
        mc.timer.timerSpeed = 1.0f;
        if(!mode.getMode().contains("Redesky") && mode.getMode() != "BlocksMC"){
            MoveUtils.motionreset();
        }
        mc.thePlayer.speedInAir = 0.02f;
        mc.thePlayer.jumpMovementFactor = 0.02f;
        switch(mode.getMode()){
            case "Verus":{
                switch(verusMode.getMode()){
                    case "Normal":{
                        MoveUtils.strafe(0.42f);
                        break;
                    }
                }
                break;
            }
        }
    }
    float speedFC = 0f;
    @Override
    public void onEvent(Event ev){
        switch(mode.getMode()){
            case "Test":{
                ticks++;
                mc.timer.timerSpeed = .7f;
                if(ticks < 5){
                    mc.thePlayer.motionY += .8f;
                }else{
                    this.toggle();
                }
                break;
            }

            case "BlocksMC":{

                if(ev instanceof EventMotion){

                    if(mc.thePlayer.onGround){

                        mc.thePlayer.motionY += 0.32f;
                    }else{
                        stage++;
                        if(stage > 1) MoveUtils.strafe(MoveUtils.getMotion() * 1.08f);
                        switch(stage){
                            case 1:{
                                mc.thePlayer.motionY += 0.05f;
                                break;
                            }
                            case 2:{
                                break;
                            }
                            case 3:{
                                mc.timer.timerSpeed = 0.9f;
                                break;
                            }
                        }
                        if(stage >= 3){
                            MoveUtils.strafe(MoveUtils.getMotion() * 1.04f);
                        }
                        if(stage == 10){
                            this.toggle();
                        }
                    }
                }

                break;
            }

            case "Redesky":{
                switch(redeMode.getMode()){
                    case "Normal":{
                        if(ev instanceof EventSendPacket){
                            EventSendPacket e = (EventSendPacket) ev;
                            if(e.getPacket() instanceof S12PacketEntityVelocity){
                                e.setCancelled(true);
                            }
                        }
                        if(ev instanceof EventMotion){
                            EventMotion e = (EventMotion) ev;
                            mc.timer.timerSpeed = (float) timer >= 1 ? timer : 1;
                            if(mc.thePlayer.onGround){
                                if(stage >= 5){
                                    this.toggle();
                                    mc.thePlayer.motionY = 0;
                                }else{
                                    mc.thePlayer.motionY = stage <= 2 ? 0.42f : 0.78f;
                                }
                                onGroundTicks++;
                                offGroundTicks = 0;
                                timer = 2;
                                stage++;
                            }else{
                                if(stage >= 2){
                                    if(mc.thePlayer.motionY < 0.78){
                                /*mc.thePlayer.motionX *= 0.99;
                                mc.thePlayer.motionZ *= 0.99;*/

                                        float f = mc.thePlayer.rotationYaw * (0.017453292F * 6);
                                        mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.2F);
                                        mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.2F);
                                        mc.thePlayer.speedInAir = 0.03f;

                                    }
                                }
                                if(stage <= 2){
                                    if(mc.thePlayer.motionY < 0.2){
                                        //mc.thePlayer.motionY -= 0.04f;
                                    }
                                }
                                onGroundTicks = 0;
                                offGroundTicks++;
                                if(offGroundTicks == 1 && stage < 3){
                                    MoveUtils.strafe(MoveUtils.getMotion() * 1.04);
                                }else{
                                    MoveUtils.strafe(Math.min(MoveUtils.getMotion() * 0.97, MoveUtils.getMotion() * 1.02));
                                }
                                if(timer > 1.1){
                                    timer-=0.1;
                                }

                                if(mc.thePlayer.speedInAir >= 0.02) mc.thePlayer.speedInAir -= 0.01;
                                if(mc.thePlayer.speedInAir < 0.02) mc.thePlayer.speedInAir = 0.02f;
                            }
                        }
                        break;
                    }

                    case "Custom":{
                        if(ev instanceof EventSendPacket){
                            EventSendPacket e = (EventSendPacket) ev;
                            if(redePacketCancel.getValue()){
                                if(e.getPacket() instanceof S12PacketEntityVelocity){
                                    e.setCancelled(true);
                                }
                                if(e.getPacket() instanceof S08PacketPlayerPosLook){
                                    e.setCancelled(true);
                                }
                            }
                        }
                        if(ev instanceof EventMotion){
                            EventMotion e = (EventMotion) ev;
                            mc.timer.timerSpeed = (float) redeTimer.getValue();
                            if(mc.thePlayer.onGround){
                                if(stage >= 5){
                                    this.toggle();
                                    mc.thePlayer.motionY = 0;
                                }else{
                                    mc.thePlayer.motionY = stage <= 2 ? 0.42f : redeMotionY.getValue();
                                }
                                onGroundTicks++;
                                offGroundTicks = 0;
                                stage++;
                            }else{
                                if(redeFlagFall.getValue()){
                                    if(!keepSpeed.getValue()){
                                        mc.thePlayer.jumpMovementFactor = (float) redeFlagFallJumpMove.getValue();
                                        mc.thePlayer.speedInAir = (float) redeFlagSpeedInAir.getValue();
                                    }
                                    if(mc.thePlayer.fallDistance2 > redeFlagFallDistance.getValue() ){
                                        mc.thePlayer.motionY += redeFlagFallMotion.getValue();
                                        mc.thePlayer.fallDistance2 = 0;

                                    }
                                    Nofall nofall = Flauxy.INSTANCE.getModuleManager().getModule(Nofall.class);
                                    if(!nofall.isToggled()){
                                        if(groundFlag.getValue()){
                                            if(mc.thePlayer.fallDistance > 2){
                                                e.setOnGround(true);
                                                mc.thePlayer.fallDistance = 0;
                                            }
                                        }
                                    }
                                }
                                if(redeBigJump.getValue()){
                                    if(thingmotion){
                                        Wrapper.instance.log("" + mc.thePlayer.motionY);
                                        mc.thePlayer.motionY += redeBigJumpMotion.getValue();
                                    }
                                    if(mc.thePlayer.motionY >= (redeMotionY.getValue() - (redeMotionY.getValue()) / 2) + redeBigJumpMotion.getValue() + (redeBigJumpLength.getValue() / 10)){
                                        thingmotion = false;
                                    }
                                }
                                mc.thePlayer.jumpMovementFactor = (float) redeCustomJumpMovement.getValue();
                                if(stage >= 2){
                                    if(mc.thePlayer.motionY < redeMotionY.getValue() / 1.5f){
                                        float f = mc.thePlayer.rotationYaw * (0.017453292F * 6);
                                        if(!keepSpeed.getValue()){
                                            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * redeMotionMult.getValue()); // 0.2 - 0.4
                                            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * redeMotionMult.getValue());
                                            mc.thePlayer.speedInAir = (float) redeCustomSpeedInAir.getValue();
                                        }
                                    }
                                }
                                onGroundTicks = 0;
                                offGroundTicks++;
                                if(!keepSpeed.getValue()){
                                    if(offGroundTicks == 1 && stage < 3){
                                        MoveUtils.setSpeed(MoveUtils.getMotion() * 1.04, mc.thePlayer.rotationYaw, 0, 1);
                                    }else{
                                        MoveUtils.setSpeed(Math.min(MoveUtils.getMotion() * 0.97, MoveUtils.getMotion() * 1.02), mc.thePlayer.rotationYaw, 0, 1);
                                    }
                                }
                                // slow down
                                /*if(redeSlowingDown.getValue()){
                                    if(mc.thePlayer.fallDistance > 2.5f){
                                        Wrapper.instance.log("a");
                                        mc.thePlayer.motionX *= redeSlowDownFactor.getValue();
                                        mc.thePlayer.motionZ *= redeSlowDownFactor.getValue();
                                    }else{

                                    }
                                }*/
                                if(mc.thePlayer.speedInAir >= 0.02) mc.thePlayer.speedInAir -= 0.01;
                                if(mc.thePlayer.speedInAir < 0.02) mc.thePlayer.speedInAir = 0.02f;
                            }
                        }
                        break;
                    }
                    case "Advanced":{
                        if(ev instanceof EventSendPacket){
                            EventSendPacket e = (EventSendPacket) ev;
                            if(e.getPacket() instanceof S12PacketEntityVelocity){
                                e.setCancelled(true);
                            }
                            if(e.getPacket() instanceof S08PacketPlayerPosLook){
                                e.setCancelled(true);
                            }
                        }
                        if(ev instanceof EventMotion){
                            EventMotion e = (EventMotion) ev;
                            mc.timer.timerSpeed = 0.8f;
                            if(mc.thePlayer.onGround){
                                if(stage >= 5){
                                    this.toggle();
                                    mc.thePlayer.motionY = 0;
                                }else{
                                    mc.thePlayer.motionY = stage <= 2 ? 0.42f : 1.01f;
                                }
                                onGroundTicks++;
                                offGroundTicks = 0;
                                stage++;
                            }else{
                                if(stage >= 2){
                                    if(mc.thePlayer.motionY < 0.78){
                                        float f = mc.thePlayer.rotationYaw * (0.017453292F * 6);
                                        mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.25F);
                                        mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.25F);
                                        mc.thePlayer.speedInAir = 0.03f;
                                    }
                                }
                                onGroundTicks = 0;
                                offGroundTicks++;
                                if(offGroundTicks == 1 && stage < 3){
                                    MoveUtils.strafe(MoveUtils.getMotion() * 1.04);
                                }else{
                                    MoveUtils.strafe(Math.min(MoveUtils.getMotion() * 0.97, MoveUtils.getMotion() * 1.02));
                                }
                                if(mc.thePlayer.speedInAir >= 0.02) mc.thePlayer.speedInAir -= 0.01;
                                if(mc.thePlayer.speedInAir < 0.02) mc.thePlayer.speedInAir = 0.02f;
                            }
                        }
                        break;
                    }
                }

                break;
            }
        }
        if(ev instanceof EventUpdate){
            if(mode.is("Funcraft")){

            }
        }
        //if(!this.isToggled()) return;
        if(ev instanceof EventMotion){
            switch(mode.getMode()){

                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Damage":{
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42f;
                            }
                            int tik = 15;
                            if(ticks < tik){
                                mc.timer.timerSpeed = 0.8f;
                                MoveUtils.strafe(speed.getValue());
                                mc.thePlayer.motionY = 0.1;
                            }else{
                                if(ticks < tik+1){
                                    MoveUtils.strafe(0.15f);
                                }
                                if(((Math.round(mc.thePlayer.posY) * 100) / 100) == (int)mc.thePlayer.posY && mc.thePlayer.fallDistance > 0.95){
                                    MoveUtils.strafe(0.27f);
                                    mc.thePlayer.motionY = 0.42f;
                                    mc.thePlayer.fallDistance = 0;
                                }
                            }
                            ticks++;
                            break;
                        }
                        case "Simple":{
                            mc.thePlayer.motionY = 0.10;
                            MoveUtils.strafe(speed.getValue() * 2);
                            break;
                        }
                        case "Normal":{
                            if(mc.thePlayer.ticksExisted % 25 == 0) {
                                mc.thePlayer.motionY = 0.25;
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                mc.thePlayer.setPosition(mc.thePlayer.posX, firstypos, mc.thePlayer.posZ);
                                MoveUtils.damage(MoveUtils.Bypass.VERUS);
                                MoveUtils.strafe(4);

                            }
                            if(mc.thePlayer.ticksExisted % 4 == 0){
                                mc.thePlayer.motionY = 0.42F;
                                MoveUtils.strafe(0.34f);
                            }else{
                                MoveUtils.strafe(0.12f);
                            }
                            ticks++;
                            break;
                        }
                        case "Highjump":{
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.motionY += 0.21*2;
                            }
                            MoveUtils.strafe();
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean lagbacked;
    double speedV;
    float timer;
    int offGroundTicks;
    int onGroundTicks;
    int stage;

    private void Redesky(Event event) {

    }

}
