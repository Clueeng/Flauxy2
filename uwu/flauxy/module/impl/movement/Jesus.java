package uwu.flauxy.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.MoveUtils;

@ModuleInfo(name = "Jesus", displayName = "Jesus", key = -1, cat = Category.Movement)
public class Jesus extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Solid", "Solid", "Motion");

    public Jesus(){
        addSettings(mode);
    }
    int state = 0;

    @Override
    public void onEvent(Event event){
        if(event instanceof EventMotion){
            final float ascensionValue = 0.06000000238418583F;
            EventMotion e = (EventMotion)event;
            switch(mode.getMode()){
                case "Solid":{
                    BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ);
                    Block block = mc.theWorld.getBlockState(pos).getBlock();
                    if (mc.thePlayer.isInWater() || (mc.thePlayer.movementInput.jump && MoveUtils.isOnLiquid())){
                        mc.thePlayer.onGround = true;
                        mc.thePlayer.motionY = ascensionValue;
                        mc.gameSettings.keyBindJump.pressed = true;
                        state+=1;
                    }else{
                        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
                    }
                    if(block instanceof BlockLiquid){
                        mc.gameSettings.keyBindJump.pressed = true;
                        mc.thePlayer.motionY -= ascensionValue;
                        state+=1;
                    }else{

                        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
                    }
                    break;
                }
            }
        }
    }

}
