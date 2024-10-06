package uwu.noctura.module.impl.display;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.ColorUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "ArrayList", displayName = "ArrayList", key = 0, cat = Category.Display)
public class ArrayList extends Module {

    public ModeSetting color = new ModeSetting("Color", "Default", "Astolfo", "Default", "Rainbow", "Custom", "Blend", "Theme");
    public ModeSetting themes = new ModeSetting("Theme","Cotton Candy","Cotton Candy", "Sunset").setCanShow(m -> color.is("Theme"));
    public ModeSetting animAlgo = new ModeSetting("Animation","Lerp","Lerp", "Quad");
    public NumberSetting animSpeed = new NumberSetting("Animation Speed", 7, 1, 10, 1); // divide by 20

    //public NumberSetting red = new NumberSetting("Red", 194, 0, 255, 1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    //public NumberSetting green = new NumberSetting("Green", 82, 0, 255, 1).setCanShow((m) -> color.is("Custom") ||  color.is("Blend"));
    //public NumberSetting blue = new NumberSetting("Blue", 226, 0, 255, 1).setCanShow((m) -> color.is("Custom") ||  color.is("Blend"));
    //public NumberSetting red2 = new NumberSetting("Red 2", 228, 0, 255, 1).setCanShow((m) ->  color.is("Blend"));
    //public NumberSetting green2 = new NumberSetting("Green 2", 139, 0, 255, 1).setCanShow((m) ->  color.is("Blend"));
    //public NumberSetting blue2 = new NumberSetting("Blue 2", 243, 0, 255, 1).setCanShow((m) -> color.is("Blend"));
    public NumberSetting hue1 = new NumberSetting("Color 1",0,0,360,1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    public GraphSetting sat1 = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    public NumberSetting hue2 = new NumberSetting("Color 2",0,0,360,1).setCanShow((m) -> color.is("Blend"));
    public GraphSetting sat2 = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue2).setCanShow((m) -> color.is("Blend"));

