package uwu.flauxy.module.impl.movement;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.EventStrafe;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.combat.Killaura;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.NumberUtil;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Speed", displayName = "Speed", key = Keyboard.KEY_X, cat = Category.Movement)
public class Speed extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "NCP", "Test", "BlocksMC", "Redesky", "Cos Factor", "ClueAC", "Bullet", "Karhu", "Hypixel");
    public ModeSetting ncpMode = new ModeSetting("NCP Mode", "Funcraft", "Funcraft", "Funcraft Funny", "Hypixel Like").setCanShow(m -> mode.is("NCP"));
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Hop", "Hop", "Low", "Fast").setCanShow(m -> mode.is("Verus"));
    public ModeSetting bmcMode = new ModeSetting("BMC Mode", "Strafe", "Strafe", "Low", "No Strafe").setCanShow(m -> mode.is("BlocksMC"));
    public ModeSetting testMode = new ModeSetting("Test Mode", "Test 1", "Test 1", "Test 2", "Test 3").setCanShow(m -> mode.is("Test"));
    public NumberSetting speed = new NumberSetting("Speed", 0.6, 0.2, 2, 0.05).setCanShow(m -> mode.is("Vanilla") || mode.is("Cos Factor") || mode.is("ClueAC"));
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
            case "Karhu":{
                airTick = 0;
                funny = false;
                mc.timer.timerSpeed=1f;
                break;
            }
        }
    }

    public void onEvent(Event event){
        if(event instanceof EventUpdate){
            this.setDisplayName("Speed " + EnumChatFormatting.WHITE + mode.getMode());
        }
        switch(mode.getMode()){
            case "Hypixel":{
                hypixel(event);
                break;
            }
            case "Karhu":{
                karhuSpeed(event);
                break;
            }
            case "Bullet":{
                if(event instanceof EventMotion){
                    if(!MoveUtils.isWalking()) return;
                    mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
                    if(mc.thePlayer.motionY < -0 && mc.thePlayer.motionY > -0.24){
                        double fX = mc.thePlayer.motionX * 0.03;
                        double fZ = mc.thePlayer.motionZ * 0.03;
                        mc.thePlayer.setPosition(mc.thePlayer.posX + fX, mc.thePlayer.posY, mc.thePlayer.posZ + fZ);
                    }
                    if(mc.thePlayer.onGround){
                        mc.timer.timerSpeed = 1f;
                    }
                }
                if(event instanceof EventStrafe){
                    EventStrafe e = (EventStrafe) event;
                    if(!MoveUtils.isWalking()) return;
                    float f = e.getYaw() * 0.017453292F;
                    if(mc.thePlayer.onGround && mc.thePlayer.moveForward <= 0){
                        mc.thePlayer.motionX += (double)(MathHelper.sin(f) * 0.35F);
                        mc.thePlayer.motionZ -= (double)(MathHelper.cos(f) * 0.35F);
                        MoveUtils.jumpVanilla(e);
                    }
                    if(mc.thePlayer.onGround && mc.thePlayer.moveForward >= 0){
                        boolean onlyMovingForward = mc.gameSettings.keyBindForward.pressed && (!mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed);
                        boolean movingSidewars = mc.gameSettings.keyBindForward.pressed && (mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed);
                        if(movingSidewars){
                            Wrapper.instance.log("Sideways");
                            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.15F);
                            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.15F);
                        }
                        if(onlyMovingForward){
                            Wrapper.instance.log("Only forwards");
                            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.14F);
                            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.14F);
                        }
                        mc.thePlayer.motionY = 0.42f;
                    }
                }


                break;
            }
            case "Cos Factor":{
                if(event instanceof EventMotion){
                    if(mc.thePlayer.onGround){
                        mc.thePlayer.jump();
                        MoveUtils.strafe(
                                (speed.getValue()) * Math.cos((mc.thePlayer.motionX + mc.thePlayer.motionZ))
                        );
                    }else{
                        MoveUtils.strafe(
                                1 * (MoveUtils.getSpeed())
                        );
                    }
                }

                break;
            }
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
                if(!mc.thePlayer.isMoving())return;
                if(mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                MoveUtils.strafe(speed.getValue());

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
                        }
                        if(event instanceof EventUpdate){
                            if(mc.thePlayer.onGround){
                                mc.gameSettings.keyBindJump.pressed = false;
                                onGroundTicks += 1;
                                offGroundTicks = 0;
                                MoveUtils.strafe(0.49); // .47
                            }
                            if(!mc.thePlayer.onGround){
                                offGroundTicks += 1;
                                onGroundTicks = 0;
                            }
                            if(offGroundTicks > 2){
                                MoveUtils.strafe(MoveUtils.getMotion() * 0.91);
                            }
                        }
                        if(event instanceof EventMove){
                            EventMove ev = (EventMove) event;
                            if(mc.thePlayer.onGround){
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
                        speedV *= 1.949;
                        stage = 0;
                    }




                    break;

                case "ClueAC":{
                    if(!MoveUtils.isWalking()) return;
                    mc.gameSettings.keyBindJump.pressed = true;
                    if(mc.thePlayer.onGround){
                        MoveUtils.strafe(1.7f);
                    }
                    MoveUtils.strafe(MoveUtils.getSpeed());
                    break;
                }
                case "Test":{
                    switch(testMode.getMode()){
                        case "Test 2":{
                            final String motion = String.valueOf(mc.thePlayer.motionY);
                            MoveUtils.strafe();
                            mc.gameSettings.keyBindJump.pressed = true;
                            if(mc.thePlayer.onGround){
                                offGroundTicks = 0;
                                MoveUtils.strafe(1.6);
                                //mc.thePlayer.onGround = false;
                            }else{
                                MoveUtils.strafe();
                            }
                            offGroundTicks++;
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
                            if(!mc.thePlayer.isMoving())return;
                            mc.timer.timerSpeed = 1.0f;
                            if(mc.thePlayer.onGround) {
                                prevOnGround = true;
                                if(MoveUtils.isWalking()) {
                                    e.setY(mc.thePlayer.motionY = 0.41999998688698);
                                    //mc.thePlayer.motionY = 0.42;
                                    // 2.13
                                    speedV *= 2.1;
                                }
                            } else if(prevOnGround) {
                                speedV -= 0.91 * (speedV - MoveUtils.getBaseMoveSpeed());
                                prevOnGround = false;
                            } else {
                                speedV -= speedV / 159;
                            }

                            if(!MoveUtils.isWalking() || mc.thePlayer.isCollidedHorizontally) {
                                speedV = MoveUtils.getBaseMoveSpeed();
                            }

                            speedV = Math.max(speedV, MoveUtils.getBaseMoveSpeed());
                            MoveUtils.strafe(speedV);

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
                                    if(speedV * 1.83 < 0.54){
                                        speedV *= 1.83;
                                    }else{
                                        speedV = 0.45;
                                    }
                                }
                            } else if(prevOnGround) {
                                speedV -= 0.69 * (speedV - MoveUtils.getBaseMoveSpeed());
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
                            if(mc.thePlayer.fallDistance > 1.0){
                                mc.timer.timerSpeed = 0.8f;
                            }else{
                                mc.timer.timerSpeed = 1.9f;
                            }
                            if(mc.thePlayer.onGround){
                                double perfect = 1.724;
                                //MoveUtils.strafe(MoveUtils.getMotion() * 1.728);
                                mc.thePlayer.motionY = 0.42f;
                                float speed = 0.0310f; //0.0320f
                                if(mc.thePlayer.speedInAir < speed){
                                   // Wrapper.instance.log(mc.thePlayer.speedInAir + "");
                                    if(mc.thePlayer.speedInAir < 0.025){
                                        mc.thePlayer.speedInAir = (float) (0.025f + (Math.random() / 100));
                                    }
                                    mc.thePlayer.speedInAir += 0.0091f;
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
                                    mc.thePlayer.motionY *= 1.00;
                                }
                                mc.thePlayer.motionX *= 0.982f;
                                mc.thePlayer.motionZ *= 0.982f;
                                mc.thePlayer.speedInAir -= 0.00019f;
                                if(mc.thePlayer.fallDistance > 0.4 && mc.thePlayer.fallDistance < 0.41){
                                    mc.thePlayer.motionY -= 0.1f;
                                }
                                if(mc.thePlayer.hurtTime > 4){
                                    mc.thePlayer.speedInAir+=0.006f;
                                }
                                //MoveUtils.strafe();
                            }
                            break;
                        }

                    }
                    break;
                }
            }
        }
    }

    private void hypixel(Event event) {
        if(event instanceof EventMotion){
            EventMotion e = (EventMotion) event;
            if(e.isPre()){
                if(mc.thePlayer.onGround && mc.thePlayer.isCollided){
                    mc.thePlayer.jump();
                    MoveUtils.strafe(MoveUtils.getBaseMoveSpeed() * 1.23f);
                }
            }
        }
    }

    private float airTick, groundTick, karhuSpeed = 1.6f;
    private boolean funny;
    public void karhuSpeed(Event e){
    }

    @Override
    public void onDisable() {

        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.jumpMovementFactor = 0.02F;
        mc.thePlayer.speedInAir = 0.02F;
    }
}
