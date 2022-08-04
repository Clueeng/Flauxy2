package uwu.flauxy.module.impl.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;

import java.util.LinkedList;

@ModuleInfo(name = "Longjump", displayName = "Longjump", key = Keyboard.KEY_G, cat = Category.Movement)
public class Longjump extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Funcraft", "Redesky", "BlocksMC");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1).setCanShow((m) -> mode.is("Verus"));

    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "Simple", "Normal").setCanShow((m) -> mode.is("Verus"));
    public ModeSetting redeMode = new ModeSetting("Redesky Mode", "Normal", "Normal", "Advanced", "Packet").setCanShow((m) -> mode.is("Redesky"));

    double firstypos;
    LinkedList<Packet> packetsLinked = new LinkedList<>();

    public Longjump(){
        addSettings(mode, verusMode, speed, redeMode);
    }
    boolean shouldBlink;

    int ticks = 0;
    public void onEnable() {
        stage = 2;
        switch(mode.getMode()){
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
        }
    }
    public void onDisable() {
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
                                e.setCancelled(mc.thePlayer.ticksExisted % 2 == 0);
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
                                    mc.thePlayer.motionY = stage <= 2 ? 0.42f : 0.72f;
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
                    case "Advanced":{
                        if(ev instanceof EventUpdate) {
                            if(mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                            } else {
                                mc.thePlayer.jumpMovementFactor = 0.125F;
                            }
                        } else if(ev instanceof EventReceivePacket) {
                            EventReceivePacket e = (EventReceivePacket) ev;

                            if(e.getPacket() instanceof S12PacketEntityVelocity) {
                                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                                packet.setMotionY(packet.getMotionY() * 3);
                                if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                                    //e.setCancelled(true);
                                }
                            } else if(e.getPacket() instanceof S27PacketExplosion) {
                                e.setCancelled(true);
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
                                mc.thePlayer.jump();
                            }
                            int tik = 15;
                            if(ticks < tik){
                                MoveUtils.strafe(speed.getValue());
                                mc.thePlayer.motionY = 0.1;
                            }else{
                                if(ticks < tik+1){
                                    MoveUtils.strafe(0.15f);
                                }
                                if(((Math.round(mc.thePlayer.posY) * 100) / 100) == (int)mc.thePlayer.posY && mc.thePlayer.fallDistance > 0.75){
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
