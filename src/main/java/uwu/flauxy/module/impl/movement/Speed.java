package uwu.flauxy.module.impl.movement;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;

import java.util.ArrayList;
import java.util.Random;

@ModuleInfo(name = "Speed", displayName = "Speed", key = Keyboard.KEY_X, cat = Category.Movement)
public class Speed extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "NCP", "Test", "BlocksMC", "Redesky");
    public ModeSetting ncpMode = new ModeSetting("NCP Mode", "Funcraft", "Funcraft", "Funcraft Funny", "Hypixel Like").setCanShow(m -> mode.is("NCP"));
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Hop", "Hop", "Low", "Fast").setCanShow(m -> mode.is("Verus"));
    public ModeSetting bmcMode = new ModeSetting("BMC Mode", "Strafe", "Strafe", "Low", "No Strafe").setCanShow(m -> mode.is("BlocksMC"));
    public ModeSetting testMode = new ModeSetting("Test Mode", "Test 1", "Test 1", "Test 2", "Test 3").setCanShow(m -> mode.is("Test"));
    public NumberSetting speed = new NumberSetting("Speed", 0.6, 0.2, 2, 0.05).setCanShow(m -> mode.is("Vanilla"));
    double speedV;
    float timer;
    int offGroundTicks;
    int onGroundTicks;
    boolean prevOnGround;
    private int stage;
    public Speed(){
        addSettings(mode, ncpMode, testMode, verusMode, bmcMode, speed);
    }


    @Override
    public void onEnable() {
        stage = 0;
        switch (mode.getMode()){
            case "NCP":{
                speedV = MoveUtils.getBaseMoveSpeed();
                break;
            }
        }
    }

    public void onEvent(Event event){
        if(event instanceof EventUpdate){
            this.setDisplayName("Speed " + EnumChatFormatting.WHITE + mode.getMode());
        }
        switch(mode.getMode()){
            case "Redesky":{
                if(event instanceof EventMotion){
                    EventMotion e = (EventMotion) event;

                    if(mc.thePlayer.motionY > 0.20 || (mc.thePlayer.motionY < -0.3 && mc.thePlayer.fallDistance < 1.5f)){
                        mc.timer.timerSpeed += 0.019f;
                    }

                    if(mc.thePlayer.onGround){
                        mc.timer.timerSpeed = 1.0f;
                        mc.gameSettings.keyBindJump.pressed = true;
                    }else{
                        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
                    }
                }

                break;
            }

            case "Vanilla":{

                if(mc.thePlayer.onGround){
                    mc.thePlayer.jump();
                    MoveUtils.strafe(speed.getValue());
                }else{
                    MoveUtils.strafe(speed.getValue());
                }

                break;
            }

            case "BlocksMC":{
                switch(bmcMode.getMode()){
                    case "Low":{
                        if(event instanceof EventMotion){
                            EventMotion em = (EventMotion) event;
                            if(mc.gameSettings.keyBindRight.pressed){
                                em.setYaw(-180);
                            }
                            if(mc.gameSettings.keyBindLeft.pressed){
                                em.setYaw(mc.thePlayer.rotationYaw - 90);

                            }

                        }
                        if(event instanceof EventUpdate){
                            offGroundTicks = mc.thePlayer.onGround ? 0 : offGroundTicks++;
                            onGroundTicks = mc.thePlayer.onGround ? onGroundTicks++ : 0;
                            float timerAdd = 1f;
                            int maxStage = 0;
                            EventUpdate e = (EventUpdate) event;
                            if(mc.thePlayer.ticksExisted % 10 == 0){
                                stage = 0;
                            }
                            if(stage % 3 == 0){
                                MoveUtils.strafe();
                                if(mc.gameSettings.keyBindRight.pressed){

                                }
                                if(mc.gameSettings.keyBindLeft.pressed){

                                }
                            }
                            if(stage == 0){
                                mc.timer.timerSpeed = 1.0f;
                                stage = 1;
                            }
                            if(stage > 0 && stage <= maxStage){
                                mc.timer.timerSpeed += timerAdd;
                                stage++;
                            }
                            if(offGroundTicks == 1){
                                Wrapper.instance.log("hi");
                                mc.thePlayer.speedInAir = 0.14f;
                            }else if(offGroundTicks > 5){
                                mc.thePlayer.speedInAir = 0.02f;
                            }
                            if(mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 3 == 0){
                                mc.thePlayer.jump();
                            }
                        }
                        break;
                    }

                    case "No Strafe":{
                        if(event instanceof EventMotion){
                            EventMotion em = (EventMotion) event;
                            if(!mc.thePlayer.onGround){
                                                MoveUtils.strafe(MoveUtils.getMotion() / 1.109f);
                            }
                        }
                        if(event instanceof EventMove){
                            EventMove ev = (EventMove) event;
                            if(mc.thePlayer.onGround){
                                MoveUtils.strafe(0.4785);
                                ev.setY(mc.thePlayer.motionY = 0.42f);
                            }
                        }

                        break;
                    }
                }
                break;
            }
        }
        if(event instanceof EventMove){
            EventMove e = (EventMove) event;
            switch (mode.getMode()){

                case "Funcraft":
                    if (mc.thePlayer.onGround) {
                        e.setY(mc.thePlayer.motionY = 0.42F);
                        if (speedV < 0.2805) {
                            speedV = 0.2805;
                        }
                        speedV *= 2.149;
                        stage = 0;
                    } else if (stage == 0) {
                        MoveUtils.strafe(1094109f);
                    }




                    break;
                case "Test":{
                    switch(testMode.getMode()){

                        case "Test 3":{
                            if(!MoveUtils.isWalking()) return;
                            mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
                            if(mc.thePlayer.onGround){
                                e.setY(mc.thePlayer.motionY = 0.42f);
                                MoveUtils.strafe(MoveUtils.getSpeed() * (1 + mc.thePlayer.jumpMovementFactor));
                            }else{
                                if(mc.thePlayer.motionY > 0.2){
                                    e.setY(mc.thePlayer.motionY -= .25);
                                    e.setType(EventType.PRE);
                                    MoveUtils.strafe(MoveUtils.getSpeed() * (1 + mc.thePlayer.jumpMovementFactor + 0.05));
                                }
                            }
                            break;
                        }
                        case "Test 2":{
                            final String motion = String.valueOf(mc.thePlayer.motionY);
                            MoveUtils.strafe();
                            if(mc.thePlayer.onGround){
                                offGroundTicks = 0;
                                MoveUtils.strafe(1.3);
                                e.setY(mc.thePlayer.motionY = 0.42);
                            }else{
                                offGroundTicks++;
                                if(offGroundTicks == 1){
                                    Wrapper.instance.log("a");
                                    MoveUtils.strafe(MoveUtils.getSpeed() * 1.04f);
                                }
                                e.setY(mc.thePlayer.motionY -= 0.018f);
                                MoveUtils.strafe(MoveUtils.getSpeed() * 0.98f);
                            }
                            break;
                        }
                        case "Test 1":{
                            if(mc.thePlayer.onGround){
                                MoveUtils.strafe(e, 0.9 * (mc.thePlayer.motionX + mc.thePlayer.motionZ));
                            }
                            break;
                        }
                    }
                    break;
                }
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Low":{
                            if(!mc.thePlayer.isMoving()) return;
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.jump();
                                MoveUtils.strafe(0.70);
                                mc.thePlayer.motionY = 0;
                                e.setY(0.42F);
                            }
                            MoveUtils.strafe(0.41);
                            break;
                        }
                        case "Fast": {
                            if (!mc.thePlayer.isEating() && mc.thePlayer.isMoving() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
                                mc.thePlayer.setSprinting(true);
                            }
                            if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                                mc.thePlayer.speedOnGround = 0.17f;
                                mc.thePlayer.jump();
                                MoveUtils.setSpeed( 0.41f);
                            }
                            if (mc.thePlayer.isMoving() && !mc.thePlayer.onGround) {
                                if (mc.thePlayer.moveForward > 0) {
                                    MoveUtils.setSpeed(mc.thePlayer.hurtTime != 0 ? 0.53f : 0.35f);
                                } else {
                                    MoveUtils.setSpeed(0.362f);
                                }
                                mc.thePlayer.speedInAir = 0.041f;
                            }
                            break;
                        }
                    }
                    break;
                }

            }

        }
        if(event instanceof EventMove){
            EventMove e = (EventMove)event;
            switch(mode.getMode()){
                case "NCP":{
                    switch(ncpMode.getMode()){

                        case "Hypixel Like":{
                            mc.timer.timerSpeed = 1.0f;
                            if(mc.thePlayer.onGround) {
                                prevOnGround = true;
                                if(MoveUtils.isWalking()) {
                                    e.setY(mc.thePlayer.motionY = 0.41999998688698);
                                    //mc.thePlayer.motionY = 0.42;
                                    // 2.13
                                    speedV *= 2.34;
                                }
                            } else if(prevOnGround) {
                                speedV -= 0.74 * (speedV - MoveUtils.getBaseMoveSpeed());
                                prevOnGround = false;
                            } else {
                                speedV -= speedV / 159;
                            }

                            if(!MoveUtils.isWalking() || mc.thePlayer.isCollidedHorizontally) {
                                speedV = MoveUtils.getBaseMoveSpeed();
                            }

                            speedV = Math.max(speedV, MoveUtils.getBaseMoveSpeed());
                            MoveUtils.strafe(e, speedV);
                            break;
                        }

                        case "Funcraft Funny":{
                            mc.timer.timerSpeed = 1.15f;
                            if(mc.thePlayer.onGround) {
                                prevOnGround = true;
                                if(MoveUtils.isWalking()) {
                                    e.setY(mc.thePlayer.motionY = 0.41999998688698);
                                    //mc.thePlayer.motionY = 0.42;
                                    // 2.13
                                    speedV *= 2.13;
                                }
                            } else if(prevOnGround) {
                                speedV -= 0.69 * (speedV - MoveUtils.getBaseMoveSpeed());
                                prevOnGround = false;
                                if(mc.gameSettings.keyBindJump.isKeyDown()) {
                                    mc.timer.timerSpeed = 1.35f;
                                    e.setY(mc.thePlayer.motionY -= 1);
                                }
                            } else {
                                speedV -= speedV / 159;
                            }

                            if(!MoveUtils.isWalking() || mc.thePlayer.isCollidedHorizontally) {
                                speedV = MoveUtils.getBaseMoveSpeed();
                            }

                            speedV = Math.max(speedV, MoveUtils.getBaseMoveSpeed());
                            MoveUtils.strafe(e, speedV);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if(event instanceof EventMotion){
            switch(mode.getMode()){
                case "Verus":{// ca serait cool mais  de tt facon a cause de la migration ca devient de moins en moins bien le cheat donc jsp jsuis nul a faire des bypass et les acs deviennent meilleurs
                    //oe je vois
                    //bah essaye de trouver des serveurs avec bcp de monde avec des ac de merde :)
                    //et aussi regarde cmt mc fonctionne car les anticheats performant se base sur ça
                    //petit example le Fly I de verus
                    switch(verusMode.getMode()){
                        case "Hop":{
                            if(!mc.thePlayer.isCollidedHorizontally){
                                if(mc.thePlayer.onGround){
                                    if(mc.thePlayer.isMoving()){
                                        //mc.gameSettings.keyBindJump.pressed = true;
                                        mc.thePlayer.motionY = 0.42f;
                                    }
                                }else{
                                    if(mc.thePlayer.moveForward > 0){
                                        MoveUtils.strafe(0.377);
                                    }else{
                                        MoveUtils.strafe(0.335);
                                    }
                                    //mc.gameSettings.keyBindJump.pressed = false;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case "NCP":{
                    switch(ncpMode.getMode()){
                        case "Funcraft":{
                            mc.timer.timerSpeed = 1.2f;
                            if(mc.thePlayer.onGround){
                                double perfect = 1.724;
                                MoveUtils.strafe(MoveUtils.getMotion() * 1.728);
                                mc.thePlayer.motionY = 0.42f;
                                float speed = 0.0320f; //0.0320f
                                if(mc.thePlayer.speedInAir < speed){
                                   // Wrapper.instance.log(mc.thePlayer.speedInAir + "");
                                    if(mc.thePlayer.speedInAir < 0.025){
                                        mc.thePlayer.speedInAir = (float) (0.025f + (Math.random() / 100));
                                    }
                                    mc.thePlayer.speedInAir += 0.0101f;
                                }else{
                                    mc.thePlayer.speedInAir = speed;
                                }
                                if(mc.thePlayer.jumpMovementFactor > 0.022){
                                    mc.thePlayer.jumpMovementFactor -= 0.002f;
                                }

                            }else{
                                if(mc.thePlayer.fallDistance > 0){
                                    mc.thePlayer.motionY *= 1.00;
                                }else{
                                    mc.thePlayer.motionY *= 1.0;
                                }
                                mc.thePlayer.motionX *= 0.988f;
                                mc.thePlayer.motionZ *= 0.988f;
                                mc.thePlayer.speedInAir -= 0.00019f;
                                if(mc.thePlayer.fallDistance > 0.4 && mc.thePlayer.fallDistance < 0.41){
                                    mc.thePlayer.motionY -= 0.1f;
                                }
                                if(mc.thePlayer.hurtTime > 4){
                                    mc.thePlayer.speedInAir+=0.006f;
                                }
                                MoveUtils.strafe();
                            }
                            break;
                        }

                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {

        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.jumpMovementFactor = 0.02F;
        mc.thePlayer.speedInAir = 0.02F;
    }
}
