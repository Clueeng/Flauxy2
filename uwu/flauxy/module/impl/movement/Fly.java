package uwu.flauxy.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Motion", "Motion", "NCP", "Verus");
    public ModeSetting ncpMode = new ModeSetting("NCP Mode", "Funcraft","Funcraft").setCanShow(m -> mode.is("NCP"));
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Slow","Slow", "Normal", "Damage").setCanShow(m -> mode.is("Verus"));
    public NumberSetting speed = new NumberSetting("Speed", 0.4, 0.2, 5, 0.05).setCanShow(m -> mode.is("Motion") || (mode.is("NCP") && ncpMode.is("Funcraft") ||
            (mode.is("Verus") && verusMode.is("Damage"))) );
    public NumberSetting timer = new NumberSetting("Timer", 1, 1, 2, 0.05).setCanShow(m -> mode.is("NCP") && ncpMode.is("Funcraft"));

    private Timer time = new Timer();
    int ticks = 0;
    float speedFC = 0f;
    public Fly(){
        addSettings(mode, ncpMode, verusMode, speed, timer);
    }

    @Override
    public void onEnable() {
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
        ticks = 0;
        switch(mode.getMode()){
            case "NCP":{
                switch (ncpMode.getMode()){
                    case "Funcraft":{
                        speedFC = mc.thePlayer.onGround ? (float) (mc.thePlayer.moveForward > 0.28 ? (Flauxy.INSTANCE.moduleManager.getModule(Speed.class).isToggled() ? MoveUtils.getBaseSpeed() / 1.1f : 1.67f) * mc.thePlayer.moveForward - 0.2873 + MoveUtils.getBaseSpeed() : 0.28) : (float) MoveUtils.getSpeed();
                        //speed = mc.thePlayer.moveForward > 0.28 ? 1.7F * mc.thePlayer.moveForward : 0.28;
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.jump();
                        }else{
                            mc.thePlayer.motionY = 0f;
                        }

                        mc.thePlayer.isCollidedVertically = false;
                        break;
                    }
                }
                break;
            }
        }
    }

    // for verus
    BlockPos oldPos;
    List<BlockPos> blockPosList = new ArrayList<>();
    boolean removeBlock = false;
    @Override
    public void onEvent(Event e){
        if(e instanceof EventMotion){
            EventMotion event = (EventMotion) e;
            if(mode.is("NCP") && ncpMode.is("Funcraft")){
                speed.setMaximum(0.9);
                speed.setValue(speed.getValue() > speed.getMaximum() ? speed.getMaximum() : speed.getValue());
            }
            if(!mode.is("NCP")){
                speed.setMaximum(5);
            }
            switch(mode.getMode()){
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Slow":{
                            if(ticks <= 1){
                                mc.thePlayer.motionY = 0.42f;
                            }else{
                                if(mc.thePlayer.isCollidedVertically){
                                    mc.thePlayer.motionY = 0.42f;
                                    MoveUtils.strafe(MoveUtils.getSpeed() * 1.35f);
                                }else{
                                    MoveUtils.strafe();
                                }
                                if(mc.thePlayer.fallDistance >= 0.2f){
                                    BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.9, mc.thePlayer.posZ);
                                    if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir){
                                        blockPosList.add(pos);
                                        oldPos =  pos;
                                        mc.theWorld.setBlock(pos, Blocks.barrier);
                                        mc.thePlayer.fallDistance = 0;
                                    }
                                }
                                if(mc.thePlayer.fallDistance >= 0 && mc.thePlayer.fallDistance <= 0.5f && oldPos != null && mc.theWorld.getBlockState(oldPos).getBlock() instanceof BlockBarrier){
                                    removeBlock = true;
                                }
                                if(removeBlock){
                                    if(time.hasTimeElapsed(200, true)){
                                        for(BlockPos bp : blockPosList){
                                            mc.theWorld.setBlockToAir(bp);
                                        }
                                        mc.theWorld.setBlockToAir(oldPos);
                                        removeBlock = false;
                                    }
                                }


                            }
                            ticks++;
                            break;
                        }
                        case "Damage":{
                            if(ticks <= 1){
                                MoveUtils.damage(MoveUtils.Bypass.VERUS);
                                mc.timer.timerSpeed = 0.15f;
                                mc.thePlayer.motionY = 0.42f;
                            }else{
                                if(ticks < 55){
                                    if(ticks > 3){
                                        mc.timer.timerSpeed = 1f;
                                    }
                                    MoveUtils.strafe(speed.getValue());
                                }else{
                                    MoveUtils.strafe(MoveUtils.getBaseSpeed());
                                }
                                if(mc.thePlayer.isCollidedVertically){
                                    mc.thePlayer.motionY = 0.42f;
                                    MoveUtils.strafe(MoveUtils.getSpeed() / 1.8f);
                                }
                                if(mc.thePlayer.fallDistance >= 0.2f){
                                    BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.9, mc.thePlayer.posZ);
                                    if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir){
                                        blockPosList.add(pos);
                                        oldPos =  pos;
                                        mc.theWorld.setBlock(pos, Blocks.barrier);
                                        mc.thePlayer.fallDistance = 0;
                                    }
                                }
                                if(mc.thePlayer.fallDistance >= 0 && mc.thePlayer.fallDistance <= 0.5f && oldPos != null && mc.theWorld.getBlockState(oldPos).getBlock() instanceof BlockBarrier){
                                    removeBlock = true;
                                }
                                if(removeBlock){
                                    if(time.hasTimeElapsed(200, true)){
                                        for(BlockPos bp : blockPosList){
                                            mc.theWorld.setBlockToAir(bp);
                                        }
                                        mc.theWorld.setBlockToAir(oldPos);
                                        removeBlock = false;
                                    }
                                }


                            }
                            ticks++;
                            break;
                        }
                    }
                    break;
                }
                case "NCP":{
                    switch(ncpMode.getMode()){
                        case "Funcraft":{
                            if(mc.thePlayer.isCollidedVertically) {
                                mc.thePlayer.jump();
                            }
                            if(ticks > 2){
                                MoveUtils.strafe(speedFC);
                                mc.timer.timerSpeed = 1.15f;
                                mc.thePlayer.cameraYaw = 0.0385F;
                                mc.thePlayer.jumpMovementFactor = 0;
                                if(!mc.thePlayer.isCollidedHorizontally) {
                                    if(speedFC > 0.49) {
                                        speedFC -= speedFC / 189; // 159
                                    }else{
                                        if(speedFC > 0.26){
                                            speedFC -= speedFC / 259;
                                        }
                                    }
                                }else {
                                    speedFC = 0.15F;
                                }
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2E-4, mc.thePlayer.posZ);
                                mc.thePlayer.motionY = - 0;
                            }
                            ticks++;
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
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
        switch(mode.getMode()){
            case "NCP":{
                switch(ncpMode.getMode()){
                    case "Funcraft":{
                        MoveUtils.strafe(MoveUtils.getBaseSpeed());
                        break;
                    }
                }
                break;
            }
        }
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.jumpMovementFactor = 0.02F;
        mc.thePlayer.speedInAir = 0.02F;

    }
}
