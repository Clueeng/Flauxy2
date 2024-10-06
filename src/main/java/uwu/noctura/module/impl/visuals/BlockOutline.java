package uwu.noctura.module.impl.visuals;

import uwu.noctura.event.Event;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

import java.awt.*;


@ModuleInfo(name = "BlockOutline", displayName = "Block Outline", key = -1, cat = Category.Visuals)
public class BlockOutline extends Module {

    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting satBright = new GraphSetting("Saturation", 100,100,0,100,0,100,1,1, hue);
    public NumberSetting opacity = new NumberSetting("Opacity", 1,0,100,1);
    public NumberSetting linewidth = new NumberSetting("Line Width", 1,0,5,1);
    public BooleanSetting fullblock = new BooleanSetting("Full Block", true);

    public BlockOutline(){
        addSettings(hue, satBright, opacity, linewidth, fullblock);
        hue.setColorDisplay(true);
        satBright.setColorDisplay(true);
    }

    @Override
    public void onEvent(Event e) {

    }

    public Color getOutlineColor(){
        return getColorFromSettings(hue,satBright);
    }
}
