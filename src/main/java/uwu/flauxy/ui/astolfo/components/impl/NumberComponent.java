package uwu.flauxy.ui.astolfo.components.impl;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import uwu.flauxy.module.setting.Setting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.ui.astolfo.components.Component;
import uwu.flauxy.ui.astolfo.components.ModuleFrame;
import uwu.flauxy.ui.astolfo.ColorHelper;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static uwu.flauxy.utils.font.FontManager.getFont;


public class NumberComponent extends Component implements ColorHelper {
    private ModuleFrame owner;
    public NumberComponent(int x, int y, ModuleFrame owner, Setting setting) {
        super(x, y, owner, setting);
        this.owner = owner;
    }

    private boolean drag;

    @Override
    public void initGui() {
        drag = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if(!Mouse.isButtonDown(0)) drag = false;

        NumberSetting slide = (NumberSetting) getSetting();
        double min = slide.getMinimum();
        double max = slide.getMaximum();
        double diff = Math.min(defaultWidth + 5, Math.max(0, mouseX - (this.x)));
        double renderWidth = defaultWidth * (slide.getValue() - min) / (max - min);
        Gui.drawRect(x, y + 18, x + (int) renderWidth, y + getOffset() - 10 + 5 + 4, owner.getParent().getCategory().getCategoryColor().getRGB());


        if(drag)
        {
            if(diff == 0)
                slide.setValue(min);
            else
            {
                double newValue = roundToPlace((diff / defaultWidth) * (max - min) + min, 2);
                if(newValue <= max)
                    this.setValue(newValue);
            }
        }

        getFont().drawString(getSetting().name.toLowerCase() + ": " + roundToPlace(((NumberSetting) getSetting()).getValue(), 2), (float) (x + 5), (float) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 2), stringColor);
        GlStateManager.resetColor();
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private double snapToStep(double value, double valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * (double) Math.round(value / valueStep);

        return value;
    }

    private void setValue(double value) {
        final NumberSetting set = (NumberSetting) getSetting();
        set.setValue(MathHelper.clamp_double(snapToStep(value, set.getIncrement()), set.getMinimum(), set.getMaximum()));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return drag = RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public int getOffset() {
        int offset = 0;
        if(this.getSetting().getCanShow().test(null)){
            offset = 18;
        }else{
            offset = 0;
        }
        return offset;
    }
}
