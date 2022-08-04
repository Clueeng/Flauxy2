package uwu.flauxy.module.impl.display;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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

    public ModeSetting color = new ModeSetting("Color", "Default", "Astolfo", "Default", "Rainbow", "Custom", "Blend");

    public NumberSetting red = new NumberSetting("Red", 194, 0, 255, 1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    public NumberSetting green = new NumberSetting("Green", 82, 0, 255, 1).setCanShow((m) -> color.is("Custom") ||  color.is("Blend"));
    public NumberSetting blue = new NumberSetting("Blue", 226, 0, 255, 1).setCanShow((m) -> color.is("Custom") ||  color.is("Blend"));
    public NumberSetting red2 = new NumberSetting("Red 2", 228, 0, 255, 1).setCanShow((m) ->  color.is("Blend"));
    public NumberSetting green2 = new NumberSetting("Green 2", 139, 0, 255, 1).setCanShow((m) ->  color.is("Blend"));
    public NumberSetting blue2 = new NumberSetting("Blue 2", 243, 0, 255, 1).setCanShow((m) -> color.is("Blend"));
    public NumberSetting offset = new NumberSetting("Offset", 2, 0, 10, 1).setCanShow((m) -> color.is("Blend"));
    BooleanSetting customfont = new BooleanSetting("Custom Font", true);
    //public BooleanSetting glow = new BooleanSetting("Glow", true);
    public BooleanSetting outline = new BooleanSetting("Outline", false);

    public BooleanSetting barLeft = new BooleanSetting("Left Bar", false).setCanShow((m) -> !outline.getValue());
    public BooleanSetting barRight = new BooleanSetting("Right Bar", true).setCanShow((m) -> !outline.getValue());


    public NumberSetting padding = new NumberSetting("Padding", 0, 0, 20, 1);


    public NumberSetting line_width = new NumberSetting("Line Width", 1, 0, 5, 1);


    public BooleanSetting background = new BooleanSetting("Background", true);
    public NumberSetting background_opacity = new NumberSetting("Background opacity", 90, 0, 255, 1).setCanShow(m -> background.getValue());
    public ArrayList() {
        addSettings(color, line_width, padding, customfont, red, green, blue, red2, green2, blue2, offset, barLeft, barRight, outline, background, background_opacity);
    }

    @Override
    public void onEventIgnore(Event e) {

    }

    float wtf = 0;

    public void onEvent(Event event) {

        if(event instanceof EventRender2D){
            ScaledResolution sr = new ScaledResolution(mc);
            java.util.ArrayList<Module> mods = new java.util.ArrayList<Module>();

            float y = 1;
            int count = 0;

            TTFFontRenderer font = Flauxy.INSTANCE.getFontManager().getFont("auxy 19");
            for (Module m : Flauxy.INSTANCE.getModuleManager().modules) {
                if (m.isToggled()) {
                    m.xSlide += (0.9f * (60F / (float) Minecraft.getDebugFPS()) * (font.getWidth(m.getDisplayName()) / 4));
                    if (m.xSlide > 8) {
                        m.xSlide = 8;
                    }

                }
                if (!m.isToggled()) {
                    m.xSlide -= 0.28f * (60F / (float) Minecraft.getDebugFPS()) * (font.getWidth(m.getDisplayName()) / 4);
                    if (m.xSlide < 0) {
                        m.xSlide = 0;
                    }

                    if(m.ySlide > 0) m.ySlide -= 0.18f * (font.getWidth(m.getDisplayName()) / 4);
                    else m.ySlide = 0f;
                }
                if (m.xSlide > 0F) {
                    mods.add(m);
                }
            }
            if(customfont.getValue()){
                mods.sort(Comparator.comparingInt(m ->  (int) Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(((Module) m).getDisplayName())).reversed());
            }else{
                mods.sort(Comparator.comparingInt(m ->  (int)mc.fontRendererObj.getStringWidth(((Module) m).getDisplayName())).reversed());
            }
            int c = 0;
            int cn = 0;
            int stringColor = -1;
            int retarded = 2;



            for(Module m : mods){
                double wi = sr.getScaledWidth();

                float wa = (float) ((float)wi - font.getWidth(m.getDisplayName()) - padding.getValue());
                // background
                if(background.getValue()){
                    if(customfont.getValue()){
                        Gui.drawRect((float) ((wi - font.getWidth(m.getDisplayName())) - m.xSlide) - (float)padding.getValue() + 8 - 2, (((float) c + (float)padding.getValue() + (font.getHeight(m.getDisplayName()))) - m.ySlide) + 2, (float) (wi) - m.xSlide - (float)padding.getValue() + 8, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, new Color(0, 0, 0, (int)background_opacity.getValue()).getRGB());
                    }else{
                        FontRenderer fonta = mc.fontRendererObj;
                        Gui.drawRect((float) ((wi - fonta.getStringWidth(m.getDisplayName())) - m.xSlide) - (float)padding.getValue() + 8 - 2, ((float) c + (float)padding.getValue() + (fonta.FONT_HEIGHT)) - m.ySlide, (float) (wi) - m.xSlide - (float)padding.getValue() + 8, (c + (fonta.FONT_HEIGHT * 2)+retarded+(float)padding.getValue()) - m.ySlide, new Color(0, 0, 0, (int)background_opacity.getValue()).getRGB());
                    }
                }


                // Color
                switch(color.getMode()){
                    case "Blend":{
                        Color col1 = new Color((int)red.getValue(),(int)green.getValue(),(int) blue.getValue());
                        Color col2 = new Color((int)red2.getValue(), (int)green2.getValue(), (int)blue2.getValue());
                        int off = (int) (offset.getValue() * 75);
                        stringColor = ColorUtils.blendThing(2F, (long) (cn * off), col1, col2);
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
                    case "Astolfo":{
                        stringColor = ColorUtils.astolfo(3, 0.45f, 1, cn * 180L);
                        break;
                    }
                }

                //actual arraylist
                //if(m.isToggled()){
                    // outline
                    if(!outline.getValue()){
                        if(customfont.getValue()){
                            if(barRight.getValue()){
                                Gui.drawRect((float) ((float) (wi - (float)line_width.getValue()) - (float)padding.getValue()), (float) c + (float)padding.getValue() - 1, (float) ((float) wi - padding.getValue()), (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, stringColor);
                            }
                            if(barLeft.getValue()){
                                Gui.drawRect(wa - m.xSlide + 4, (float) c + (float)padding.getValue() - 1, (float) (wa + (float)line_width.getValue()) - m.xSlide + 4, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, stringColor);
                            }
                        }else{
                            if(barRight.getValue()){ //                                                                                                       (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+8) - m.ySlide
                                Gui.drawRect((float) ((float) (wi - (float)line_width.getValue()) - padding.getValue()), (float) ((float) c + (float)padding.getValue()) - 0.75f - 1.5f, (float) ((float) wi - padding.getValue()), (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, stringColor);
                            }
                            if(barLeft.getValue()){
                                Gui.drawRect(wa - m.xSlide, ((float) c + 8 + (font.getHeight(m.getDisplayName()))) - m.ySlide, (float) (wa + (float)line_width.getValue()) - m.xSlide, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+8) - m.ySlide, stringColor);
                            }
                        }
                    }

                    // outline end

                    // text
                    if(customfont.isEnabled()) font.drawStringWithShadow(m.getDisplayName(), (float) (wi - font.getWidth(m.getDisplayName()) - (float)padding.getValue()) + 7 - m.xSlide, c + (float)padding.getValue() + 0, stringColor);
                    else mc.fontRendererObj.drawStringWithShadow(m.getDisplayName(), (float) (wi - mc.fontRendererObj.getStringWidth(m.getDisplayName()) - (float)padding.getValue()) + 7 - m.xSlide, c + (float)padding.getValue() + 0, stringColor);
                    // values changing

                    c+=m.ySlide - 0.12f;
                    if(m.ySlide < font.getHeight(m.getDisplayName())+retarded+1){
                        m.ySlide+=0.28f;
                    }
                    if(m.ySlide > font.getHeight(m.getDisplayName())+retarded+1){
                        m.ySlide -= 0.08f;
                    }
                    if(!m.isToggled()){
                        if(m.ySlide > 0) m.ySlide -= 0.28f;
                        else m.ySlide = 0f;
                    }
                    // X
                    if(m.xSlide < (float) (wi - font.getWidth(m.getDisplayName()) - 16) - 18){
                        m.xSlide+=0.042f;
                    }
                    if(m.xSlide > (float) (wi - font.getWidth(m.getDisplayName()) - 12)){
                        m.xSlide = (float) (wi - font.getWidth(m.getDisplayName()) - 8);
                    }
                    if(!m.isToggled()){
                        if(m.xSlide > 0) m.xSlide -= 0.09f;
                        else m.ySlide = 0f;
                    }
                    cn+=1;
                    // color reset
                //}
                GlStateManager.resetColor();
            }
        }
    }

    private static java.util.List<Module> getFontSortedModules(TTFFontRenderer fr, boolean lowerCase) {
        java.util.List<Module> sortedList = getActiveModules();
        sortedList.sort(Comparator.comparingDouble(e -> lowerCase ? -fr.getWidth(e.getDisplayName().toLowerCase()) : -fr.getWidth(e.getDisplayName())));
        return sortedList;
    }

    private static java.util.List<Module> getSortedModules(FontRenderer fr, boolean lowerCase) {
        java.util.List<Module> sortedList = getActiveModules();
        sortedList.sort(Comparator.comparingDouble(e -> lowerCase ? -fr.getStringWidth(e.getDisplayName().toLowerCase()) : -fr.getStringWidth(e.getDisplayName())));
        return sortedList;
    }

    public static java.util.List<Module> getActiveModules() {
        java.util.List<Module> modds = new java.util.ArrayList<>();
        for (Module mod : Flauxy.INSTANCE.getModuleManager().modules) {
            //if (mod.isToggled()) {
                modds.add(mod);
            //}
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
