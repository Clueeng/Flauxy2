package uwu.noctura.module.impl.player;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;

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
