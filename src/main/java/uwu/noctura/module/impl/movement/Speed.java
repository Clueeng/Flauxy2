package uwu.noctura.module.impl.movement;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.event.impl.packet.EventMove;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.combat.Killaura;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.MoveUtils;
import uwu.noctura.utils.NumberUtil;
import uwu.noctura.utils.Wrapper;

@ModuleInfo(name = "Speed", displayName = "Speed", key = Keyboard.KEY_X, cat = Category.Movement)
public class Speed extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "NCP", "Test", "Polar", "BlocksMC", "Redesky", "Cos Factor", "ClueAC", "Bullet", "Vulcan", "Hypixel");
    public ModeSetting vulcanMode = new ModeSetting("Vulcan Mode", "Low", "Low", "Hop", "Ground").setCanShow(m -> mode.is("Vulcan"));
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
        addSettings(mode, vulcanMode, ncpMode, testMode, verusMode, bmcMode, speed);
    }


    @Override
    public void onEnable() {
        stage = 0;
        switch (mode.getMode()){
            case "Polar":{
                airTick = 0;
                funny = false;
                mc.timer.timerSpeed=1f;
                break;
            }
            case "NCP":{
                speedV = MoveUtils.getBaseMoveSpeed();
                break;
            }
            case "Vulcan":{
                funny = false;
                mc.timer.timerSpeed=1f;
                break;
            }
        }
    }

    public void onEvent(Event event){
        if(mc.thePlayer == null) return;
        if(event instanceof EventUpdate){
            if(mc.thePlayer == null || mc.theWorld == null) this.toggle();
            this.setArrayListName("Speed " + EnumChatFormatting.WHITE + mode.getMode());
        }
        switch(mode.getMode()){
            case "Hypixel":{
                hypixel(event);
                break;
            }
            case "Polar":{
                polarSpeed(event);
                break;
            }
            case "Vulcan":{
                switch (vulcanMode.getMode()){
                    case "Low":{
                        vulcanoSped(event);
                        break;
                    }
                    case "Hop":{
                        vulcanoSpedHop(event);
                        break;
                    }
                    case "Ground":{
                        vulcanoGround(event);
                        break;
                    }
                }
                break;
            }
            case "Bullet":{
                if(event instanceof EventMotion){
                    if(!MoveUtils.isWalking()) return;
                }
                if(event instanceof EventUpdate){
                    mc.gameSettings.keyBindJump.pressed = true;
                    boolean anythingButForward = mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed;
                    if(mc.thePlayer.ticksExisted % 20 == 0){
                        if(!anythingButForward){
                            mc.thePlayer.jumpMovementFactor = 0.025f;
                        }
                        mc.timer.timerSpeed = 0.22f;
                        offGroundTicks++;
                        if(offGroundTicks == 10){
                            mc.timer.timerSpeed = 0.152f;
                        }
                    }
                    if(mc.thePlayer.ticksExisted % 20 == 3){
                        if(!anythingButForward){
                            mc.thePlayer.jumpMovementFactor = 0.020f;
                        }
                        mc.timer.timerSpeed = 3f;
                        if(offGroundTicks > 10){
                            mc.timer.timerSpeed = 3.2f;
                            offGroundTicks = 0;
                        }
                    }
                    if(MoveUtils.distToGround(4) < 0.4 && mc.thePlayer.motionY < -0.01){
                        mc.thePlayer.motionY -= 2f;
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
                                int amp = MoveUtils.getSpeedEffect();
                                float funny = 0.0f;
                                MoveUtils.strafe(0.48 + (0.205f * amp) + funny); // .47
                            }
                            if(!mc.thePlayer.onGround){
                                offGroundTicks += 1;
                                onGroundTicks = 0;
                            }
                            if(offGroundTicks > 2){
                                int amp = MoveUtils.getSpeedEffect();
                                MoveUtils.strafe(MoveUtils.getMotion() * (0.91 + (amp / 100f)));
                            }
                            if(mc.thePlayer.hurtTime > 6 && Noctura.INSTANCE.getModuleManager().getModule(Killaura.class).currentTarget != null && offGroundTicks > 5){

                                int amp = MoveUtils.getSpeedEffect();
                                MoveUtils.strafe(0.74f + (0.15f * amp));
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
                                MoveUtils.strafe(0.6);
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


    private float airTick, groundTick, polarSpeeds = 1.6f;
    private boolean funny;
    public void polarSpeed(Event e){
        if(e instanceof EventMotion){
            EventMotion ev = (EventMotion) e;
            if(!ev.isPre())return;
            if(mc.thePlayer.isMoving() && mc.thePlayer.hurtTime == 0){
                if(!mc.thePlayer.onGround){
                    airTick++;
                    groundTick = 0;
                }else{
                    airTick = 0;
                    groundTick++;
                }
                if (airTick >= 0 && airTick <= 33) {
                    if (mc.thePlayer.onGround && groundTick >= 3 && mc.thePlayer.motionY == -0.0784000015258789) {
                        mc.thePlayer.jump();
                        funny = false;
                        mc.timer.timerSpeed = 1f;
                    } else if (airTick > 0 && !funny) {
                        funny = true;
                        mc.timer.timerSpeed = 1f;

                    } else if (airTick >= 3 && airTick <= 4) {
                        if (airTick % 2 == 0) {
                            ((EventMotion) e).setOnGround(true);
                            mc.thePlayer.jump();
                            mc.thePlayer.jump();
                            mc.thePlayer.motionX*= polarSpeeds;
                            mc.thePlayer.motionZ *= polarSpeeds;
                            mc.thePlayer.motionY -= 0.12;

                        } else {
                            mc.timer.timerSpeed = 1f;

                            mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                        }
                    }
                } else {
                    mc.thePlayer.jumpMovementFactor = 0.02f;
                }
            }
        }
    }

    private void vulcanoGround(Event e){
        if(e instanceof EventMotion){
            EventMotion em = (EventMotion) e;
            if(!MoveUtils.isWalking())return;

            if (mc.thePlayer.isAirBorne && mc.thePlayer.fallDistance > 2) {
                mc.timer.timerSpeed = 1f;
                return;
            }

            if (mc.thePlayer.onGround) {
                //mc.thePlayer.tryJump();
                mc.gameSettings.keyBindJump.pressed = true;
                int amp = MoveUtils.getRealSpeedEffect();
                float ds = (float) ((MoveUtils.getBaseSpeed() * 1.26f) + ((amp * 0.18f) + (amp > 0 ? 0.00f : 0)));

                if(MoveUtils.standsOnIce()){
                    if(amp > 0){
                        MoveUtils.strafe(MoveUtils.getMotion() * 1.72f);
                    }else{
                        MoveUtils.strafe(MoveUtils.getBaseSpeed() * 1.28f);
                    }
                }
                mc.thePlayer.motionY = 0.42f;
                offGroundTicks = 0;
                if(amp > 0){
                    MoveUtils.strafe(ds - 0.365f);
                }else{

                }
            } else {
                int amp = MoveUtils.getRealSpeedEffect();
                float ds = (float) ((MoveUtils.getBaseSpeed() * 1.26f) + (amp * 0.18f));
                offGroundTicks++;
                if(offGroundTicks == 1 && amp > 0){
                    if(amp == 1){
                        MoveUtils.strafe(ds - 0.21f);
                    }else{
                        MoveUtils.strafe(ds - 0.32f);
                    }
                }
                if(offGroundTicks == 1 && amp == 0){
                    MoveUtils.strafe(MoveUtils.getSpeed() * 0.92);

                }
                mc.gameSettings.keyBindJump.pressed = false;
                if (mc.thePlayer.motionY < 0 && mc.thePlayer.fallDistance < 0.978f && offGroundTicks > 5) {
                    mc.thePlayer.motionY -= 0.06;
                }
            }
        }
    }

    private void vulcanoSpedHop(Event e) {
        if(e instanceof EventSendPacket){
            EventSendPacket es = (EventSendPacket) e;
            if(es.getPacket() instanceof C03PacketPlayer){
                // send vehicle packet to spoof a vehicle
                // will have to pingspoof over 1.5k ms to bypass BP V

                // Spoof a nonexistent entity to ride
                EntityHorse spoofedHorse = new EntityHorse(mc.thePlayer.worldObj);
                spoofedHorse.setPosition(
                        mc.thePlayer.posX + 2,
                        mc.thePlayer.posY - 1,
                        mc.thePlayer.posZ
                );
                mc.thePlayer.mountEntity(spoofedHorse);

            }
        }
        if(e instanceof EventMotion){
            EventMotion em = (EventMotion) e;
            if(!MoveUtils.isWalking())return;

            if(mc.thePlayer.onGround && em.isPre()){
                mc.thePlayer.motionY = 0.42f;
                MoveUtils.strafe(.42);
                offGroundTicks = 0;
            }
            if(!mc.thePlayer.onGround){
                offGroundTicks++;
                if(offGroundTicks > 1){
                    if(offGroundTicks % 3 == 0){
                        Wrapper.instance.log(MoveUtils.distToGround(3) + 0.02);
                        em.setY(mc.thePlayer.posY - MoveUtils.distToGround(3) + 0.01);
                        em.setOnGround(true);
                    }
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
    public void vulcanoSped(Event e){
        if(e instanceof EventMotion){
            EventMotion em = (EventMotion) e;
            if(!MoveUtils.isWalking())return;
            if(!mc.gameSettings.keyBindJump.pressed){
                int amp = MoveUtils.getSpeedEffect();
                int ampreal = MoveUtils.getRealSpeedEffect();
                if(mc.thePlayer.onGround && em.isPre()){
                    MoveUtils.jumpVanilla(false, em);
                    float ds = (float) ((MoveUtils.getBaseSpeed() * 1.26f) + ((amp * 0.18f) + (ampreal > 0 ? 0.012f : 0)));
                    if(ampreal == 1){
                        ds += 0.096f;
                    }
                    if(mc.thePlayer.moveStrafing != 0){
                        ds -= 0.004f;
                    }
                    MoveUtils.strafe(ds);
                    if(MoveUtils.standsOnIce()){
                        float baseSpeed = (float) MoveUtils.getBaseSpeed();
                        float ampFactor = (amp * 0.18f) + (amp > 0 ? 0.012f : 0);
                                                                         // ice boost
                        float result = (baseSpeed * 1.26f + ampFactor) * (amp > 0 ? 1 + (amp / 3.5f) : 1.f);
                        MoveUtils.strafe(result);
                    }
                    mc.timer.timerSpeed = 1.0f;
                    funny = false;
                }
                if(!mc.thePlayer.onGround && !funny){
                    if(ampreal > 0){
                        MoveUtils.strafe(MoveUtils.getMotion() * 0.62);
                    }
                    mc.thePlayer.motionY = -0.13;
                    funny = true;
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
