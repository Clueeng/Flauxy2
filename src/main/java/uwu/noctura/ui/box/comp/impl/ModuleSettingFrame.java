package uwu.noctura.ui.box.comp.impl;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.module.impl.display.ClickGUI;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.ui.box.BoxGUI;
import uwu.noctura.ui.box.comp.ModuleFrame;
import uwu.noctura.ui.box.comp.impl.components.BooleanComponent;
import uwu.noctura.ui.box.comp.impl.components.GraphComponent;
import uwu.noctura.ui.box.comp.impl.components.ModeComponent;
import uwu.noctura.ui.box.comp.impl.components.NumberComponent;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ModuleSettingFrame extends GuiScreen {

    public ModuleFrame parent;
    int x, y, guiWidth, guiHeight;
    public ScaledResolution sr;
    public BoxGUI parentGui;

    ArrayList<Component> components = new ArrayList<>();

    @Override
    public void initGui() {
        sr = new ScaledResolution(mc);
        x = 108;
        guiWidth = sr.getScaledWidth() - x;
        y = 48;
        guiHeight = sr.getScaledHeight() - (y * 2);
    }

    public ModuleSettingFrame(ModuleFrame parent, BoxGUI oldGui){
        this.parent = parent;
        this.parentGui = oldGui;

        int settingsX = parent.getParent().x + 4, settingsY = parent.getParent().y + 42;
        for(Setting s : parent.getModule().getSettings()){
            if(s instanceof ModeSetting){
                ModeComponent mode = new ModeComponent(settingsX, settingsY, this, (ModeSetting) s);
                components.add(mode);
            }
            if(s instanceof BooleanSetting){
                BooleanComponent mode = new BooleanComponent(settingsX, settingsY, this, (BooleanSetting) s);
                components.add(mode);
            }
            if(s instanceof NumberSetting){
                NumberComponent mode = new NumberComponent(settingsX, settingsY, this, (NumberSetting) s);
                components.add(mode);
            }
            if(s instanceof GraphSetting){
                GraphSetting gset = (GraphSetting)s;
                GraphComponent graph;
                if(gset.colorDisplay){
                    graph = new GraphComponent(settingsX, settingsY, this, (GraphSetting) s, gset.hue);
                }else{
                    graph = new GraphComponent(settingsX, settingsY, this, (GraphSetting) s);
                }
                components.add(graph);
            }

        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiScreen.drawRect(x, y, guiWidth, guiHeight, new Color(58, 58, 58).getRGB());
        GuiScreen.drawRect(x, y, guiWidth, y + 24, new Color(35, 35, 35).getRGB());
        //GuiScreen.drawRect(x, y + 23, guiWidth, y + 24, new Color(234, 231, 227).getRGB());
        RenderUtil.drawGradientSideways(x, y + 23, guiWidth, y + 24, new Color(31, 52, 135).getRGB(), new Color(116, 76, 236).getRGB());
        //mc.fontRendererObj.drawStringWithShadow(parent.module.getName(), sr.getScaledWidth() / 2f - (mc.fontRendererObj.getStringWidth(parent.module.getName())/2f), y + 8, -1);
        //mc.fontRendererObj.drawStringWithShadow(Noctura.INSTANCE.name + ".lol - " + parent.module.getName(), x + 8, y + 8, -1);

        TTFFontRenderer title = FontManager.getFont("Poppins", 19);
        RenderUtil.drawGradientString(title, "Noctura.lol", x + 4, y + 6, new Color(31, 52, 135), new Color(116, 76, 236));
        title.drawStringWithShadow(" - " + this.parent.module.getName(), x + 4 + title.getWidth("Noctura.lol"), y + 6, new Color(99, 99, 99).getRGB());


        int guioffset = 12;
        int settingsX = parent.getParent().x + guioffset;
        int settingsY = parent.getParent().y + 42;
        int compGapX = 18;
        int compGapY = 8;
        int outScreen = parent.getParent().guiWidth;

        int[] oldRowComponentsHeight = new int[100];
        int col = 0;

        for(Component c : components){
            if(!c.getSetting().getCanShow().test(this.parent.module)) continue;
            c.setX(settingsX);
            c.setY(settingsY + oldRowComponentsHeight[col]);
            oldRowComponentsHeight[col] = (oldRowComponentsHeight[col]) + c.compHeight + compGapY;
            c.drawScreen(mouseX, mouseY);
            // first way
            col++;
            settingsX += c.compWidth + compGapX;
            if(settingsX + c.compWidth > outScreen){
                col = 0;
                settingsX = parent.getParent().x + guioffset;
                //settingsY += c.compHeight + compGap;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(Component c : components){
            c.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onGuiClosed() {
        //mc.displayGuiScreen(parentGui);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE){
            mc.displayGuiScreen(parentGui);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        for(Component c : components){
            c.mouseReleased(mouseX, mouseY, state);
        }
    }
}
