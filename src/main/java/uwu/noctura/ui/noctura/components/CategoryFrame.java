package uwu.noctura.ui.noctura.components;


import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
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

    @Getter
    @Setter
    private float x, y, xDrag, yDrag;
    @Getter
    private int width, height;
    private float velocityX, velocityY;

    private float offset;

    @Setter
    private boolean drag;

    private final Category category;
    public boolean hideCat;

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
    private float targetCloseAnim, currentCloseAnim;
    private int prevMouseX, prevMouseY;
    private float rotationAngle;

    public void drawScreen(int mouseX, int mouseY)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(getX() + width / 2f, getY() + 7.5f, 0); // Translate to the center of the category tab
        GL11.glRotatef(rotationAngle, 0, 0, 1); // Rotate around the Z-axis
        GL11.glTranslatef(-(getX() + width / 2f), -(getY() + 7.5f), 0); // Translate back
        if (drag) {
            int deltaX = mouseX - prevMouseX;
            int deltaY = mouseY - prevMouseY;
            float velocity = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            float targetAngle = Math.min(velocity * 0.8f, 75.0f) * (deltaX < 0 ? -1 : 1);
            // Interpolate smoothly towards the target angle
            rotationAngle = (float) MathHelper.lerp(0.1, rotationAngle, targetAngle);
        } else {
            // Smoothly interpolate towards 0 when not dragging
            rotationAngle = (float) MathHelper.lerp(0.1, rotationAngle, 0);
        }

        // category top draw
        int colT = new Color(169, 88, 211).getRGB();



        RenderUtil.drawRoundedRect2(getX(), getY() - 1, getX() + width, getY() + 15 + 1, 14, colT);

        getFont().drawStringWithShadow(category.name(), x + 2, y + (categoryNameHeight / 2f - getFont().getHeight("A") / 2f), stringColor);


        RenderUtil.drawRoundedRect2(getX(), getY() - 1, getX() + width, getY() + 15 + 1, 14, colT);
        Gui.drawRect(getX(), getY() + 6, getX() + width, getY() + 15 + 1, colT);
        getFont().drawStringWithShadow(category.name(), x + 2, y + (categoryNameHeight / 2f - getFont().getHeight("A") / 2f), stringColor);


        // background

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(getX() - 1, getY() + 17, getX() + width + 1, Math.max(currentCloseAnim, getY() + 17));
        Gui.drawRect(getX() - 1, getY() + 17, getX() + width + 1, currentCloseAnim , new Color(0, 0, 0, 45).getRGB());
        GaussianBlur.renderBlur(8f);

        Gui.drawRect(getX() - 1, getY() + 17, getX() + width + 1, getY() + getHeight() + 1, new Color(0, 0, 0, 45).getRGB());

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();
        if(!hideCat){
            handleScrolling(mouseX, mouseY);
            targetCloseAnim = getY() + getHeight() + 1;
        }else{
            targetCloseAnim = getY() + categoryNameHeight + 1;
            if(currentCloseAnim < getY() + categoryNameHeight + 2){
                currentCloseAnim = getY() + categoryNameHeight + 1;
            }
            if(currentCloseAnim > getY() + categoryNameHeight + 2 && currentCloseAnim < getY() + categoryNameHeight + 4){
                System.out.println(getY() + categoryNameHeight + 2);
                System.out.println(currentCloseAnim);
                currentCloseAnim = getY() + categoryNameHeight + 2;
            }
        }
        currentCloseAnim = (float) MathHelper.lerp(0.025, currentCloseAnim, targetCloseAnim);
        if(currentCloseAnim > getY() + getHeight() + 1){
            currentCloseAnim = getY() + getHeight() + 1;
        }
        int safeguard = hideCat ? 2 : 30;
        if(currentCloseAnim < getY() + categoryNameHeight + safeguard){
            currentCloseAnim = getY() + categoryNameHeight + safeguard;
        }
        if(this.category.equals(Category.Display)){
        }




        // Drag ClickGUI
        if(drag) {
            velocityX = (mouseX - this.prevMouseX) * 1.0f;
            velocityY = (mouseY - this.prevMouseY) * 1.0f;

            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            float w = (float)(sr.getScaledWidth());
            float h = (float)(sr.getScaledHeight());

            setX(net.minecraft.util.MathHelper.clamp_float(this.xDrag + mouseX, 0, w - width));
            setY(net.minecraft.util.MathHelper.clamp_float(this.yDrag + mouseY, 0, h - categoryNameHeight));


            System.out.println(mouseX + " " + prevMouseX);
        }else{
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            float w = (float)(sr.getScaledWidth());
            float h = (float)(sr.getScaledHeight());

            setX(net.minecraft.util.MathHelper.clamp_float((getX() + velocityX), 0, w - width));
            setY(net.minecraft.util.MathHelper.clamp_float((getY() + velocityY), 0, h - categoryNameHeight));
            velocityX = (float) MathHelper.lerp(0.1, velocityX, 0);
            velocityY = (float) MathHelper.lerp(0.1, velocityY, 0);
            if(Math.abs(velocityX) < 0.00045f){
                velocityX = 0;
            }
            if(Math.abs(velocityY) < 0.00045f){
                velocityY = 0;
            }

        }
        GlStateManager.color(1f,1f,1f,1f);

        // modules with scissorbox
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(getX() + (width / 2F) - animation.update().getValue(), getY() + categoryNameHeight, x + (width / 2F) + animation.getValue(), y + getHeight());

        handleModuleRendering(mouseX, mouseY);


        GL11.glDisable(3089);
        GL11.glPopMatrix();
        Gui.drawRect(getX(), getY() + 15, getX() + width, getY() + 16, new Color(255, 255, 255).getRGB());
        // Drawing category name

        // End rotation
        GL11.glPopMatrix();
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // I really need to explain?
        for (ModuleFrame module : this.modules)
        {
            if(!hideCat){
                if(module.mouseClicked(mouseX, mouseY, mouseButton) && !hideCat) {
                    setDrag(false);
                    return;
                }
            }
        }
        if(mouseButton == 1 && RenderUtil.hover(x, y, mouseX, mouseY, width, categoryNameHeight)){
            hideCat = !hideCat;
        }

        if(RenderUtil.hover(x, y, mouseX, mouseY, width, categoryNameHeight) && mouseButton == 0)
        {
            setDrag(true);
            setXDrag(getX() - mouseX);
            setYDrag(getY() - mouseY);
        } else{
            setDrag(false);
        }

    }

    public void handleScrolling(int mouseX, int mouseY){
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

    public void keyTyped(char c, int keycode){
        for(ModuleFrame m : modules){
            m.keyTyped(c, keycode);
        }
    }

    public void handleModuleRendering(int mouseX, int mouseY){
        // draw modules on top of bg
        GlStateManager.color(1f,1f,1f,1f);
        int i = 0;

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(getX() - 1, getY() + 17, getX() + width + 1, Math.max(currentCloseAnim, getY() + 17));

        for (ModuleFrame module : this.modules) {
            module.setX(x);
            module.setY((int) (y + categoryNameHeight + i - currentOffset));
            module.drawScreen(mouseX, mouseY);
            i += module.getOffset();
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();

    }

    @SuppressWarnings("unused")
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(state == 0){
            this.drag = false;
        }
    }

    public void setXDrag(float xDrag)
    {
        this.xDrag = xDrag;
    }

    public void setYDrag(float yDrag)
    {
        this.yDrag = yDrag;
    }

}
