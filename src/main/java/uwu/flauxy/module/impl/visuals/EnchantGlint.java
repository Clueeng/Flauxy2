package uwu.flauxy.module.impl.visuals;

import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

import java.awt.*;

@ModuleInfo(name = "EnchantGlint", displayName = "Enchant Glint", key = -1, cat = Category.Visuals)
public class EnchantGlint extends Module {


    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting satBright = new GraphSetting("Saturation", 100,100,0,100,0,100,1,1, hue);
    public BooleanSetting allItem = new BooleanSetting("All Item",false);

    public EnchantGlint(){
        hue.setColorDisplay(true);
        satBright.setColorDisplay(true);
        addSettings(hue, satBright, allItem);
    }

    public Color getColor(){
        return getColorFromSettings(hue,satBright);
    }



}
