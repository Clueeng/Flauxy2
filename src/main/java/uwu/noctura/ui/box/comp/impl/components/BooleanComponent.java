package uwu.noctura.ui.box.comp.impl.components;

import net.minecraft.client.gui.Gui;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.ui.box.comp.impl.Component;
import uwu.noctura.ui.box.comp.impl.ModuleSettingFrame;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.ColorUtils;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;

import static uwu.noctura.utils.MoveUtils.mc;

public class BooleanComponent extends Component {

    public ModuleSettingFrame owner;
    public BooleanSetting setting;

    public BooleanComponent(int x, int y, ModuleSettingFrame owner, BooleanSetting setting) {
        super(x, y, owner, setting);
        this.x = x;
        this.y = y;
        this.compWidth = 130;
        this.compHeight = 24;
        this.owner = owner;
        this.setting = setting;
        progress = this.setting.isEnabled() ? 1.0f : 0f;
    }

    @Override
    public void initGui() {

    }

    private float progress = 0.0f;

    public void updateProgress(boolean isToggled) {
        progress = (float) MathHelper.lerp(0.03, progress, isToggled ? 1.0f : 0.0f);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        TTFFontRenderer title = FontManager.getFont("Poppins", 19);

        RenderUtil.drawRoundedRect3(x, y, compWidth, compHeight, 20, new Color(20, 20, 20, 100).getRGB());
        title.drawStringWithShadow(setting.getName(), x + 20, y + (compHeight / 2f) - (title.getHeight("A") / 2f) + 1, -1);

        updateProgress(this.setting.isEnabled());

        int offColor = new Color(129, 0, 255).getRGB();
        int onColor = new Color(60, 60, 60).getRGB();

        int interpolated = ColorUtils.interpolateColor(onColor, offColor, progress);

        RenderUtil.drawCircle(x + 10, y + (compHeight / 2f), 3f, new Color(interpolated).getRGB());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.hover(x, y, mouseX, mouseY, compWidth, compHeight)){
            setting.setEnabled(!setting.isEnabled());
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
