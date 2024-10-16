package uwu.noctura.ui.noctura.components.impl;

import net.minecraft.client.renderer.GlStateManager;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.ui.noctura.ColorHelper;
import uwu.noctura.ui.noctura.components.Component;
import uwu.noctura.ui.noctura.components.ModuleFrame;
import uwu.noctura.utils.animtations.Animate;
import uwu.noctura.utils.animtations.Easing;
import uwu.noctura.utils.render.RenderUtil;

import static uwu.noctura.utils.font.FontManager.getFont;


public class BooleanComponent extends Component implements ColorHelper {
    private final Animate animation;
    private ModuleFrame owner;

    public BooleanComponent(int x, int y, ModuleFrame owner, Setting setting)
    {
        super(x, y, owner, setting);
        this.owner = owner;
        this.animation = new Animate().setMin(0).setMax(5).setSpeed(15).setEase(Easing.LINEAR).setReversed(!((BooleanSetting) setting).isEnabled());
    }

    @Override
    public void initGui()
    {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        animation.update();
        getFont().drawString(getSetting().name, (float)x + (defaultWidth / 2) - (getFont().getWidth(getSetting().getName()) / 2), (float) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 5), ((BooleanSetting) getSetting()).isEnabled() ? nocturaGalaxyDark : -1);

        //RenderUtil.drawFilledCircle(x + defaultWidth - 10, (int) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 6.75f), 5, new Color(19, 19, 19, 130));
        GlStateManager.resetColor();


        if(((BooleanSetting) getSetting()).isEnabled() || animation.getValue() != 0)
        {

            //RenderUtil.drawFilledCircle(x + defaultWidth - 10, (int) (y + (getOffset() / 2F - (getFont().getHeight("A") / 2F)) + 6.75f), animation.getValue(), new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue()));

            GlStateManager.resetColor();
        }
        GlStateManager.resetColor();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, getOffset())) {
            BooleanSetting set = (BooleanSetting) getSetting();
            set.setEnabled(!set.isEnabled());
            animation.setReversed(!set.isEnabled());
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

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
