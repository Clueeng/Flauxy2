package uwu.noctura.module.impl.visuals;

import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

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
