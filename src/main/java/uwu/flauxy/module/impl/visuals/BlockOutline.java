package uwu.flauxy.module.impl.visuals;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender3D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.render.RenderUtil;


@ModuleInfo(name = "BlockOutline", displayName = "Block Outline", key = -1, cat = Category.Visuals)
public class BlockOutline extends Module {

    public NumberSetting red = new NumberSetting("Red", 1,0,255,1);
    public NumberSetting green = new NumberSetting("Green", 1,0,255,1);
    public NumberSetting blue = new NumberSetting("Blue", 1,0,255,1);
    public NumberSetting opacity = new NumberSetting("Opacity", 1,0,100,1);
    public NumberSetting linewidth = new NumberSetting("Line Width", 1,0,5,1);
    public BooleanSetting fullblock = new BooleanSetting("Full Block", true);

    public BlockOutline(){
        addSettings(red, green, blue, opacity, linewidth, fullblock);
    }

    @Override
    public void onEvent(Event e) {

    }
}
