package uwu.noctura.ui.noctura;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import uwu.noctura.module.Category;
import uwu.noctura.ui.noctura.components.CategoryFrame;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {
    private final List<CategoryFrame> categories;

    public ClickGUI()
    {
        this.categories = new ArrayList<>();

        int index = -1;
        for(Category category : Category.values()) {
            if(!category.equals(Category.Macro)){
                index++;
                categories.add(new CategoryFrame(category, category.id > 5 ? 33 + ((index-5) * (175 + 10)) : 33 + (index * (175 + 10)), category.id > 5 ? height + 280 : 10));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void initGui()
    {
        gaussianAnim = 0.1f;
        opacityAnim = 0;
        categories.forEach(CategoryFrame::initGui);
        super.initGui();
    }
    float gaussianAnim;
    float opacityAnim;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(0
                ,0,width, height);

        gaussianAnim = (float) MathHelper.lerp(0.136f, gaussianAnim, 9f);
        opacityAnim = (float) MathHelper.lerp(0.0336f, opacityAnim, 512);

        Gui.drawRect(1, 1, 1, 1, new Color(0, 0, 0, 10).getRGB()); //
        this.drawGradientRect(0, (int) (height - opacityAnim), width, height, new Color(255, 255, 255, 0).getRGB(), new Color(190, 20, 200, 90).getRGB());
        GaussianBlur.renderBlur(gaussianAnim); // 9f
        Gui.drawRect(1, 1, 1, 1, new Color(0, 0, 0, 10).getRGB());

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();

        categories.forEach(frameCategory -> frameCategory.drawScreen(mouseX, mouseY));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        categories.forEach(frameCategory -> frameCategory.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        categories.forEach(frameCategory -> frameCategory.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
