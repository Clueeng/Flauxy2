package uwu.flauxy.module.impl.visuals;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender3D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.render.RenderUtil;

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
