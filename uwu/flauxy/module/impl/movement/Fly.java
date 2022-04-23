package uwu.flauxy.module.impl.movement;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Verus", "Hypixel", "Funcraft", "Vanilla");
    NumberSetting speed = new NumberSetting("Speed", 4.2, 0.1, 6, 0.1);
    public ModeSetting verusMode = new ModeSetting("Verus Mode", "Damage", "Damage", "Normal").setCanShow((m) -> mode.is("Verus"));

    double posYLOL = 0;
    int ticks = 0;

    @Override
    public void onEnable() {
        posYLOL = mc.thePlayer.posY;
    }

    public Fly(){
        addSettings(mode);
        addSettings(speed);
        addSettings(verusMode);
    }

    @Override
    public void onEventIgnore(Event e) {
    }
    public static double roundToOnGround(final double posY) {
        return posY - (posY % 0.015625);
    }
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){
            EventMotion event = (EventMotion)e;
            switch(mode.getMode()){
                case "Verus":{
                    switch(verusMode.getMode()){
                        case "Normal":{
                            MoveUtils.strafe();
                            ticks++;
                            break;
                        }
                    }
                    break;
                }

                case "Vanilla":{

                    break;
                }
            }
        }
    }
}
