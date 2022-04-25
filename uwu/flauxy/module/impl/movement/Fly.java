package uwu.flauxy.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.WorldUtil;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Funcraft", "Vanilla");
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "Normal", "BlocksMC").setCanShow((m) -> mode.is("Verus"));
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1).setCanShow((m) -> (mode.is("Vanilla") || (mode.is("Verus") && verusMode.is("BlocksMC"))));
    BooleanSetting motionReset = new BooleanSetting("Motion Reset", true);

    int ticks = 0;
    int stage =0;
    float speedFC = 0f;
    boolean wasOnAir;
    double posYLOL = 0;

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
    }
    public static double roundToOnGround(final double posY) {
        return posY - (posY % 0.015625);
    }
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            EventMotion event = (EventMotion) e;
            if(WorldUtil.shouldNotRun()) return;
            switch (mode.getMode()) {

                case "Verus": {
                    switch (verusMode.getMode()) {
                        case "Normal": {


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
