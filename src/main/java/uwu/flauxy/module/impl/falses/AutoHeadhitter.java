package uwu.flauxy.module.impl.falses;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "AutoHeadHitter", cat = Category.False, displayName = "Auto Head Hitter", key = Keyboard.KEY_NONE)
public class AutoHeadhitter extends Module {

    public BooleanSetting autoHit = new BooleanSetting("Auto HH",true);
    public NumberSetting tickDelay = new NumberSetting("Auto HH Delay",2,2,20, 1).setCanShow(m -> autoHit.isEnabled());
    public BooleanSetting changeJumpDelay = new BooleanSetting("Change Jump Delay",true);
    public NumberSetting jumpDelay = new NumberSetting("MC Jump Delay",0,0,10, 1).setCanShow(m -> changeJumpDelay.getValue());
    public BooleanSetting perfectBoost = new BooleanSetting("Perfect Boost",true);
    public static int jumpDelayTick = 10;
    private boolean boost, actualGo, idk2;

    public AutoHeadhitter(){
        addSettings(autoHit, tickDelay, changeJumpDelay, jumpDelay, perfectBoost);
    }

    @Override
    public void onDisable() {
        boost = false;
        jumpDelayTick = 10;
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate)e;
            if(ev.getType().equals(EventType.PRE))return;
            jumpDelayTick = changeJumpDelay.isEnabled() ? (int) jumpDelay.getValue() : 10;
            if(autoHit.getValue() && !mc.thePlayer.isInWater()){
                if(mc.thePlayer.ticksExisted % tickDelay.getValue() == 0){
                    if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                        mc.gameSettings.keyBindJump.pressed = true;
                    }
                }else{
                    mc.gameSettings.keyBindJump.pressed = false;
                }
            }

            if (perfectBoost.isEnabled()) {
                // Get the player's current bounding box
                AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
                // Extend the bounding box upwards and in the direction the player is moving
                double expandY = 0.4;
                double expandForward = MoveUtils.getMotion(); // east south
                if(mc.thePlayer.getHorizontalFacing().equals(EnumFacing.WEST) || mc.thePlayer.getHorizontalFacing().equals(EnumFacing.NORTH)){
                    expandForward = -expandForward;
                }

                double expandForward2 = MoveUtils.getMotion();
                Vec3 lookVec = mc.thePlayer.getLookVec();
                AxisAlignedBB extendedBoundingBox = playerBoundingBox.expand(lookVec.xCoord * expandForward, expandY, lookVec.zCoord * expandForward);

                // Get the coordinates of the blocks that the bounding box intersects
                int minX = MathHelper.floor_double(extendedBoundingBox.minX);
                int maxX = MathHelper.floor_double(extendedBoundingBox.maxX);
                int minY = MathHelper.floor_double(extendedBoundingBox.maxY - 0.2);
                int maxY = MathHelper.floor_double(extendedBoundingBox.maxY);
                int minZ = MathHelper.floor_double(extendedBoundingBox.minZ);
                int maxZ = MathHelper.floor_double(extendedBoundingBox.maxZ);

                boolean blockAbove = false;
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            BlockPos blockPos = new BlockPos(x, y, z);
                            IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                            if (blockState.getBlock().getMaterial() != Material.air) {
                                blockAbove = true;
                                break;
                            }
                        }
                        if (blockAbove) break;
                    }
                    if (blockAbove) break;
                }

                boost = blockAbove;
                if (boost && actualGo) {
                    int d = jumpDelayTick;
                    jumpDelayTick = 0;
                    mc.gameSettings.keyBindJump.pressed = true;
                    jumpDelayTick = d;
                    actualGo = false;
                }
                if (!boost) actualGo = true;
            }
        }
    }
}
