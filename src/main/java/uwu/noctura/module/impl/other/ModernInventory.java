package uwu.noctura.module.impl.other;

import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

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
