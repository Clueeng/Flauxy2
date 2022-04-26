package uwu.flauxy.module.impl.movement;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.WorldUtil;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Speed", displayName = "Speed", key = Keyboard.KEY_X, cat = Category.Movement)
public class Speed extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Hypixel");
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Hop", "Hop", "Low", "Float", "Damage", "Ground").setCanShow(m -> mode.is("Verus"));
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.025).setCanShow(m -> (mode.is("Vanilla") || (mode.is("Verus") && verusMode.is("Damage"))));
    NumberSetting speedLow = new NumberSetting("Speed", 1, 0, 5, 0.1).setCanShow(m -> ((mode.is("Verus") && verusMode.is("Low"))));


    public Speed(){
        addSettings(mode, verusMode, speedLow, speed);
    }

    public boolean state;
    public double moveSpeed;
    private int ticks = 0;

    @Override
    public void onDisable() {
        ticks = 0;
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @Override
    public void onEvent(Event ev){

        //if(!this.isToggled()) return;
        if(ev instanceof EventMove){
            if(WorldUtil.shouldNotRun()){
                return;
            }
            EventMove em = (EventMove)ev;
            this.setDisplayName("Speed §f" + mode.getMode());
            switch(mode.getMode()){
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Ground":{
                            if(!mc.thePlayer.onGround) return;

                            MoveUtils.strafe(0.425);
                            if(mc.thePlayer.ticksExisted % 6 == 0){
                                MoveUtils.strafe(-0.119f);
                            }
                            break;
                        }
                        case "Hop":{
                            if(mc.thePlayer.onGround){

                                MoveUtils.strafe(0.69);
                                em.setY(mc.thePlayer.motionY = 0.42f);
                            }else{
                                float speed = 0.415f;
                                if(mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed) speed = 0.34f;
                                if(mc.gameSettings.keyBindBack.pressed) speed = 0.34f;

                                MoveUtils.strafe(speed);
                            }
                            break;
                        }
                        case "Low":{
                            if(!mc.thePlayer.isMoving()) return;
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.jump();
                                MoveUtils.strafe(0.70 + (speedLow.getValue() / 10));
                                mc.thePlayer.motionY = 0;
                                em.setY(0.42F);
                            }
                            MoveUtils.strafe(0.41 + (speedLow.getValue() / 40));
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if(ev instanceof EventMotion){
            EventMotion em =(EventMotion)ev;
            switch(mode.getMode()){
                case "Hypixel":{
                    if(mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                        mc.thePlayer.motionY = 0.42F;
                        moveSpeed = MoveUtils.getBaseSpeed() + 0.002;
                    } else {
                        moveSpeed = MoveUtils.getSpeedMotion() + 0.002;
                    }
                    MoveUtils.strafe(moveSpeed);



                break;
                }
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Float":{
                            if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                                if (mc.thePlayer.isCollidedVertically) {
                                    em.setOnGround(true);
                                    MoveUtils.strafe(0.38f);
                                    mc.thePlayer.motionY = 0.42F;
                                    mc.timer.timerSpeed = 1.0f;

                                } else {
                                    mc.thePlayer.motionY = ticks % 10 == 0 ? -0.42f : 0;
                                    MoveUtils.strafe(0.39f);
                                }
                                ticks++;
                            }
                            break;
                        }
                    }
                    break;
                }
                case "Vanilla":{
                    MoveUtils.strafe(speed.getValue());
                    if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                        mc.thePlayer.jump();
                    }
                    if (!mc.thePlayer.isMoving()) {
                        MoveUtils.motionreset();
                    }
                    break;
                }
            }
        }
    }

}
