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
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.shader.impl.GlowUtil;

import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "ArrayList", displayName = "ArrayList", key = 0, cat = Category.Display)
public class ArrayList extends Module {

    public ModeSetting color = new ModeSetting("Color", "Default", "Astolfo", "Default");
    //public BooleanSetting glow = new BooleanSetting("Glow", true);

    public ArrayList() {
        addSettings(color);
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

        /*for (Module m : mods) {
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
        }*/
        int c = 0;
        int cn = 0;
        TTFFontRenderer font = Flauxy.INSTANCE.getFontManager().getFont("auxy 21");
        int retarded = 0;
        int stringColor = -1;
        for(Module m : getFontSortedModules(font, false)){
            double wi = sr.getScaledWidth();

            font.drawStringWithShadow(m.getDisplayName(), (float) (wi - font.getWidth(m.getDisplayName()) - 2  ), c, stringColor);
            //Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(m.getName(), (int) x  -Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(m.getName()) + 2 , (int) y, stringcolor);
            c+=font.getHeight(m.getName())+retarded+1;
            cn+=1;

        }
    }

    private static java.util.List<Module> getFontSortedModules(TTFFontRenderer fr, boolean lowerCase) {
        java.util.List<Module> sortedList = getActiveModules();
        sortedList.sort(Comparator.comparingDouble(e -> lowerCase ? -fr.getWidth(e.getDisplayName().toLowerCase()) : -fr.getWidth(e.getDisplayName())));
        return sortedList;
    }

    public static java.util.List<Module> getActiveModules() {
        java.util.List<Module> modds = new java.util.ArrayList<>();
        for (Module mod : Flauxy.INSTANCE.getModuleManager().modules) {
            if (mod.isToggled()) {
                modds.add(mod);
            }
        }
        return modds;
    }
    public static java.util.List<Module> getModules() {
        List<Module> modds = new java.util.ArrayList<>();
        for (Module mod : Flauxy.INSTANCE.getModuleManager().modules) {
            modds.add(mod);
        }
        return modds;
    }
}
