package uwu.noctura.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

@ModuleInfo(name = "AutoTool", displayName = "Auto Tool", key = 0, cat = Category.Player)
public class AutoTool extends Module {

    private int oldSlot = -1;
    private boolean wasBreaking = false;

    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            if (this.mc.currentScreen == null && this.mc.thePlayer != null && this.mc.theWorld != null && this.mc.objectMouseOver != null && this.mc.objectMouseOver
                    .getBlockPos() != null && this.mc.objectMouseOver.entityHit == null &&
                    Mouse.isButtonDown(0)) {
                float bestSpeed = 1.0F;
                int bestSlot = -1;
                Block block = this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock();
                for (int k = 0; k < 9; k++) {
                    ItemStack item = this.mc.thePlayer.inventory.getStackInSlot(k);
                    if (item != null) {
                        float speed = item.getStrVsBlock(block);
                        if (speed > bestSpeed) {
                            bestSpeed = speed;
                            bestSlot = k;
                        }
                    }
                }
                if (bestSlot != -1 && this.mc.thePlayer.inventory.currentItem != bestSlot) {
                    this.mc.thePlayer.inventory.currentItem = bestSlot;

                    this.wasBreaking = true;
                } else if (bestSlot == -1) {
                    if (this.wasBreaking) {
                        this.mc.thePlayer.inventory.currentItem = this.oldSlot;
                        this.wasBreaking = false;
                    }
                    this.oldSlot = this.mc.thePlayer.inventory.currentItem;
                }
            } else if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (this.wasBreaking) {
                    this.mc.thePlayer.inventory.currentItem = this.oldSlot;
                    this.wasBreaking = false;
                }
                this.oldSlot = this.mc.thePlayer.inventory.currentItem;
            }
        }


    }
}
