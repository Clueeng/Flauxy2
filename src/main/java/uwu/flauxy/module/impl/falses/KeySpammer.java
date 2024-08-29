package uwu.flauxy.module.impl.falses;

import it.unimi.dsi.fastutil.booleans.BooleanSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventFrame;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "KeySpammer", key = -1, cat = Category.False, displayName = "Key Spammer")
public class KeySpammer extends Module {

    public static int spamKeycode;
    public BooleanSetting onFrame = new BooleanSetting("On Frame",true);
    public BooleanSetting flipKeystate = new BooleanSetting("Flip Keystate",true);

    public NumberSetting delay = new NumberSetting("Delay",50,0,200,1).setCanShow(o -> !onFrame.isEnabled());
    public long lastDrop = 0;

    public KeySpammer(){
        addSettings(onFrame, delay, flipKeystate);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventFrame){
            //KeyBinding.
            if(!onFrame.getValue()){
                if(Math.abs(System.currentTimeMillis() - lastDrop) > delay.getValue()){
                    //if(Keyboard.isKeyDown(spamKeycode)){
                    //    Wrapper.instance.log("Spamming " + Keyboard.getKeyName(spamKeycode));
                    //    KeyBinding.onTick(spamKeycode);
                    //}
                    key(flipKeystate.isEnabled());
                    lastDrop = System.currentTimeMillis();
                }
            }else{
                key(flipKeystate.isEnabled());
            }
        }
    }

    boolean globalFlip;

    public void key(boolean flip){
        if(flip){
            if(Keyboard.isKeyDown(spamKeycode)){
                globalFlip = !globalFlip;
                Wrapper.instance.log("Spamming " + Keyboard.getKeyName(spamKeycode));
                KeyBinding.setKeyBindState(spamKeycode, globalFlip);
            }
        }else{
            if(Keyboard.isKeyDown(spamKeycode)){
                Wrapper.instance.log("Spamming " + Keyboard.getKeyName(spamKeycode));
                KeyBinding.onTick(spamKeycode);
            }
        }
    }

}
