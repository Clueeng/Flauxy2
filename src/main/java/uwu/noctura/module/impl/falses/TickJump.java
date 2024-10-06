package uwu.noctura.module.impl.falses;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

@ModuleInfo(name = "TickJump", displayName = "Tick Jump", cat = Category.False, key = -1)
public class TickJump extends Module {

    public boolean pressing, pressed;
    int ticks;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            KeyBinding key = mc.gameSettings.keyBindJump;
            pressing = Keyboard.isKeyDown(key.getKeyCode());
            if(ticks == 1){
                key.pressed = false;
                ticks = 0;
            }
            if(pressing){
                ticks += 1;
            }else{
                ticks = 0;
            }
        }
    }
}
