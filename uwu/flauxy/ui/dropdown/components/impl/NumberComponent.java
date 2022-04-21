package uwu.flauxy.ui.dropdown.components.impl;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import uwu.flauxy.Flauxy;
import uwu.flauxy.module.impl.display.ClickGUI;
import uwu.flauxy.module.setting.Setting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.ui.dropdown.ColorHelper;
import uwu.flauxy.ui.dropdown.components.ModuleFrame;
import uwu.flauxy.ui.dropdown.components.Component;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static uwu.flauxy.utils.font.FontManager.getFont;


public class NumberComponent extends Component implements ColorHelper {
    public NumberComponent(int x, int y, ModuleFrame owner, Setting setting) {
        super(x, y, owner, setting);
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
        Gui.drawRect(x, y + 15, x + (int) renderWidth, y + getOffset() - 10 + 5 + 4, new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue()).getRGB());


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

        Flauxy.INSTANCE.getFontManager().getFont("auxy 8").drawString(getSetting().name + ": " + roundToPlace(((NumberSetting) getSetting()).getValue(), 2), (float) (x + 5), (float) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) - 4.3 + 5), stringColor);

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
        return 15;
    }
}
