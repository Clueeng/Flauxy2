package uwu.noctura.module.impl.player;

import net.minecraft.block.BlockBed;
import net.minecraft.item.ItemBed;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.BlockUtil;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.WorldUtil;

import java.util.List;

@ModuleInfo(name = "Breaker", displayName = "Breaker", key = -1, cat = Category.Player)
public class Breaker extends Module {

    public NumberSetting range = new NumberSetting("Range", 4.2, 1, 6,.1);
    public BooleanSetting breakThrough = new BooleanSetting("Through Blocks", true);

    public Breaker(){
        addSettings(range, breakThrough);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){
            EventMotion ev = (EventMotion) e;
            double range = this.range.getValue();

            List<BlockPos> bedPositions = WorldUtil.searchBlocksInCuboid((float)range, mc.thePlayer.getPositionEyes(1.0f),
                    (pos, state) -> state.getBlock() instanceof BlockBed);

            if(!bedPositions.isEmpty()){
                BlockPos priority = bedPositions.get(0);

                if(!breakThrough.isEnabled() && !canBreak(priority)){
                    return;
                }

                float[] rotations = BlockUtil.getDirectionToBlock(priority.getX(), priority.getY(), priority.getZ(), EnumFacing.DOWN);
                Noctura.INSTANCE.getModuleManager().getModule(Scaffold.class).clientRotations(rotations[0], rotations[1]);
                ev.setYaw(rotations[0]);
                ev.setPitch(rotations[1]);

                breakBlock(priority);
            }
        }
    }

    private void breakBlock(BlockPos pos) {
        EnumFacing breakDirection = EnumFacing.DOWN;
        mc.playerController.onPlayerDamageBlock(pos, breakDirection);
        mc.thePlayer.swingItem();
    }

    private boolean canBreak(BlockPos pos) {
        int airCount = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offsetPos = pos.offset(facing);
            if (mc.theWorld.getBlockState(offsetPos).getBlock().isAir(mc.theWorld.getBlockState(offsetPos), mc.theWorld, offsetPos)) {
                airCount++;
            }
        }
        return airCount > 0;
    }
}
