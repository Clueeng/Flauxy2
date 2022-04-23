package uwu.flauxy.module.impl.display;


import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.ColorUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "ArrayList", displayName = "ArrayList", key = 0, cat = Category.Display)
public class ArrayList extends Module {

    public ModeSetting color = new ModeSetting("Color", "Default", "Astolfo", "Default", "Rainbow", "Custom");

    public NumberSetting red = new NumberSetting("Red", 125, 0, 255, 1);
    public NumberSetting green = new NumberSetting("Green", 185, 0, 255, 1);
    public NumberSetting blue = new NumberSetting("Blue", 25, 0, 255, 1);
    BooleanSetting customfont = new BooleanSetting("Custom Font", true);
    //public BooleanSetting glow = new BooleanSetting("Glow", true);

    public BooleanSetting barLeft = new BooleanSetting("Left Bar", true);
    public BooleanSetting barRight = new BooleanSetting("Right Bar", true);

    public NumberSetting line_width = new NumberSetting("Line Width", 1, 0, 5, 1);
    public ArrayList() {
        addSettings(color, red, green, blue, barLeft, barRight, line_width, customfont);
    }

    public void onEvent(Event event) {

        if(event instanceof EventRender2D){
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
            int stringColor = -1;

            TTFFontRenderer font = Flauxy.INSTANCE.getFontManager().getFont("auxy 21");
            int retarded = 0;
            for(Module m : getFontSortedModules(font, false)){
                switch(color.getMode()){
                    case "Astolfo":{
                        stringColor = ColorUtils.astolfo(5, 0.8f, 1, cn * 20L);
                        break;
                    }
                    case "Rainbow":{
                        stringColor = ColorUtils.getRainbow(3, 0.40f, 1, cn * 180L);
                        break;
                    }
                    case "Custom":{
                        stringColor = new Color((int) red.getValue(), (int) blue.getValue(), (int) green.getValue()).getRGB();
                        break;
                    }
                }
                double wi = sr.getScaledWidth();
                int placeX = 0;
                if(barRight.getValue()){
                    Gui.drawRect((float) (wi - (float)line_width.getValue()), (float) c, (float) wi, c + font.getHeight(m.getDisplayName())+retarded+1, stringColor);
                    placeX = 2;
                }
                if(barLeft.getValue()){
                    float wa = (float)wi - font.getWidth(m.getDisplayName()) - 6 - placeX;
                    Gui.drawRect(wa, (float) c, (float) wa + (float)line_width.getValue(), c + font.getHeight(m.getDisplayName())+retarded+1, stringColor);
                }

                if (customfont.isEnabled()) {
                    font.drawStringWithShadow(m.getDisplayName(), (float) (wi - font.getWidth(m.getDisplayName()) - 2  ) - placeX, c, stringColor);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(m.getDisplayName(), (float) (wi - font.getWidth(m.getDisplayName()) - 2  ) - placeX, c, stringColor);
                }
                //Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(m.getName(), (int) x  -Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(m.getName()) + 2 , (int) y, stringcolor);

                c+=font.getHeight(m.getDisplayName())+retarded+1;
                cn+=1;
                GlStateManager.resetColor();

            }
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
