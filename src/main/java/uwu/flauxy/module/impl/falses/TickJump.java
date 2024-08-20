package uwu.flauxy.module.impl.falses;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.utils.Wrapper;

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
