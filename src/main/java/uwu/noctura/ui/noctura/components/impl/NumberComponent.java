package uwu.noctura.ui.noctura.components.impl;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.ui.noctura.ColorHelper;
import uwu.noctura.ui.noctura.components.Component;
import uwu.noctura.ui.noctura.components.ModuleFrame;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static uwu.noctura.utils.font.FontManager.getFont;


public class NumberComponent extends Component implements ColorHelper {
    private ModuleFrame owner;
    public NumberComponent(int x, int y, ModuleFrame owner, Setting setting) {
        super(x, y, owner, setting);
        this.owner = owner;
    }

    private boolean drag;
    private boolean inputMode;
    private String inputText = "";

    @Override
    public void initGui() {
        drag = false;
        inputMode = false;
        inputText = "";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (!Mouse.isButtonDown(0)) drag = false;

        NumberSetting slide = (NumberSetting) getSetting();
        boolean isColor = slide.colorDisplay;

        double min = slide.getMinimum();
        double max = slide.getMaximum();
        double diff = Math.min(defaultWidth + 5, Math.max(0, mouseX - (this.x)));
        double renderWidth = defaultWidth * (slide.getValue() - min) / (max - min);
        if (isColor) {
            for (int i = 0; i < defaultWidth; i++) {
                float hue = (float) i / (float) defaultWidth;
                int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
                Gui.drawRect(x + i, y + 20, x + i + 1, y + getOffset() - 10 + 5 + 8, color);
            }
        } else {
            Gui.drawRect(x, y + 20, x + defaultWidth, y + getOffset() - 10 + 5 + 8, new Color(125, 125, 125).getRGB());
            Gui.drawRect(x, y + 20, x + (int) renderWidth, y + getOffset() - 10 + 5 + 8, nocturaGalaxyDark);
        }
        if (drag) {
            if (diff == 0) {
                slide.setValue(min);
            } else {
                double newValue = roundToPlace((diff / defaultWidth) * (max - min) + min, 2);
                if (newValue <= max) {
                    this.setValue(newValue);
                }
            }
        }
        if (inputMode) {
            getFont().drawString(slide.getName() + ": " + inputText + " (Type Value)", (float) (x + 5), (float) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 2), stringColor);
        } else {
            getFont().drawString(slide.getName() + ": " + roundToPlace(slide.getValue(), 2), (float) (x + 5), (float) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 2), stringColor);
        }
        //getFont().drawString(getSetting().name + ": " + roundToPlace(slide.getValue(), 2), (float) (x + 5), (float) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 2), stringColor);
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
        if(inputMode){
            set.setValue2(value);
            System.out.println("a");
        }else{
            set.setValue(MathHelper.clamp_double(snapToStep(value, set.getIncrement()), set.getMinimum(), set.getMaximum()));
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
            if (RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && !((NumberSetting)getSetting()).colorDisplay) {
                inputMode = true;
                inputText = String.valueOf(((NumberSetting)getSetting()).getValue());
                return true;
            }
        } else if (mouseButton == 0) {
            if(inputMode){
                double newValue = Double.parseDouble(inputText);
                setValue(newValue);
                inputMode = false;
            }
            return drag = RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, getOffset());
        }
        return false;
    }


    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {
        inputMode = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (inputMode) {
            System.out.println("adding");
            if (Character.isDigit(typedChar) || typedChar == '.' || typedChar == '-') {
                inputText += typedChar;
            } else if (keyCode == Keyboard.KEY_BACK && !inputText.isEmpty()) {
                inputText = inputText.substring(0, inputText.length() - 1);
            } else if (keyCode == Keyboard.KEY_RETURN) {
                try {
                    double newValue = Double.parseDouble(inputText);
                    setValue(newValue);
                } catch (NumberFormatException ignored) {
                }
                inputMode = false;
            } else if (keyCode == Keyboard.KEY_ESCAPE) {
                inputMode = false;
            }
        }
    }

    @Override
    public int getOffset() {
        int offset = 0;
        if(this.getSetting().getCanShow().test(null)){
            offset = 24;
        }else{
            offset = 0;
        }
        return offset;
    }
}
