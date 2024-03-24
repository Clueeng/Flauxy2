package uwu.flauxy.module.impl.display;

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

@ModuleInfo(name = "GameInfo", displayName = "Game Info", key = -1, cat = Category.Display)
public class GameInfo extends Module {

    public NumberSetting x = new NumberSetting("X", 4, 0, 1920, 1);
    public NumberSetting y = new NumberSetting("y", 4, 0, 1080, 1);
    int kills;
    int seconds, minutes, hours;
    boolean shouldResetStats;
    java.util.ArrayList<GameInformations> infos = new java.util.ArrayList();

    public GameInfo(){
        addSettings(x, y);
    }

    @Override
    public void onEnable() {
        infos.clear();
        infos.add(new GameInformations("name", "Name", mc.thePlayer.getDisplayName()));
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            ScaledResolution sr = new ScaledResolution(mc);
            x.setMaximum(sr.getScaledWidth());
            y.setMaximum(sr.getScaledHeight());
            if(x.getValue() > x.getMaximum()){
                x.setValue(x.getMaximum());
            }
            if(y.getValue() > y.getMaximum()){
                y.setValue(y.getMaximum());
            }
        }

        if(e instanceof EventRender2D){

            // base rectangle
            double xpos = x.getValue();
            double ypos = y.getValue();
            int w = 140;
            int h = 12 * infos.size();
            int back =  new Color(0, 0, 0, 90).getRGB();

            RenderUtil.drawRoundedRect(xpos, ypos, w, h, 4, back);
            TTFFontRenderer font = Flauxy.INSTANCE.getFontManager().getFont("arial 19");
            for(GameInformations gi : infos){
                gi.updateValue();
                String name = gi.getName();
                String customName = gi.getCustomName();

                font.drawString(customName, (float) (xpos + 4), (float) ((ypos + 2) + (12 * infos.indexOf(gi))), -1);
            }

        }
    }
}
