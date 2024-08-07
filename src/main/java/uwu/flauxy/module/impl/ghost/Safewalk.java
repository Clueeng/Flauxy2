package uwu.flauxy.module.impl.ghost;

import net.minecraft.entity.Entity;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Safewalk", displayName = "Safewalk", key = -1, cat = Category.Ghost)
public class Safewalk extends Module {

    public BooleanSetting sneak = new BooleanSetting("Sneak", true);
    //public NumberSetting range = new NumberSetting("Reach", 3.0, 3.0, 6.0, 0.05);
    private Timer timer = new Timer();
    boolean runTimer = false;


    public Safewalk(){
        addSettings(sneak);
    }

    @Override
    public void onEnable() {

    }
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if(mc.thePlayer.rotationPitch < 60) return;
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
