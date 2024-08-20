package uwu.flauxy.module.impl.falses;

import net.minecraft.client.renderer.EntityRenderer;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Zoom",  displayName = "Zoom", cat = Category.False, key = -1)
public class Zoom extends Module {

    public BooleanSetting hold = new BooleanSetting("Hold For Zoom",true);
    public NumberSetting zoomSpeed = new NumberSetting("Zoom Speed",1,0.1f,10f,0.1f);
    public NumberSetting zoomFactor = new NumberSetting("Zoom Factor",1,0.25f,12,0.25f);
    public BooleanSetting smoothZoom = new BooleanSetting("Smooth Zoom",true);
    public ModeSetting smoothFunction = new ModeSetting("Smooth Function","Lerp", "Lerp", "Quad", "Ease in");

    public Zoom(){
        addSettings(hold, zoomSpeed, zoomFactor, smoothZoom, smoothFunction);
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
