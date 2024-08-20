package uwu.flauxy.module.impl.falses;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventFrame;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "SpamDrop", displayName = "Spam Drop", key = -1, cat = Category.False)
public class SpamDrop extends Module {
    // spam drop, spam block
    // hold key with Keyboard. and get the key from gamesettings

    private NumberSetting timing = new NumberSetting("Timing (tick)", 5,1,20,1);
    private BooleanSetting onSwingDrop = new BooleanSetting("On Swing",true);
    public SpamDrop(){
        addSettings(timing);
    }

    @Override
    public void onEnable() {
        Wrapper.instance.log("Usage: Hold the drop key to continuously send drop item packets");
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            EventUpdate ev = (EventUpdate)e;
            if(ev.getType().equals(EventType.POST))return;
            if(mc.thePlayer.ticksExisted % timing.getValue() == 0){
                if(Keyboard.isKeyDown(mc.gameSettings.keyBindDrop.getKeyCode())){
                    mc.thePlayer.dropOneItem(false);
                }
            }
            if(mc.gameSettings.keyBindAttack.pressed){
                mc.thePlayer.dropOneItem(false);
            }
        }
    }
}
