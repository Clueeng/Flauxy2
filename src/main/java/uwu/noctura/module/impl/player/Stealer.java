package uwu.noctura.module.impl.player;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.timer.Timer;

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