    public NumberSetting offset = new NumberSetting("Offset", 2, 0, 10, 1).setCanShow((m) -> color.is("Blend") || color.is("Theme"));
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
        hue1.setColorDisplay(true);
        hue2.setColorDisplay(true);
        sat1.setColorDisplay(true);
        sat2.setColorDisplay(true);
        addSettings(color, themes, animAlgo, animSpeed, line_width, padding, customfont, hue1, sat1, hue2, sat2, offset, barLeft, barRight, outline, background, background_opacity);
    }

    @Override
    public void onEventIgnore(Event e) {

    }
    public int stringColor = -1;

    float wtf = 0;

    public void onEvent(Event event) {

        if(event instanceof EventRender2D){
            ScaledResolution sr = new ScaledResolution(mc);
            java.util.ArrayList<Module> mods = new java.util.ArrayList<Module>();
            TTFFontRenderer font = Noctura.INSTANCE.getFontManager().getFont("Good 18");
            double animFactor = 100;
            for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
                if (m.isToggled()) {
                    switch (animAlgo.getMode()){
                        case "Lerp":{
                            m.xSlide = (float) MathHelper.lerp(animSpeed.getValue() / animFactor, m.xSlide,80f);
                            break;
                        }
                        case "Quad":{
                            animFactor = 40;
                            m.xSlide = (float) MathHelper.easeInOutQuad(animSpeed.getValue() / animFactor, m.xSlide,80f);
                            break;
                        }
                    }
                }
                if (!m.isToggled()) {
                    switch (animAlgo.getMode()){
                        case "Lerp":{
                            m.xSlide = (float) MathHelper.lerp(animSpeed.getValue() / animFactor, m.xSlide,customfont.isEnabled() ? 80 - font.getWidth(m.getDisplayName()) - 8 : 80 - mc.fontRendererObj.getStringWidth(m.getDisplayName()) - 6);
                            break;
                        }
                        case "Quad":{
                            m.xSlide = (float) MathHelper.easeInOutQuad(animSpeed.getValue() / animFactor, m.xSlide,customfont.isEnabled() ? 80 - font.getWidth(m.getDisplayName()) - 8 : 80 - mc.fontRendererObj.getStringWidth(m.getDisplayName()) - 6);
                            break;
                        }
                    }
                }
                if (m.xSlide > 80 - font.getWidth(m.getDisplayName()) - 4 + padding.getValue()) {
                    mods.add(m);
                }
            }
            if(customfont.getValue()){
                mods.sort(Comparator.comparingDouble(m ->  (double) Noctura.INSTANCE.getFontManager().getFont("Good 18").getWidth(((Module) m).getDisplayName())).reversed());
            }else{
                mods.sort(Comparator.comparingInt(m ->  (int)mc.fontRendererObj.getStringWidth(((Module) m).getDisplayName())).reversed());
            }
            int c = 2;
            int cn = 0;
            stringColor = -1;
            int retarded = 2; // hjeight


            float expandedLeft = 4.0f;

            for(Module m : mods){
                float lengthOfMod = customfont.getValue() ? font.getWidth(m.getDisplayName()) : mc.fontRendererObj.getStringWidth(m.getDisplayName());
                lengthOfMod = customfont.getValue() ? 74 : 74;
                double wi = sr.getScaledWidth();
                float wa = customfont.getValue() ? (float) ((float)wi - font.getWidth(m.getDisplayName()) - padding.getValue()) :
                        (float) (wi - mc.fontRendererObj.getStringWidth(m.getDisplayName()) - padding.getValue());

                // background
                if(background.getValue()){
                    Gui.drawRect(0, 0, 0, 0, new Color(0, 0, 0, 1).getRGB());
                    if(customfont.getValue()){
                        Gui.drawRect((float) ((wi - font.getWidth(m.getDisplayName())) - m.xSlide) - (float)padding.getValue() + 8 - 2 + lengthOfMod - expandedLeft, (((float) c + (float)padding.getValue() + (font.getHeight(m.getDisplayName()))) - m.ySlide) + 1.5f, (float) (wi) - m.xSlide - (float)padding.getValue() + 8 + lengthOfMod, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 1.5f, new Color(0, 0, 0, (int)background_opacity.getValue()).getRGB());
                    }else{
                        FontRenderer fonta = mc.fontRendererObj;
                        Gui.drawRect((float) ((wi - fonta.getStringWidth(m.getDisplayName())) - m.xSlide) - (float)padding.getValue() + 8 - 2 + lengthOfMod - expandedLeft, ((float) c + (float)padding.getValue() + (fonta.FONT_HEIGHT)) - m.ySlide, (float) (wi) - m.xSlide - (float)padding.getValue() + 8 + lengthOfMod, (c + (fonta.FONT_HEIGHT * 2)+retarded+(float)padding.getValue()) - m.ySlide, new Color(0, 0, 0, (int)background_opacity.getValue()).getRGB());
                    }
                }
                // Color
                switch(color.getMode()){
                    case "Blend":{
                        Color col1 = getColorFromSettings(hue1,sat1);
                        Color col2 = getColorFromSettings(hue2,sat2);
                        int off = (int) (offset.getValue() * 75);
                        stringColor = ColorUtils.blendThing(2F, (long) (cn * off), col1, col2);
                        break;
                    }
                    case "Theme":{
                        switch (themes.getMode()){
                            case "Cotton Candy":{
                                Color col1 = new Color(228, 117, 230);
                                Color col2 = new Color(162, 161, 230);
                                int off = (int) (offset.getValue() * 75);
                                stringColor = ColorUtils.blendThing(2F, (long) (cn * off), col1, col2);
                                break;
                            }
                            case "Sunset":{
                                Color col1 = new Color(170, 67, 223);
                                Color col2 = new Color(197, 81, 35);
                                Color col3 = new Color(173, 61, 38);
                                int off = (int) (offset.getValue() * 75) * 2;
                                stringColor = ColorUtils.blendThing(2F, (long) (cn * off), col1, col2);
                                stringColor = ColorUtils.blendMultiple(2f, (cn * off), col1, col2, col3).getRGB();
                                break;
                            }
                        }
                        break;
                    }
                    case "Rainbow":{
                        stringColor = ColorUtils.getRainbow(3, 0.40f, 1, cn * 180L);
                        break;
                    }
                    case "Custom":{
                        stringColor = getColorFromSettings(hue1,sat1).getRGB();
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
                                Gui.drawRect((float) ((float) (wi - (float)line_width.getValue()) - (float)padding.getValue()) + lengthOfMod - expandedLeft, (float) c + (float)padding.getValue() - 4, (float) ((float) wi - padding.getValue()) + lengthOfMod, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, stringColor);
                            }
                            if(barLeft.getValue()){
                                Gui.drawRect(wa - m.xSlide + 6 + lengthOfMod- expandedLeft, (float) c + (float)padding.getValue() - 3, (float) (wa + (float)line_width.getValue()) - m.xSlide + 4 + lengthOfMod, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide, stringColor);
                            }
                        }else{
                            if(barRight.getValue()){ //                                                                                                       (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+8) - m.ySlide
                                Gui.drawRect((float) ((float) (wi - (float)line_width.getValue()) - padding.getValue()) + lengthOfMod, (float) ((float) c + (float)padding.getValue()) - 5f, (float) ((float) wi - padding.getValue())  + lengthOfMod, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, stringColor);
                            }
                            if(barLeft.getValue()){
                                Gui.drawRect(wa - m.xSlide + lengthOfMod- expandedLeft + 6, (float) (((float) c + (font.getHeight(m.getDisplayName()))) - m.ySlide + padding.getValue()), (float) (wa + (float)line_width.getValue()) - m.xSlide + lengthOfMod, (float) ((c + (font.getHeight(m.getDisplayName()) * 2)+retarded) - m.ySlide + padding.getValue()), stringColor);
                            }
                        }
                    }else{
                        if(customfont.getValue()){
                            Gui.drawRect(wa - m.xSlide + 4  + lengthOfMod + 1- expandedLeft, (float) c + (float)padding.getValue() - 1, (float) (wa + (float)line_width.getValue()) - m.xSlide + 4 + lengthOfMod + 1- expandedLeft, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f, stringColor);
                            // Bar below the fucking module
                            float leftx =  wa - m.xSlide + lengthOfMod + 4;
                            float bottomStart = (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2.5f;
                            float width = (float) ((float) (wi - (float)line_width.getValue()) - padding.getValue()) + lengthOfMod;
                            //Wrapper.instance.log(mods.indexOf(m) + " " + m.getName());
                            if(mods.indexOf(m) == mods.size()-1){
                                // Last bar
                                Gui.drawRect(leftx + 1- expandedLeft, bottomStart - 1, leftx + font.getWidth(m.getDisplayName()) + 3f, bottomStart, stringColor);
                            }else{
                                int nextModInd = mods.indexOf(m) + 1;
                                Module nextMod = mods.get(nextModInd);
                                float lengthNM = font.getWidth(nextMod.getDisplayName());
                                float adding = font.getWidth(m.getDisplayName()) - lengthNM - 4;
                                Gui.drawRect(leftx + 1- expandedLeft, bottomStart -1, leftx + adding + 2, bottomStart, stringColor);
                            }

                            // Top Bar
                            if(mods.indexOf(m) == 0){
                                Gui.drawRect(wa - m.xSlide + 4 + lengthOfMod + 1- expandedLeft, (float) c + (float)padding.getValue() - 2, leftx + font.getWidth(m.getDisplayName()) + 7 - expandedLeft, (float) c + (float)padding.getValue() - 1, stringColor);
                            }
                            // Right bar
                            Gui.drawRect(leftx + font.getWidth(m.getDisplayName()) + 3, (float) c + (float)padding.getValue() - 2, leftx + font.getWidth(m.getDisplayName()) + 4, (c + (font.getHeight(m.getDisplayName()) * 2)+retarded+(float)padding.getValue()) - m.ySlide + 2f, stringColor);

                        }else{

                            // Left bar
                            Gui.drawRect(wa - m.xSlide + 4  + lengthOfMod + 1- expandedLeft, (float) c + (float)padding.getValue() - 3, (float) (wa + (float)line_width.getValue()) - m.xSlide + 4 + lengthOfMod + 1- expandedLeft, (c + (mc.fontRendererObj.FONT_HEIGHT * 2)+retarded+(float)padding.getValue()) - m.ySlide + .5f, stringColor);
                            // Bar below the fucking module
                            float leftx =  wa - m.xSlide + lengthOfMod + 4;
                            float bottomStart = (c + (mc.fontRendererObj.FONT_HEIGHT * 2)+retarded+(float)padding.getValue()) - m.ySlide + .5f;
                            float width = (float) ((float) (wi - (float)line_width.getValue()) - padding.getValue()) + mc.fontRendererObj.getStringWidth(m.getDisplayName());
                            //Wrapper.instance.log(mods.indexOf(m) + " " + m.getName());
                            if(mods.indexOf(m) == mods.size()-1){
                                // Last bar below
                                Gui.drawRect(leftx + 1- expandedLeft, bottomStart - 1, leftx + mc.fontRendererObj.getStringWidth(m.getDisplayName()) + 4, bottomStart, stringColor);
                            }else{
                                int nextModInd = mods.indexOf(m) + 1;
                                Module nextMod = mods.get(nextModInd);
                                float lengthNM = mc.fontRendererObj.getStringWidth(nextMod.getDisplayName());
                                float adding = mc.fontRendererObj.getStringWidth(m.getDisplayName()) - lengthNM;
                                Gui.drawRect(leftx + 1- expandedLeft, bottomStart -1, leftx + adding + 2- expandedLeft, bottomStart, stringColor);
                            }

                            // Top Bar
                            if(mods.indexOf(m) == 0){
                                Gui.drawRect(wa - m.xSlide + 4 + lengthOfMod + 1- expandedLeft, (float) c + (float)padding.getValue() - 4, leftx + mc.fontRendererObj.getStringWidth(m.getDisplayName()) + 5, (float) c + (float)padding.getValue() - 3, stringColor);
                            }
                            // Right bar
                            Gui.drawRect(leftx + mc.fontRendererObj.getStringWidth(m.getDisplayName()) + 4, (float) c + (float)padding.getValue() - 3, leftx + mc.fontRendererObj.getStringWidth(m.getDisplayName()) + 5, (c + (mc.fontRendererObj.FONT_HEIGHT * 2)+retarded+(float)padding.getValue()) - m.ySlide + .5f, stringColor);
                        }
                    }

                    // outline end

                    // text
                    if(customfont.isEnabled()) font.drawStringWithShadow(m.getDisplayName(), (float) (wi - font.getWidth(m.getDisplayName()) - (float)padding.getValue()) + 7f - m.xSlide + lengthOfMod - (expandedLeft / 2), (((float) c + (float)padding.getValue() + (font.getHeight(m.getDisplayName()))) - m.ySlide) + 2.0f, stringColor);
                    else mc.fontRendererObj.drawStringWithShadow(m.getDisplayName(), (float) (wi - mc.fontRendererObj.getStringWidth(m.getDisplayName()) - (float)padding.getValue()) + 7 - m.xSlide + lengthOfMod - (expandedLeft / 2), (((float) c + (float)padding.getValue() + (font.getHeight(m.getDisplayName()))) - m.ySlide) + 1.5f, stringColor);
                    // values changing

                    c+= (int) (m.ySlide - 0.12f);
                    if(!m.isToggled()){
                        switch (animAlgo.getMode()){
                            case "Lerp":{
                                m.ySlide = (float) MathHelper.lerp(animSpeed.getValue() / animFactor, m.ySlide,0);
                                break;
                            }
                            case "Quad":{
                                m.ySlide = (float) MathHelper.easeInOutQuad(animSpeed.getValue() / animFactor, m.ySlide,0);
                                break;
                            }
                        }
                    }else{
                        switch (animAlgo.getMode()){
                            case "Lerp":{
                                m.ySlide = (float) MathHelper.lerp(animSpeed.getValue() / animFactor, m.ySlide,font.getHeight(m.getDisplayName())+retarded+1);
                                break;
                            }
                            case "Quad":{
                                m.ySlide = (float) MathHelper.easeInOutQuad(animSpeed.getValue() / animFactor, m.ySlide,font.getHeight(m.getDisplayName())+retarded+1);
                                break;
                            }
                        }
                    }
                    // X
                    if(!m.isToggled()){

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
        for (Module mod : Noctura.INSTANCE.getModuleManager().modules) {
            //if (mod.isToggled()) {
                modds.add(mod);
            //}
        }
        return modds;
    }
    public static java.util.List<Module> getModules() {
        List<Module> modds = new java.util.ArrayList<>();
        for (Module mod : Noctura.INSTANCE.getModuleManager().modules) {
            modds.add(mod);
        }
        return modds;
    }
}
