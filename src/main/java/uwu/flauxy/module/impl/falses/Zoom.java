package uwu.flauxy.module.impl.falses;

import net.minecraft.client.renderer.EntityRenderer;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;

@ModuleInfo(name = "Zoom",  displayName = "Zoom", cat = Category.False, key = -1)
public class Zoom extends Module {

    public BooleanSetting hold = new BooleanSetting("Hold For Zoom",true);

    public Zoom(){
        addSettings(hold);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(ev.getType().equals(EventType.PRE)){
                if(mc.gameSettings.ofKeyBindZoom.isPressed() && !hold.getValue()){
                    EntityRenderer.toggleZoom = !EntityRenderer.toggleZoom;
                }
            }
        }
    }
}
