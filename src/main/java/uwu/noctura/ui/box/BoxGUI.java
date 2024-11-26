package uwu.noctura.ui.box;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.module.Category;
import uwu.noctura.ui.box.comp.CategoryWindow;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class BoxGUI extends GuiScreen {

    public int x, y, guiWidth, guiHeight;
    float scrollX, lerpScrollX;
    public ScaledResolution sr;
    public Category currentCategory;
    public float currentCatX;

    ArrayList<CategoryWindow> categoryWindows = new ArrayList<>();

    @Override
    public void onGuiClosed() {
        return;
    }

    boolean debutAnimInit, debutAnimClose;
    float translateAnimX = -250;
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE){
            debutAnimClose = true;
            return;
        }
    }

    int catPushOffset = 152;

    @Override
    public void initGui() {
        debutAnimClose = false;
        debutAnimInit = true;
        translateAnimX = -1800;
        categoryWindows.clear();
        super.initGui();
        sr = new ScaledResolution(mc);
        x = 108;
        guiWidth = sr.getScaledWidth() - x;
        y = 48;
        guiHeight = sr.getScaledHeight() - (y * 2);

        int xCat = catPushOffset;
        for(Category c : Category.values()){
            CategoryWindow window = new CategoryWindow(c, x + xCat, y, this);
            categoryWindows.add(window);
            xCat += window.width;
        }
        currentCategory = currentCategory == null ? categoryWindows.get(0).category : currentCategory;
    }

    int debutTick, closeTick;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        sr = new ScaledResolution(mc);

        if(debutAnimInit){
            closeTick = 0;
            if(debutTick == 0){
                debutAnimClose = false;
                translateAnimX = -1000;
            }
            translateAnimX = (float) MathHelper.easeInOutQuad(0.11, translateAnimX, 0);
            if(translateAnimX >= -0.02){
                debutAnimInit = false;
            }
            debutTick++;
        }
        if(debutAnimClose){
            debutTick = 0;
            if(closeTick == 0){
                debutAnimInit = false;
                translateAnimX = 0;
            }
            translateAnimX = (float) MathHelper.easeInOutQuad(0.11, translateAnimX, -1000);
            System.out.println(translateAnimX);
            if(translateAnimX <= -900){
                mc.displayGuiScreen(null);
            }
            closeTick++;
        }

        GlStateManager.pushMatrix();

        GlStateManager.translate(translateAnimX, 0f, 0f);

        drawGuiBackground();

        //GuiScreen.drawRect(x, y, guiWidth, guiHeight, new Color(58, 58, 58).getRGB());
        GuiScreen.drawRect(x, y, guiWidth, y + 24, new Color(35, 35, 35).getRGB());
        //GuiScreen.drawRect(x, y + 23, guiWidth, y + 24, new Color(234, 231, 227).getRGB());
        RenderUtil.drawGradientSideways(x, y + 23, guiWidth, y + 24, new Color(31, 52, 135).getRGB(), new Color(116, 76, 236).getRGB());
        //mc.fontRendererObj.drawString("Noctura.lol - " + currentCategory.name(), x + 4, y + 8, -1);

        TTFFontRenderer title = FontManager.getFont("Poppins", 19);
        RenderUtil.drawGradientString(title, "Noctura.lol", x + 4, y + 6, new Color(31, 52, 135), new Color(116, 76, 236));
        title.drawStringWithShadow(" - All In One", x + 58, y + 6, new Color(90, 90, 90).getRGB());

        // draw all selection

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(x - translateAnimX, y, guiWidth, guiHeight);
        int totalCat = -((getCat(currentCategory).width) * categoryWindows.size()) - (x - guiWidth);
        if (Mouse.hasWheel() && RenderUtil.hover(x, y, mouseX, mouseY, guiWidth, 24)) {
            int wheel = Mouse.getDWheel();
            if(wheel > 0){
                lerpScrollX += 36;
            }
            if(wheel < 0){
                lerpScrollX -= 36;
            }
            lerpScrollX = Math.max(Math.min(0, lerpScrollX), totalCat-catPushOffset);
            scrollX = (float) MathHelper.lerp(0.1, scrollX, Math.max(Math.min(0, lerpScrollX), totalCat-catPushOffset));
        }
        int cw = 0;
        int cy = 0;
        int ch = 0;
        for(CategoryWindow c : categoryWindows){
            drawSelection(c);
            cw = c.width;
            cy = c.y;
            ch = c.height;
            if(c.category.equals(currentCategory)){
                c.drawCategoryFrame(mouseX, mouseY);
            }
        }
        for(CategoryWindow c : categoryWindows){
            drawSelText(c);
        }

        GL11.glDisable(3089);
        GL11.glPopMatrix();

        GlStateManager.popMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawSelection(CategoryWindow c){

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(x + catPushOffset - translateAnimX, y, guiWidth, guiHeight);
        GlStateManager.resetColor();
        Gui.drawRect(c.x + scrollX, c.y, c.x + c.width + scrollX, c.y + c.height + 3, new Color(20, 20, 20).getRGB());
        if(true){
           // Gui.drawRect(c.x + scrollX, c.y, c.x + c.width + scrollX, c.y + c.height + 3, new Color(142, 142, 142, 70).getRGB());
        }
        currentCatX = (float) MathHelper.lerp(0.01, currentCatX, getCat(currentCategory).x + scrollX);
        Gui.drawRect(currentCatX, c.y, currentCatX + c.width, c.y + c.height + 3, new Color(53, 53, 53).getRGB());
        GlStateManager.resetColor();
        GL11.glDisable(3089);
        GL11.glPopMatrix();

    }

    public void drawSelText(CategoryWindow c){
        TTFFontRenderer fontString = FontManager.getFont("Poppins", 20);

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(x + catPushOffset - translateAnimX, y, guiWidth, guiHeight);
        fontString.drawStringWithShadow(c.category.name(), c.x + scrollX + (c.width / 2f) - (fontString.getWidth(c.category.name()) / 2f), c.y + (fontString.getHeight(c.category.name())/2f), -1);
        GlStateManager.resetColor();
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (CategoryWindow categoryWindow : categoryWindows) {
            categoryWindow.mouseClicked(mouseX, mouseY, mouseButton);
            if(RenderUtil.hover(categoryWindow.x + scrollX, categoryWindow.y, mouseX, mouseY, categoryWindow.width, categoryWindow.height + 4)){
                currentCategory = categoryWindow.category;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void drawGuiBackground(){
        RenderUtil.drawRect(x, y, guiWidth, guiHeight, new Color(43, 41, 41, 255).getRGB());
    }

    public CategoryWindow getCat(Category c){
        for(CategoryWindow cw : categoryWindows){
            if(cw.category.equals(c))return cw;
        }
        return null;
    }

}
