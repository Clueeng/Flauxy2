package uwu.flauxy.module.impl.player;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;

@ModuleInfo(name = "InventoryMove", displayName = "Inventory Move", key = -1, cat = Category.Player)
public class InventoryMove extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Spoof");

    public InventoryMove() {
        addSettings(mode);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion) {
            if(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat || mc.currentScreen == null) return;
            keyset(mc.gameSettings.keyBindForward);
            keyset(mc.gameSettings.keyBindLeft);
            keyset(mc.gameSettings.keyBindRight);
            keyset(mc.gameSettings.keyBindBack);
            keyset(mc.gameSettings.keyBindJump);
        }
    }

    private void keyset(KeyBinding key){
        key.pressed = Keyboard.isKeyDown(key.getKeyCode());
    }

}
