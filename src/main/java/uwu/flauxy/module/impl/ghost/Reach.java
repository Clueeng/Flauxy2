package uwu.flauxy.module.impl.ghost;

import net.minecraft.client.gui.ScaledResolution;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.font.FontManager;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;

@ModuleInfo(name = "Reach", displayName = "Reach", key = -1, cat = Category.Ghost)
public class Reach extends Module {

    public NumberSetting range = new NumberSetting("Reach", 3.0, 3.0, 6.0, 0.05);
    public NumberSetting blockRange = new NumberSetting("Block Reach", 3.0, 3.0, 12.0, 0.1);

    public Reach(){
        addSettings(range, blockRange);
    }

    @Override
    public void onEnable() {

    }
    public static double currentRange = 3.0d;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            currentRange = range.getValue();
        }
    }

    @Override
    public void onDisable() {
        currentRange = 3.0d;
    }
}
