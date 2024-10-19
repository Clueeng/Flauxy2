package uwu.noctura.module.impl.player;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.ui.dropdown.ClickGUI;
import uwu.noctura.ui.packet.PacketTweaker;

@ModuleInfo(name = "InventoryMove", displayName = "Inventory Move", key = -1, cat = Category.Player)
public class InventoryMove extends Module {

    //public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Spoof");
    public BooleanSetting onlyClickGUI = new BooleanSetting("ClickGUI only", false);

    public InventoryMove() {
        addSettings(onlyClickGUI);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion) {
            if(!(mc.currentScreen instanceof ClickGUI || mc.currentScreen instanceof uwu.noctura.ui.astolfo.ClickGUI || mc.currentScreen instanceof uwu.noctura.ui.noctura.ClickGUI)){
                if(onlyClickGUI.isEnabled()){
                    return;
                }
            }
            if(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat || mc.currentScreen == null || mc.currentScreen instanceof PacketTweaker) return;
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
