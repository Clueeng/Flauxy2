package uwu.flauxy.module.impl.other;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

@ModuleInfo(name = "ModernInventory", displayName = "Modern Inventory", cat = Category.Other, key = -1)
public class ModernInventory extends Module {
    boolean closedInventory, wasInScreen;
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(ev.isPre()){
                if(mc.currentScreen != null){
                    wasInScreen = true;
                }else{
                    if(wasInScreen){
                        closedInventory = true;
                    }
                    wasInScreen = false;
                }
                if(closedInventory){
                    mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
                    mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
                    mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
                    mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
                    mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
                    mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
                    closedInventory = false;
                }
            }
        }
    }
}
