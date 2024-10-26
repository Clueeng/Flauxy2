package uwu.noctura.ui.box.comp.impl.components;

import net.minecraft.client.gui.Gui;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.ui.box.comp.impl.Component;
import uwu.noctura.ui.box.comp.impl.ModuleSettingFrame;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;

import static uwu.noctura.utils.MoveUtils.mc;

public class ModeComponent extends Component {

    public ModuleSettingFrame owner;
    public ModeSetting setting;

    private boolean showOthers;

    public ModeComponent(int x, int y, ModuleSettingFrame owner, ModeSetting setting) {
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

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        TTFFontRenderer title = FontManager.getFont("Poppins", 19);
        RenderUtil.drawRoundedRect3(x, y, compWidth, compHeight, 20, new Color(20, 20, 20, 100).getRGB());
        title.drawStringWithShadow(setting.getName() + ": " + setting.getMode(), x + 8, y + (compHeight / 2f) - 5, -1);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.hover(x, y, mouseX, mouseY, compWidth, compHeight))
        {
            if(mouseButton == 2){
                showOthers = !showOthers;
            }
            ModeSetting enumValue = (ModeSetting) getSetting();
            enumValue.cycle(mouseButton);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state) {
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
        return 0;
    }
}
