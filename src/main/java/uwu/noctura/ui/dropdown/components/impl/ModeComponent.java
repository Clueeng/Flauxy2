package uwu.noctura.ui.dropdown.components.impl;

import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.ui.dropdown.ColorHelper;
import uwu.noctura.ui.dropdown.components.ModuleFrame;
import uwu.noctura.ui.dropdown.components.Component;
import uwu.noctura.utils.render.RenderUtil;

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

        getFont().drawString(((ModeSetting) getSetting()).getMode(), x + defaultWidth - getFont().getWidth(((ModeSetting) getSetting()).getMode()) , y + (getOffset() / 2F - (getFont().getHeight("A") / 2F))  + 3, -1);
        getFont().drawString(getSetting().name, x + 5, y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 3, -1);
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
            offset = 16;
        }else{
            offset = 0;
        }
        return offset;
    }
}
