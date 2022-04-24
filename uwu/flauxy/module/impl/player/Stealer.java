package uwu.flauxy.module.impl.player;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Stealer", displayName = "Stealer", key = Keyboard.KEY_Y, cat = Category.Player)
public class Stealer extends Module {

    NumberSetting delay = new NumberSetting("Delay", 25, 0, 500, 5);
    BooleanSetting autoclose = new BooleanSetting("Auto Close", true);
    NumberSetting autocloseDelay = new NumberSetting("Delay", 10, 0, 100, 5);

    public Stealer(){
        addSettings(delay, autoclose, autocloseDelay);
    }
    private Timer timer = new Timer();

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if ((mc.thePlayer.openContainer != null) && (mc.thePlayer.openContainer instanceof ContainerChest)) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

                for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                    if ((chest.getLowerChestInventory().getStackInSlot(i) != null) && timer.hasTimeElapsed(delay.getValue(), true)) {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                    }
                }
                if(empty(chest) && autoclose.getValue() && timer.hasTimeElapsed(autocloseDelay.getValue(), true)){
                    mc.thePlayer.closeScreen();
                }
            }
        }
    }

    private boolean empty(Container c2) {
        int slot = c2.inventorySlots.size() == 90 ? 54 : 27;
        for (int i2 = 0; i2 < slot; ++i2) {
            if (!c2.getSlot(i2).getHasStack()) continue;
            return false;
        }
        return true;
    }
}
