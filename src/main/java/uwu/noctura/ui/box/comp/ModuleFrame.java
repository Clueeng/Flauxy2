package uwu.noctura.ui.box.comp;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import uwu.noctura.Noctura;
import uwu.noctura.module.Module;
import uwu.noctura.ui.box.BoxGUI;
import uwu.noctura.ui.box.comp.impl.ModuleSettingFrame;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;

import static uwu.noctura.utils.font.FontManager.getFont;
import static uwu.noctura.utils.render.ColorUtils.interpolateColor;

@Getter
public class ModuleFrame {

    public CategoryWindow categoryWindow;
    public Module module;
    int x, y;
    int w, h;
    Minecraft mc = Minecraft.getMinecraft();
    public BoxGUI parent;

    public ModuleFrame(Module module, CategoryWindow categoryWindow, int x, int y, BoxGUI parent){
        this.categoryWindow = categoryWindow;
        this.module = module;
        this.x = x;
        this.y = y;
        this.w = 172;
        this.h = 52;
        this.parent = parent;
    }
    private float progress = 0.0f;

    public void updateProgress(boolean isToggled) {
        float speed = 0.05f;
        progress = (float) MathHelper.lerp(0.02, progress, isToggled ? 1.0f : 0.0f);
    }

    public void drawScreen(int mouseX, int mouseY){
        //Gui.drawRect(x, y, x + w, y + h, new Color(0,0,0).getRGB());
        TTFFontRenderer icon = FontManager.getFont("icons2", 16);
        TTFFontRenderer modfont = FontManager.getFont("Good", 22);
        RenderUtil.drawRoundedRect3(x, y, w, h, 8, new Color(20, 20, 20).getRGB());
        //RenderUtil.drawRoundedRect3(x, y + h - 16, w, 16, 8, new Color(77, 67, 110).getRGB());
        GlStateManager.resetColor();



        /*if(this.module.isToggled()){
            RenderUtil.drawGradientSideways(x, y + h - 4 + 2, x + w, y + h - 3 + 3, new Color(31, 52, 135).getRGB(), new Color(116, 76, 236).getRGB());
        }else{
            RenderUtil.drawGradientSideways(x, y + h - 4 + 2, x + w, y + h - 3 + 3, new Color(110, 3, 16).getRGB(), new Color(168, 14, 50).getRGB());
        }*/
        int onEndColor = new Color(31, 52, 135).getRGB();
        int onStartColor = new Color(116, 76, 236).getRGB();
        int offStartColor = new Color(110, 3, 16).getRGB();
        int offEndColor = new Color(168, 14, 50).getRGB();

        int startColor = interpolateColor(offStartColor, onStartColor, progress);
        int endColor = interpolateColor(offEndColor, onEndColor, progress);

        updateProgress(module.isToggled());

        Color test = new Color(endColor);
        Color test2 = new Color(startColor);
        //RenderUtil.drawGradientSideways(x, y + h - 4 + 2, x + w, y + h - 3 + 3, new Color(31, 52, 135).getRGB(), new Color(116, 76, 236).getRGB());


        RenderUtil.drawGradientSideways(x, y + h - 4 + 2, x + w, y + h - 3 + 3, test.getRGB(), test2.getRGB());

        Gui.drawRect(x, y + h - 20, x + w, y + h - 4,new Color(20, 20, 20).getRGB() );
        modfont.drawStringWithShadow(module.getDisplayName(), -1 + x + (w / 2f) - (modfont.getWidth(module.getDisplayName())/2f), y + 4, -1);
        if(isHovered(mouseX, mouseY) && !module.settings.isEmpty()){
            icon.drawString("N", -1 + x + w - 12, y + 4, -1);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        if(isHovered(mouseX, mouseY)){
            if(mouseButton == 0){
                module.toggle();
            }else if(mouseButton == 1){
                // make its own gui for ModuleSettingFrame
                if(!module.settings.isEmpty()){
                    mc.displayGuiScreen(new ModuleSettingFrame(this, parent));
                }
            }
        }
    }

    public boolean isHovered(int mouseX, int mouseY){
        return RenderUtil.hover(x, y, mouseX, mouseY, w, h) && this.categoryWindow.category.equals(parent.currentCategory);
    }

}
