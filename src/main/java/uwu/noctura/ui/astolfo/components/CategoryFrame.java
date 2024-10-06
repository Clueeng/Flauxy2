package uwu.noctura.ui.astolfo.components;


import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.ui.astolfo.ColorHelper;
import uwu.noctura.utils.animtations.Animate;
import uwu.noctura.utils.animtations.Easing;
import uwu.noctura.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static uwu.noctura.utils.font.FontManager.getFont;


public class CategoryFrame implements ColorHelper {

    // Stuff
    private int x, y, xDrag, yDrag;
    private int width, height;

    private int offset; // Used to scroll

    private boolean drag;

    private final Category category;

    private final ArrayList<ModuleFrame> modules;

    // Smooth animation
    private final Animate animation;

    // Asking x and y so categories are not on themself
    public CategoryFrame(Category category, int x, int y)
    {
        this.category = category;
        this.modules = new ArrayList<>();
        this.animation = new Animate().setEase(Easing.CUBIC_OUT).setSpeed(250).setMin(0).setMax(defaultWidth / 2F);

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
        this.animation.setSpeed(30);
    }

    public void drawScreen(int mouseX, int mouseY)
    {

        Gui.drawRect(getX() - 2, getY() - 2, getX() + width + 2, getY() + getHeight() + 2, category.getCategoryColor().getRGB());
        Gui.drawRect(getX() - 1, getY() - 1, getX() + width + 1, getY() + getHeight() + 1, darkerMainColor);
        AtomicInteger offCat = new AtomicInteger();
        this.modules.forEach(module -> offCat.addAndGet(module.getOffset()+1));

        // Calculate height
        height = Math.min(categoryNameHeight + offCat.get(), defaultHeight);

        if(Mouse.hasWheel() && RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, height))
        {
            int wheel = Mouse.getDWheel();
            if(wheel > 0 && offset - (moduleHeight - 1) > 0) {
                offset -= moduleHeight;
            } else if(wheel < 0 && offset + (moduleHeight - 1) <= offCat.get() - height + categoryNameHeight) {
                offset += moduleHeight;
            }
        }

        // Drawing category base
        // Drawing category name rect thing

        RenderUtil.drawRoundedRect2(getX(), getY(), getX() + width, getY() + getHeight(), 5, mainColor);
        RenderUtil.drawRoundedRect2(getX(), getY(), getX() + width, getY() + categoryNameHeight, 5, darkerMainColor);
        //Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(category.name().toLowerCase(), x + 2, (int) (y + ((categoryNameHeight / 2F) - getFont().getHeight("A") / 2F) + 1), stringColor);
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
        for (ModuleFrame module : this.modules)
        {
            module.setX(x);
            module.setY(y + categoryNameHeight + i - offset);
            module.drawScreen(mouseX, mouseY);
            i += module.getOffset();
        }


        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // I really need to explain?
        for (ModuleFrame module : this.modules)
        {
            if(module.mouseClicked(mouseX, mouseY, mouseButton)) {
                setDrag(false);
                return;
            }
        }

        if(RenderUtil.hover(x, y, mouseX, mouseY, width, height) && mouseButton == 0)
        {
            setDrag(true);
            setXDrag(getX() - mouseX);
            setYDrag(getY() - mouseY);
        } else
            setDrag(false);
    }

    @SuppressWarnings("unused")
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.drag = false;
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
