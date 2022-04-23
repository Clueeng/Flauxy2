package uwu.flauxy.ui.dropdown.components.impl;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.module.impl.display.ClickGUI;
import uwu.flauxy.module.setting.Setting;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.ui.dropdown.ColorHelper;
import uwu.flauxy.ui.dropdown.components.ModuleFrame;
import uwu.flauxy.utils.animtations.Animate;
import uwu.flauxy.utils.animtations.Easing;
import uwu.flauxy.ui.dropdown.components.Component;
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

        getFont().drawString(((ModeSetting) getSetting()).getMode(), x + defaultWidth - getFont().getWidth(((ModeSetting) getSetting()).getMode()) , y + (getOffset() / 2F - (getFont().getHeight("A") / 2F))  + 4, -1);
        getFont().drawString(getSetting().name, x + 5, y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 4, -1);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()))
        {
            ModeSetting enumValue = (ModeSetting) getSetting();


            if(mouseButton == 0) {
              enumValue.cycle();
            }

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
            offset = 20;
        }else{
            offset = 0;
        }
        return offset;
    }
}
