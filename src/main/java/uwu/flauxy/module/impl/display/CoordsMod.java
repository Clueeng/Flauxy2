package uwu.flauxy.module.impl.display;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.client.gui.Gui;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;


@ModuleInfo(name = "Coords", key = -1, cat = Category.Display, displayName = "Coords")
public class CoordsMod extends Module {

    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue); // sat bri
    public BooleanSetting background = new BooleanSetting("Background",true);

    public CoordsMod(){
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(500);
        hue.setColorDisplay(true);
        saturationValue.setColorDisplay(true);
        addSettings(hue, saturationValue);
    }

    @Override
    public void onEvent(Event e) {
        Color c = getColorFromSettings(hue,saturationValue);
        if(e instanceof EventRender2D){
            int x = mc.thePlayer.getPosition().getX();
            int y = mc.thePlayer.getPosition().getY();
            int z = mc.thePlayer.getPosition().getZ();
            String coords = "X: " + x + " Y: " + y + " Z: " + z;
            int padding = 6;
            setMoveW(mc.fontRendererObj.getStringWidth(coords) + padding);
            setMoveH(mc.fontRendererObj.FONT_HEIGHT + padding);
            if(background.isEnabled()){
                RenderUtil.drawRoundedRect2(getMoveX(),getMoveY(),getMoveX() + getMoveW(),getMoveY() + getMoveH(), 4,new Color(0,0,0,90).getRGB());
            }
            mc.fontRendererObj.drawStringWithShadow(coords,getMoveX()+(padding / 2f),getMoveY()+(padding / 2f),c.getRGB());
        }
    }
}
