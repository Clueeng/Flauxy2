package uwu.flauxy.module.impl.player;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

@ModuleInfo(name = "InventoryMove", displayName = "Inventory Move", key = -1, cat = Category.Player)
public class InventoryMove extends Module {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion) {
            if(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat || mc.currentScreen == null)
                return;
            keyset(mc.gameSettings.keyBindForward);
            keyset(mc.gameSettings.keyBindLeft);
            keyset(mc.gameSettings.keyBindRight);
            keyset(mc.gameSettings.keyBindBack);
            keyset(mc.gameSettings.keyBindJump);
        }
    }

    private void keyset(KeyBinding key){
        key.pressed = GameSettings.isKeyDown(key);
    }

}
