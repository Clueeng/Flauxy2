package uwu.noctura.module.impl.visuals;

import net.minecraft.client.gui.ScaledResolution;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.render.RenderUtil;

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
                x += 16;
                int expand = 8;
                RenderUtil.drawRoundedRect2(x - expand, y - expand,x + expand + mc.fontRendererObj.getStringWidth("Aiming at " + mc.pointedEntity.getName()),
                        y + expand + mc.fontRendererObj.FONT_HEIGHT, 3,new Color(0, 0, 0, 120).getRGB());
                mc.fontRendererObj.drawStringWithShadow("Aiming at " + mc.pointedEntity.getName(), x, y, Color.red.getRGB());
            }
        }
    }
}
