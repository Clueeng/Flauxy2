package uwu.flauxy.module.impl.visuals;

import net.minecraft.client.gui.ScaledResolution;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

import java.awt.*;

@ModuleInfo(name = "PointedHUD", cat = Category.Visuals, key = -1, displayName = "Pointed HUD")
public class PointedHUD extends Module {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender2D){
            ScaledResolution sr = new ScaledResolution(mc);
            int x = sr.getScaledWidth() / 2;
            int y = sr.getScaledHeight() / 2;
            if(mc.pointedEntity != null){
                mc.fontRendererObj.drawString("Aiming at " + mc.pointedEntity.getName(), x, y, Color.red.getRGB());
            }
        }
    }
}
