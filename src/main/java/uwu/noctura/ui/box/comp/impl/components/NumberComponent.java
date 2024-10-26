package uwu.noctura.ui.box.comp.impl.components;

import org.lwjgl.input.Keyboard;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.ui.box.comp.impl.Component;
import uwu.noctura.ui.box.comp.impl.ModuleSettingFrame;
import net.minecraft.client.gui.Gui;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static uwu.noctura.utils.MoveUtils.mc;

public class NumberComponent extends Component {
    public ModuleSettingFrame owner;
    public NumberSetting setting;
    public boolean dragging = false;
    public boolean inputMode = false;
    public String inputText = "";

    public NumberComponent(int x, int y, ModuleSettingFrame owner, NumberSetting setting) {
        super(x, y, owner, setting);
        this.x = x;
        this.y = y;
        this.compWidth = 130;
        this.compHeight = 24;
        this.owner = owner;
        this.setting = setting;
    }

    @Override
    public void initGui() {
        dragging = false;
        inputMode = false;
        inputText = "";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        TTFFontRenderer title = FontManager.getFont("Poppins", 19);
        Gui.drawRect(x, y + (compHeight / 2f), x + compWidth, y + compHeight, new Color(0, 0, 0, 90).getRGB());
        double min = setting.getMinimum();
        double max = setting.getMaximum();

        double renderWidth = compWidth * (setting.getValue() - min) / (max - min);

        if (setting.colorDisplay) {
            Gui.drawRect(x, y + (compHeight / 2f), x + compWidth, y + compHeight, new Color(0, 0, 0, 255).getRGB());
            for (int i = 1; i < compWidth-1; i++) {
                float hue = (float) i / (float) compWidth;
                int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
                Gui.drawRect(x + i, y + (compHeight / 2f) + 1, x + i + 1, y + compHeight - 1, color);
            }
            Gui.drawRect((float) (x + (renderWidth)  - 1), y + (compHeight / 2f), x + (float) renderWidth + 1, y + compHeight, new Color(0, 0, 0, 255).getRGB());
            Gui.drawRect((float) (x + (renderWidth)  - .5), y + (compHeight / 2f) + 1, x + (float) renderWidth + .5f, y + compHeight - 1, new Color(255, 255, 255, 255).getRGB());
        } else {
            Gui.drawRect(x, y + (compHeight / 2f), x + (int) renderWidth, y + compHeight, new Color(90, 90, 255, 255).getRGB());
        }
        if (dragging) {
            double diff = Math.min(compWidth, Math.max(0, mouseX - this.x));
            if (diff == 0) {
                setting.setValue(min);
            } else {
                double newValue = roundToPlace((diff / compWidth) * (max - min) + min, 2);
                setValue(newValue);
            }
        }

        if (inputMode) {
            title.drawStringWithShadow(setting.getName() + ": " + inputText + " (Type Value)", x + 2, y, -1);
        } else {
            title.drawStringWithShadow(setting.getName() + ": " + roundToPlace(setting.getValue(), 2), x + 2, y, -1);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY) && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            dragging = true;
            return true;
        }
        if (mouseButton == 0 && (isHovering(mouseX, mouseY))) {
            inputMode = true;
            inputText = String.valueOf(setting.getValue());
            return true;
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (inputMode) {
            if (Character.isDigit(typedChar) || typedChar == '.' || typedChar == '-') {
                inputText += typedChar;
            } else if (keyCode == 14 && !inputText.isEmpty()) {  // Handle backspace
                inputText = inputText.substring(0, inputText.length() - 1);
            } else if (keyCode == Keyboard.KEY_RETURN) {  // Handle Enter key
                try {
                    double newValue = Double.parseDouble(inputText);
                    setValue(newValue);
                } catch (NumberFormatException ignored) {
                }
                inputMode = false;
            } else if (keyCode == 1) {  // Handle Escape key
                inputMode = false;
            }
        }
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {
        inputMode = false;
    }

    @Override
    public int getOffset() {
        return compHeight;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + compWidth && mouseY >= y && mouseY <= y + compHeight;
    }

    private void setValue(double value) {
        setting.setValue(Math.max(setting.getMinimum(), Math.min(setting.getMaximum(), roundToPlace(value, 2))));
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        return false;
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
