package uwu.noctura.ui.noctura.components;


import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.ui.noctura.ColorHelper;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.animtations.Animate;
import uwu.noctura.utils.animtations.Easing;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static uwu.noctura.utils.font.FontManager.getFont;


public class CategoryFrame implements ColorHelper {

    private int x, y, xDrag, yDrag;
    private int width, height;

    private float offset;

    private boolean drag;

    private final Category category;
    private boolean hideCat;

    private final ArrayList<ModuleFrame> modules;
    private final Animate animation;
    public CategoryFrame(Category category, int x, int y)
    {
        this.category = category;
        this.modules = new ArrayList<>();
        this.animation = new Animate().setEase(Easing.CUBIC_OUT).setSpeed(1200).setMin(0).setMax(defaultWidth / 2F);

        this.x = x;
        this.y = y;
        this.xDrag = 0;
        this.yDrag = 0;
        this.offset = 0;

        this.drag = false;

        this.width = defaultWidth;
        this.height = defaultHeight;

        for (Module module : Noctura.INSTANCE.getModuleManager().getModules(category)) {
            this.modules.add(new ModuleFrame(module, this, 0, 0));
        }
    }

    public void initGui()
    {
        this.animation.setSpeed(200);
    }

    private float targetOffset, currentOffset;

    public void drawScreen(int mouseX, int mouseY)
    {
        // category top draw
        int colT = new Color(169, 88, 211).getRGB();

        RenderUtil.drawRoundedRect2(getX(), getY() - 1, getX() + width, getY() + 15 + 1, 14, colT);
        Gui.drawRect(getX(), getY() + 6, getX() + width, getY() + 15 + 1, colT);


        // background


        if(!hideCat){

            Gui.drawRect(getX() - 1, getY() + 17, getX() + width + 1, getY() + getHeight() + 1 , new Color(0, 0, 0, 45).getRGB());

            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.prepareScissorBox(getX() - 1, getY() + 17, getX() + width + 1, getY() + getHeight() + 1);
            GaussianBlur.renderBlur(8f);

            Gui.drawRect(getX() - 1, getY() + 17, getX() + width + 1, getY() + getHeight() + 1, new Color(0, 0, 0, 45).getRGB());

            GL11.glDisable(3089);
            GL11.glPopMatrix();
            StencilUtil.uninitStencilBuffer();


            AtomicInteger offCat = new AtomicInteger();
            this.modules.forEach(module -> offCat.addAndGet(module.getOffset()+1));

            // Calculate height
            height = Math.min(categoryNameHeight + offCat.get(), defaultHeight);
            if (Mouse.hasWheel() && RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, height)) {
                int wheel = Mouse.getDWheel();
                if (wheel > 0 && targetOffset > 0) {
                    targetOffset -= moduleHeight;
                } else if (wheel < 0 && targetOffset + (moduleHeight - 1) <= offCat.get() - height + categoryNameHeight) {
                    targetOffset += moduleHeight;
                }
            }
            currentOffset = (float) MathHelper.lerp(0.1, currentOffset, targetOffset);
            if (currentOffset >= offCat.get() - height + categoryNameHeight) {
                targetOffset -= 2;
            }
            if(currentOffset < 0){
                targetOffset = 0;
            }
        }

        getFont().drawStringWithShadow(category.name(), x + 2, y + (categoryNameHeight / 2f - getFont().getHeight("A") / 2f), stringColor);

        // Drag ClickGUI
        if(drag) {
            setX(this.xDrag + mouseX);
            setY(this.yDrag + mouseY);
        }
        GlStateManager.color(1f,1f,1f,1f);

        // Drawing category name
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(getX() + (width / 2F) - animation.update().getValue(), getY() + categoryNameHeight, x + (width / 2F) + animation.getValue(), y + getHeight());

        GlStateManager.color(1f,1f,1f,1f);
        // Drawing modules
        int i = 0;
        if(!hideCat){
            for (ModuleFrame module : this.modules) {
                module.setX(x);
                module.setY((int) (y + categoryNameHeight + i - currentOffset));
                module.drawScreen(mouseX, mouseY);
                i += module.getOffset();
            }
        }

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        Gui.drawRect(getX(), getY() + 15, getX() + width, getY() + 16, new Color(255, 255, 255).getRGB());
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // I really need to explain?
        for (ModuleFrame module : this.modules)
        {
            if(module.mouseClicked(mouseX, mouseY, mouseButton) && !hideCat) {
                setDrag(false);
                return;
            }
        }
        if(mouseButton == 1 && RenderUtil.hover(x, y, mouseX, mouseY, width, categoryNameHeight)){
            hideCat = !hideCat;
        }

        if(RenderUtil.hover(x, y, mouseX, mouseY, width, height) && mouseButton == 0)
        {
            setDrag(true);
            setXDrag(getX() - mouseX);
            setYDrag(getY() - mouseY);
        } else{
            setDrag(false);
        }

    }

    @SuppressWarnings("unused")
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(state == 0){
            this.drag = false;
        }
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY()
    {
        return y;
    }

    public void setXDrag(int xDrag)
    {
        this.xDrag = xDrag;
    }

    public void setYDrag(int yDrag)
    {
        this.yDrag = yDrag;
    }

    public void setDrag(boolean drag)
    {
        this.drag = drag;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
