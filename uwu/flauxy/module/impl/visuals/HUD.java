package uwu.flauxy.module.impl.visuals;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.utils.font.FontManager;
import uwu.flauxy.utils.font.TTFFontRenderer;

@ModuleInfo(name = "HUD", displayName = "HUD", key = -1, cat = Category.VISUALS)
public class HUD extends Module {

    @EventTarget
    public void onRender(EventRender2D ev){
        //FontManager.auxy.drawString("Sex", 4, 4, -1);
        Minecraft.getMinecraft().fontRendererObj.drawString("Sex", 4, 4, -1);
        TTFFontRenderer font = Flauxy.instance.fmgr.getFont("auxy");
        font.drawString("fuck", 4, 12, -1);
    }
}
