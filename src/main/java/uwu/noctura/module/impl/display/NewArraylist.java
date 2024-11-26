package uwu.noctura.module.impl.display;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.ModuleManager;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.MathUtils;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.ColorUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleInfo(name = "NewArraylist", displayName = "NewArraylist", cat = Category.Display, key = -1)
public class NewArraylist extends Module {

    /*

    Settings :
        -Color
        - Animation Toggle
        - Font Renderer

        Extra
        - Hide Some Categories
     */

    // Colors

    public ModeSetting color = new ModeSetting("Color", "Default", "Default", "Custom", "Blend");
    public NumberSetting hue1 = new NumberSetting("Color 1",0,0,360,1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    public GraphSetting sat1 = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    public NumberSetting hue2 = new NumberSetting("Color 2",0,0,360,1).setCanShow((m) -> color.is("Blend"));
    public GraphSetting sat2 = new GraphSetting("Saturation 2",0,0,0,100,0,100,1,1, hue2).setCanShow((m) -> color.is("Blend"));

    // Rectangle
    public BooleanSetting outline = new BooleanSetting("Outline", true);

    // String
    public BooleanSetting customFont = new BooleanSetting("Custom Font", true);
    public BooleanSetting hideSomeCategories = new BooleanSetting("Hide Useless", true);
    public NumberSetting offset = new NumberSetting("Offset", 2, 0, 10, 1).setCanShow((m) -> color.is("Blend"));

    // Constructor
    public NewArraylist(){
        hue1.setColorDisplay(true); sat1.setColorDisplay(true);
        hue2.setColorDisplay(true); sat2.setColorDisplay(true);
        addSettings(color, hue1, sat1, hue2, sat2
                 , outline
                , customFont, offset, hideSomeCategories);
    }

    /* Global variables */
    public int moduleColor = -1;
    // Default value
    TTFFontRenderer font;

    float testAnimY = 0f;

    @Override
    public void onEvent(Event e) {
        if(mc.thePlayer == null || mc.theWorld == null) return;
        String currentFont = "Good";
        font = Noctura.INSTANCE.getFontManager().getFont(currentFont + " 18");
        if(font == null && customFont.isEnabled())return;
        if(e instanceof EventRender2D){

            ScaledResolution sr = new ScaledResolution(mc);
            float width = sr.getScaledWidth();

            java.util.ArrayList<Module> mods = new java.util.ArrayList<Module>(); // to be replaced by custom fonts?

            // Animation Loop

            // when disabled, xslide should slowly increase til its out of the screen
            // when toggled, it should slowly decrease til its at its position (width - getstring)
            // Here, i will draw the module at xSlide. it will be easier
            for(Module m : getModules()){
                double endPosition = width - modWidth(m);
                double animationSpeed = 0.06;
                if(m.isToggled()){
                    m.xSlide = (float) MathHelper.lerp(animationSpeed, m.xSlide, endPosition);
                }else{
                    m.xSlide = (float) MathHelper.lerp(animationSpeed, m.xSlide, width);
                }
                if(m.xSlide + 0.2 <= width){
                    if(!m.getName().toLowerCase().contains("sprint")){
                        mods.add(m);
                    }
                }
            }

            // Sorting modules by length
            if(customFont.getValue()){
                mods.sort(Comparator.comparingDouble(m ->  (double) Noctura.INSTANCE.getFontManager().getFont(currentFont + " 18").getWidth(modName((Module) m))).reversed());
            }else{
                mods.sort(Comparator.comparingInt(m ->  (int)mc.fontRendererObj.getStringWidth(modName((Module) m))).reversed());
            }

            int moduleIndex = 0;
            float verticalOffset = 0;

            // Drawing Loop
            for(Module m : mods){
                double stringX = m.xSlide;
                float calculatedHeight = (customFont.isEnabled() ? font.getHeight(m.getDisplayName()) : mc.fontRendererObj.FONT_HEIGHT);

                drawBackground(m, stringX, verticalOffset + 1);

                /* deb outline */
                if(outline.isEnabled()){
                    drawOutline(m, stringX, verticalOffset, calculatedHeight, mods, width, moduleIndex);
                }
                /* end outline */

                float scale = 1;
                float scaledX = (float) (stringX / scale);
                float scaledY = verticalOffset / scale;

                GlStateManager.pushMatrix();

                GlStateManager.scale(scale, scale, scale);
                GlStateManager.translate(scaledX, scaledY, 1f);

                drawModule(m, 0, 0, moduleIndex);
                GlStateManager.popMatrix();


                moduleIndex += 1;

                verticalOffset += calculatedHeight + 2f;
                //testAnimY += calculatedHeight + 2f;
                //verticalOffset = (float) MathHelper.lerp(0.1, verticalOffset, testAnimY);

            }
        }
    }



    public java.util.ArrayList<Module> getModules() {
        return (hideSomeCategories.isEnabled() ? Noctura.INSTANCE.getModuleManager().getModulesExcluding(Category.Display, Category.Visuals
                ,Category.Other, Category.False) : ModuleManager.modules);
    }


    public String modName(Module m){
        return m.getArrayListName() == null ? m.getDisplayName() : m.getArrayListName();
    }

    public double modWidth(Module m){
        return this.customFont.isEnabled() ? font.getWidth(modName(m)) : mc.fontRendererObj.getStringWidth(modName(m));
    }

    public void drawModule(Module m, double stringX, double stringY, int modindex){
        if(customFont.isEnabled()){
            if(font == null) return;
            font.drawStringWithShadow(modName(m), stringX, stringY, chooseColor(modindex));
            return;
        }
        mc.fontRendererObj.drawStringWithShadow(modName(m), (float) stringX, (float) stringY, chooseColor(modindex));
    }

    private void drawOutline(Module m, double stringX, float verticalOffset, float calculatedHeight, ArrayList<Module> mods, float width, int modindex){
        int lengthOfMod = 74; // ??
        if(customFont.isEnabled()){
            double leftX = stringX - 1;
            float bottomStart = verticalOffset + m.ySlide + calculatedHeight + 2;
            float rectWidth = width + lengthOfMod;

            boolean isLast = mods.indexOf(m) == mods.size() - 1;

            if(isLast){
                float right = (float)(leftX + font.getWidth(modName(m)) + 4f);

                Gui.drawRect((float) leftX + 0f, bottomStart - 1, right, bottomStart, chooseColor(modindex));
            }else{
                int current = mods.indexOf(m);
                Module following = mods.get(current + 1);
                float lengthFollowing = font.getWidth(modName(following));
                float lengthCurrent = font.getWidth(modName(m));
                float difference = lengthCurrent - lengthFollowing;
                float right = (float) (leftX + difference);

                Gui.drawRect((float) leftX + 0f, bottomStart - 1, right, bottomStart, chooseColor(modindex));
            }
            Gui.drawRect((float) leftX - 1f, verticalOffset + m.ySlide, (float) leftX + 0f, bottomStart, chooseColor(modindex));

            GlStateManager.resetColor();
        }else{
            double leftX = stringX - 1;
            float bottomStart = verticalOffset  + calculatedHeight + 2;
            float rectWidth = width + lengthOfMod;

            boolean isLast = mods.indexOf(m) == mods.size() - 1;

            if(isLast){
                float right = (float)(leftX + mc.fontRendererObj.getStringWidth(modName(m)) + 4f);

                Gui.drawRect((float) leftX + 0f, bottomStart - 1, right, bottomStart, chooseColor(modindex));
            }else{
                int current = mods.indexOf(m);
                Module following = mods.get(current + 1);
                float lengthFollowing = mc.fontRendererObj.getStringWidth(modName(following));
                float lengthCurrent = mc.fontRendererObj.getStringWidth(modName(m));
                float difference = lengthCurrent - lengthFollowing;
                float right = (float) (leftX + difference);

                Gui.drawRect((float) leftX + 0f, bottomStart - 1, right, bottomStart, chooseColor(modindex));
            }
            float test = 0;
            if(m.ySlide <= 12) test = 12;
            if(m.ySlide <= 1 && m.ySlide >= -1) test = 0;
            Gui.drawRect((float) leftX - 1f, verticalOffset + m.ySlide - test, (float) leftX + 0f, bottomStart, chooseColor(modindex));

            GlStateManager.resetColor();
        }
    }

    private void drawBackground(Module m, double stringX, float stringY) {
        float bgOffset = 1f;
        float aroundX = 1;
        float calculatedHeight = (customFont.isEnabled() ? font.getHeight(m.getDisplayName()) : mc.fontRendererObj.FONT_HEIGHT);
        int bgOpacity = net.minecraft.util.MathHelper.clamp_int(90, 0, 255);

        if(customFont.isEnabled()){
            if(font == null) return;
            Gui.drawRect((float)stringX - aroundX, stringY - bgOffset, (float)(stringX + aroundX + font.getWidth(modName(m))), stringY + calculatedHeight + bgOffset, new Color(0, 0, 0, bgOpacity).getRGB());
            GlStateManager.resetColor();
            return;
        }
        Gui.drawRect((float)stringX - aroundX, stringY - bgOffset, (float)(stringX + aroundX + mc.fontRendererObj.getStringWidth(modName(m))), stringY + calculatedHeight + bgOffset, new Color(0, 0, 0, bgOpacity).getRGB());
        GlStateManager.resetColor();
    }

    public int chooseColor(int moduleIndex){
        //moduleColor;
        int fin = -1;
        switch (color.getMode()){
            case "Custom":{
                fin = getColorFromSettings(hue1, sat1).getRGB();
                break;
            }
            case "Default":{
                fin = -1;
                break;
            }
            case "Blend":{
                Color col1 = getColorFromSettings(hue1,sat1);
                Color col2 = getColorFromSettings(hue2,sat2);
                int off = (int) (offset.getValue() * 75);
                fin = ColorUtils.blendThing(2F, (long)moduleIndex * off, col1, col2);
                break;
            }
        }

        return fin;
    }
}
