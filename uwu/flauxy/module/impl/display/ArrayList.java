package uwu.flauxy.module.impl.display;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.shader.impl.GlowUtil;

import java.util.Comparator;

@ModuleInfo(name = "ArrayList", displayName = "ArrayList", key = 0, cat = Category.Display)
public class ArrayList extends Module {

    public ModeSetting color = new ModeSetting("Color", "Default", "Astolfo", "Default");
    public BooleanSetting glow = new BooleanSetting("Glow", true);

    public ArrayList() {
        addSettings(color, glow);
    }

    @EventTarget
    public void onRender(EventRender2D event) {

        ScaledResolution sr = new ScaledResolution(mc);
        java.util.ArrayList<Module> mods = new java.util.ArrayList<Module>();

        float y = 1;
        int count = 0;

        for (Module m : Flauxy.INSTANCE.getModuleManager().modules) {
            mods.add(m);
        }

        mods.sort(Comparator.comparingInt(m ->  (int) Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(((Module) m).getName())).reversed());

        for (Module m : mods) {
            if(m.isToggled()) {
                int stringcolor = 0;
                switch (color.getMode()) {
                    case "Astolfo":
                        stringcolor = ColorUtils.rainbow(count * -100, 1.0f, 0.47f);
                        break;
                    case "Default":
                        stringcolor = -1;
                        break;
                }


                float x = sr.getScaledWidth() - Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(m.getName()) - 3;
                if (glow.isEnabled()) {
                    float finalY = y;
                    int finalStringcolor = stringcolor;
                    GlowUtil.drawAndBloom(() -> Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(m.getName(), (int) x - Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(m.getName()) + 2 , (int) finalY, finalStringcolor));
                }else {
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(m.getName(), (int) x  -Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(m.getName()) + 2 , (int) y, stringcolor);
                }

                count++;
                y += 9.7;
            }
        }
    }
}
