package uwu.noctura.module.impl.falses;

import net.minecraft.client.renderer.EntityRenderer;
import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

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
