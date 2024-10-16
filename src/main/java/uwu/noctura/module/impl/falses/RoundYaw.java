package uwu.noctura.module.impl.falses;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "RoundYaw", displayName = "Round Yaw", cat = Category.False, key = -1)
public class RoundYaw extends Module {

    NumberSetting modulo = new NumberSetting("Modulo (%)", 1, 0, 360, 1);
    NumberSetting increased = new NumberSetting("Increase By", 1, 0, 55, 1);

    public RoundYaw(){
        addSettings(modulo, increased);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            int mouseX = mc.mouseHelper.deltaX;
            if(Math.abs(mouseX) < 10){
                float moduloValue = (float) modulo.getValue();
                float increaseValue = (float) increased.getValue();
                float roundedYaw = Math.round(mc.thePlayer.rotationYaw / moduloValue) * moduloValue;
                if (mc.thePlayer.rotationYaw != roundedYaw + increaseValue) {
                    mc.thePlayer.rotationYaw = roundedYaw + (increaseValue);
                }
            }
        }
    }
}
