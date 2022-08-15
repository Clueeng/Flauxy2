package uwu.flauxy.ui.astolfo.components.impl;

import uwu.flauxy.module.setting.Setting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.ui.astolfo.components.Component;
import uwu.flauxy.ui.astolfo.components.ModuleFrame;
import uwu.flauxy.ui.astolfo.ColorHelper;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;

import static uwu.flauxy.utils.font.FontManager.getFont;

public class ModeComponent extends Component implements ColorHelper {
    public ModeComponent(int x, int y, ModuleFrame owner, Setting setting) {
        super(x, y, owner, setting);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        getFont().drawString(((ModeSetting) getSetting()).getMode().toLowerCase(), x + 12 , y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 16, new Color(174, 174, 174, 255).getRGB());
        getFont().drawString(getSetting().name.toLowerCase(), x + 5, y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 3, -1);
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
            offset = 34;
        }else{
            offset = 0;
        }
        return offset;
    }
}
