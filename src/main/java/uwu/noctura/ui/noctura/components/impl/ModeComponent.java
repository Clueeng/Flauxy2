package uwu.noctura.ui.noctura.components.impl;

import net.minecraft.client.renderer.GlStateManager;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.ui.noctura.ColorHelper;
import uwu.noctura.ui.noctura.components.Component;
import uwu.noctura.ui.noctura.components.ModuleFrame;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;

import static uwu.noctura.utils.font.FontManager.getFont;

public class ModeComponent extends Component implements ColorHelper {
    public ModeComponent(int x, int y, ModuleFrame owner, Setting setting) {
        super(x, y, owner, setting);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        getFont().drawString(((ModeSetting) getSetting()).getMode(), x + 3 + getFont().getWidth(getSetting().name + ": "), y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 0, new Color(174, 174, 174, 255).getRGB());
        getFont().drawString(getSetting().name + ": ", x + 5, y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 0, -1);


        int circleY = (int) (y + (getOffset() / 2F)) + 12;
        int circleRadius = 5;
        int amountOfModes = ((ModeSetting) getSetting()).modes.size();
        int totalCircleWidth = amountOfModes * (2 * circleRadius);
        int spacing = (defaultWidth - totalCircleWidth) / (amountOfModes + 1);
        float startX = (x + (float) (defaultWidth - totalCircleWidth - spacing * (amountOfModes - 1)) / 2) + 8;
        int index = 0;

        for (String choice : ((ModeSetting) getSetting()).modes) {
            int circleX = (int) (startX + index * (2f * circleRadius + spacing));
            RenderUtil.drawFilledCircle(circleX, circleY, 2, ((ModeSetting) getSetting()).is(choice) ? Color.white : new Color(80, 80, 80, 250));
            GlStateManager.resetColor();
            index++;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()))
        {
            ModeSetting enumValue = (ModeSetting) getSetting();
            enumValue.cycle(mouseButton);
            return true;
        }
        return false;
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
            offset = 24;
        }else{
            offset = 0;
        }
        return offset;
    }
}
