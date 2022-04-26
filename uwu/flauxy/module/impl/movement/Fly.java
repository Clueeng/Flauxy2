package uwu.flauxy.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventUI;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.NumberUtil;
import uwu.flauxy.utils.WorldUtil;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_NONE, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Vanilla", "Hycraft", "Funcraft");
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "BlocksMC", "Collision").setCanShow((m) -> mode.is("Verus"));
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1).setCanShow((m) -> (mode.is("Vanilla") || mode.is("Funcraft") || (mode.is("Verus") && verusMode.is("BlocksMC"))));
    NumberSetting timer = new NumberSetting("Timer", 1.25, 1, 2.5, 0.05).setCanShow(m -> mode.is("Funcraft"));
    BooleanSetting motionReset = new BooleanSetting("Motion Reset", true);

    int ticks = 0;
    int stage =0;
    float speedFC = 0f;
    boolean wasOnAir;
    double posYLOL = 0;
    BlockPos oldPos;

    @Override
    public void onEnable() {
        wasOnAir = !mc.thePlayer.onGround;
        posYLOL = mc.thePlayer.posY;
        stage = 0;
        ticks = 0;
    }

    public Fly(){
        addSettings(mode);
        addSettings(verusMode);
        addSettings(speed);
        addSettings(timer);
    }
    boolean removeBlock = false;
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUI){
            if(!mode.is("Funcraft")){
                speed.setMaximum(6);
                speed.setIncrement(0.1);
                speed.setMinimum(0.4);
            }else{
                speed.setMaximum(1.8f);
                if(speed.getValue() > speed.getMaximum()){
                    speed.setValue(speed.getMaximum());
                }
                speed.setIncrement(0.05);
                speed.setMinimum(0.1);
            }
        }

        if(e instanceof EventMove){
            EventMove event = (EventMove) e;
            switch(mode.getMode()){
                case "Hycraft":{
                    //double number = 8.258 * 10;
                    double y = mc.thePlayer.posY * 10;
                    double randY = (double)Math.round(y) / 10;
                    break;
                }
            }
        }
        if (e instanceof EventMotion) {
            this.setDisplayName("Fly " + ChatFormatting.WHITE + mode.getMode());
            EventMotion event = (EventMotion) e;
            if(WorldUtil.shouldNotRun()) return;

            switch (mode.getMode()) {
                case "Funcraft":{
                    mc.timer.timerSpeed = (float) timer.getValue();
                    if(mc.thePlayer.isCollidedVertically){
                        if(mc.thePlayer.moveForward > 0.28) {
                            speedFC = (float) (MoveUtils.getBaseSpeed() + 0.9127 * speed.getValue());
                        } else {
                            speedFC = (float) (MoveUtils.getBaseSpeed() + 0.1 * speed.getValue());
                        }
                        mc.thePlayer.jump();
                    }else{
                        mc.thePlayer.onGround = false;
                        event.setOnGround(false);
                        mc.thePlayer.cameraYaw = 0.00f;
                        mc.thePlayer.setSprinting(true);
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E-10, mc.thePlayer.posZ);
                        mc.thePlayer.motionY = 0;
                        mc.thePlayer.jumpMovementFactor = 0;
                        if(speedFC > MoveUtils.getBaseSpeed() * 1){
                            speedFC -= speedFC / 159;
                        }else{
                            speedFC = (float) MoveUtils.getBaseSpeed();
                        }
                        if(mc.thePlayer.isMoving()){
                            MoveUtils.strafe(speedFC);
                        }
                    }
                    break;
                }
                case "Hycraft":{
                    break;
                }
                case "Verus": {
                    switch (verusMode.getMode()) {
                        case "Collision": {
                            if(ticks <= 1){
                                posYLOL = mc.thePlayer.posY;
                            }
                            if(mc.thePlayer.fallDistance > 0.8f){

                            }
                            ticks++;
                            break;
                        }
                        case "BlocksMC": {
                            if (wasOnAir) {
                                mc.timer.timerSpeed = 1.0f;
                                airjump();
                            } else {
                                if (ticks < 1) {
                                    MoveUtils.damage(MoveUtils.Bypass.VERUS);
                                    speedFC = (float) (speed.getValue() * 4);
                                }
                                if (ticks > 3) {
                                    if (ticks < 77) {
                                        MoveUtils.strafe(speedFC);
                                        if (speedFC >= 0.23) {
                                            speedFC -= 0.059;
                                        }
                                    }
                                }
                                if (ticks < 25) {
                                    if (speedFC > 0.23f) {
                                        speedFC -= 0.095f;
                                    }
                                    mc.thePlayer.motionY = -0.0785f;
                                    mc.timer.timerSpeed = 0.75f;
                                } else {
                                    mc.timer.timerSpeed = 1.0F;
                                    airjump();
                                }
                                if (ticks == 58) {
                                    Wrapper.instance.log(ticks + "");
                                    MoveUtils.strafe(0);
                                    speedFC = 0f;
                                }
                            }
                            ticks++;
                            break;
                        }
                    }
                    break;
                }

                case "Vanilla": {
                    mc.thePlayer.motionY = 0;
                    if(mc.gameSettings.keyBindJump.pressed) mc.thePlayer.motionY += 0.42f;
                    if(mc.gameSettings.keyBindSneak.pressed) mc.thePlayer.motionY -= 0.42f;
                    if(mc.thePlayer.isMoving()){
                        MoveUtils.strafe(speed.getValue() / 1.25f);
                    }else{
                        MoveUtils.stopMoving();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if(motionReset.getValue()){
            mc.thePlayer.motionX *= 0.1f;
            mc.thePlayer.motionZ *= 0.1f;
        }
        mc.timer.timerSpeed = 1.0F;
    }

    public void airjump(){
        if (mc.gameSettings.keyBindJump.pressed ? mc.thePlayer.ticksExisted % 2 == 0 : mc.thePlayer.fallDistance > 1.200f) {
            if(mc.gameSettings.keyBindJump.pressed){
                MoveUtils.strafe(0.21f);
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                mc.thePlayer.jump();
            }else{
                this.mc.thePlayer.addExhaustion(1.5f);
                MoveUtils.strafe(MoveUtils.getSpeed() * 1.31f);
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                mc.thePlayer.motionY = 0.42f;
                mc.thePlayer.fallDistance = 0f;
            }
        }
    }

}
