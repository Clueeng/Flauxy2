package uwu.noctura.module.impl.ghost;

import net.minecraft.entity.Entity;
import org.lwjgl.input.Mouse;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.utils.MoveUtils;
import uwu.noctura.utils.timer.Timer;

@ModuleInfo(name = "Safewalk", displayName = "Safewalk", key = -1, cat = Category.Ghost)
public class Safewalk extends Module {

    public BooleanSetting sneak = new BooleanSetting("Sneak", true);
    public BooleanSetting requireClick = new BooleanSetting("On Click",true);
    //public NumberSetting range = new NumberSetting("Reach", 3.0, 3.0, 6.0, 0.05);
    private Timer timer = new Timer();
    boolean runTimer = false;


    public Safewalk(){
        addSettings(sneak, requireClick);
    }

    @Override
    public void onEnable() {

    }
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if(mc.thePlayer.rotationPitch < 60 || (requireClick.isEnabled() && !Mouse.isButtonDown(1))) return;
            if (sneak.getValue()) {
                if(Entity.sneak2){
                    mc.gameSettings.keyBindSneak.pressed = true;
                    // Pressing sneak key
                    if(MoveUtils.isWalking()){
                        runTimer = MoveUtils.getMotion() > 0.03f;
                    }
                }
                if (runTimer) {
                    if (timer.hasTimeElapsed(100, true)) {
                        mc.gameSettings.keyBindSneak.pressed = (false); // Releasing sneak keyS
                        Entity.sneak2 = false;
                        runTimer = false;
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
    }
}
