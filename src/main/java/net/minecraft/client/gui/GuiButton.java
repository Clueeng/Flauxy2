package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.render.RoundedUtils;

import java.awt.*;

public class GuiButton extends Gui
{
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

    /** Button width in pixels */
    protected int width;
    private long lastTime = System.nanoTime(); // Initialisez avec le temps actuel
    private float deltaTime = 0f;


    protected float smoothnessX = 1, offsetX = 0;

    /** Button height in pixels */
    protected int height;

    /** The x position of this control. */
    public int xPosition;

    /** The y position of this control. */
    public int yPosition;

    /** The string displayed on this control. */
    public String displayString;
    public int id;

    /** True if this control is enabled, false to disable. */
    public boolean enabled;

    /** Hides the button completely if false. */
    public boolean visible;
    protected boolean hovered;

    public GuiButton(int buttonId, int x, int y, String buttonText)
    {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            /*this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);*/
            long now = System.nanoTime();
            deltaTime = (now - lastTime) / 1_000_000_000.0f; // Convertit en secondes
            lastTime = now;
            float adjustment = deltaTime * 60;
            if(enabled){
                if(hovered){
                    //this.smoothnessX = Math.max(0.982f, this.smoothnessX * 0.95f);
                    //this.offsetX += (1 / this.smoothnessX) * adjustment;
                    this.offsetX = (float) uwu.noctura.utils.MathHelper.lerp(0.1, offsetX, .75);
                    this.smoothnessX = (float) uwu.noctura.utils.MathHelper.lerp(0.01, smoothnessX, 255);
                }else{
                    //this.smoothnessX = Math.min(1f, this.smoothnessX * 1.05f);
                    //this.offsetX -= (1 * this.smoothnessX) * adjustment;
                    this.offsetX = (float) uwu.noctura.utils.MathHelper.lerp(0.1, offsetX, 0);
                    this.smoothnessX = (float) uwu.noctura.utils.MathHelper.lerp(0.1, smoothnessX, 0);
                }
            }
            this.offsetX = MathHelper.clamp_float(this.offsetX, 0, 4);
            RoundedUtils.drawRoundedOutline(this.xPosition + (this.offsetX * 4), this.yPosition, this.xPosition + this.width - (this.offsetX * 4), this.yPosition + 19, 4, 2, new Color(0, 0, 0, 250).getRGB());
            RoundedUtils.drawRoundedOutline(this.xPosition + (this.offsetX * 4), this.yPosition, this.xPosition + this.width - (this.offsetX * 4), this.yPosition + 19, 4, 2, this.enabled ? -1 : new Color(91, 72, 72).getRGB());
            FontManager.getFont().drawStringWithShadow(">", this.xPosition + (this.offsetX * 4) + 4, (this.yPosition + (this.height - 8) / 2f) - 2, new Color(255, 255, 255, (int)smoothnessX).getRGB());
            FontManager.getFont().drawStringWithShadow("<", this.xPosition + this.width - (this.offsetX * 4) - 12, (this.yPosition + (this.height - 8) / 2f) - 2, new Color(255, 255, 255, (int)smoothnessX).getRGB());

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            FontManager.getFont().drawStringWithShadow(this.displayString, (this.xPosition + (this.width / 2) - FontManager.getFont().getWidth(this.displayString) / 2), (this.yPosition + (this.height - 8) / 2) - 2, -1);
            //this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, (this.yPosition + (this.height - 8) / 2) - 1, j);
        }
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    /**
     * Whether the mouse cursor is currently over the button.
     */
    public boolean isMouseOver()
    {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY)
    {
    }

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
}
