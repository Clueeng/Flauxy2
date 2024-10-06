package uwu.noctura.module.impl.display.keystrokes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.module.impl.ghost.AutoClicker;
import uwu.noctura.utils.MathHelper;

import java.awt.*;

public class Keystroke {
    public KeyBinding keyBinding;
    public int white = 0, whiteEnd = 0; // 0 to 255
    public boolean pressed;
    public float relativeX, relativeY, size = 1.25f;

    public Keystroke(KeyBinding keyBinding, int white, float relativeX, float relativeY){
        this.keyBinding = keyBinding;
        this.white = white;
        this.pressed = false;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public void updatePressed(){
        Minecraft mc = Minecraft.getMinecraft();
        AutoClicker ac = Noctura.INSTANCE.getModuleManager().getModule(AutoClicker.class);
        if(keyBinding.equals(mc.gameSettings.keyBindAttack) || keyBinding.equals(mc.gameSettings.keyBindUseItem) || ac.isClickingLeft || ac.isClickingRight ){
            if(keyBinding.equals(mc.gameSettings.keyBindAttack) || ac.isClickingLeft){
                this.pressed = mc.gameSettings.keyBindAttack.isKeyDown() || ac.isClickingLeft;
            }else if(keyBinding.equals(mc.gameSettings.keyBindUseItem) || ac.isClickingRight){
                this.pressed = mc.gameSettings.keyBindUseItem.isKeyDown() || ac.isClickingRight;
            }
        }else{
            this.pressed = Keyboard.isKeyDown(this.keyBinding.getKeyCode());
        }
    }

    public boolean pressed(){
        return this.pressed;
    }

    public void setPressed(boolean pressed){
        this.pressed = pressed;
    }

    public void clamp_color(){
        this.white = (int)net.minecraft.util.MathHelper.clamp_float(this.white,0,254);
        this.whiteEnd = (int)net.minecraft.util.MathHelper.clamp_float(this.white,0,254);
    }

    public void render(float absoluteX, float absoluteY, int pressCol){
        String keyString = Keyboard.getKeyName(this.keyBinding.getKeyCode());
        clamp_color();
        float expandSize = 8.0f;
        float size = getSize();
        float renderedRelativeX = (this.relativeX) * (size);
        float renderedRelativeY = (this.relativeY) * (size);
        expandSize = expandSize * (size);
        //size(size,absoluteX,absoluteY);
        Gui.drawRect(absoluteX - expandSize + renderedRelativeX,absoluteY - expandSize + renderedRelativeY, absoluteX + expandSize + renderedRelativeX,absoluteY + expandSize + renderedRelativeY
                ,new Color(this.white, this.white, this.white, 90).getRGB());
        float textExpand = expandSize / size;
        if(this.pressed){
            pressCol = pressCol;
        }else{
            pressCol = new Color(255,255,255).getRGB();
        }
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(keyString,absoluteX + (textExpand / 2.0f) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(keyString)) + (renderedRelativeX),
                absoluteY + (textExpand / 2.0f) - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT/1.25f) + renderedRelativeY, pressCol);
        //GlStateManager.popMatrix();
    }
    public void renderClicks(float absoluteX, float absoluteY, float cpsLeft, float cpsRight, int pressCol){
        String keyString = keyBinding.equals(Minecraft.getMinecraft().gameSettings.keyBindAttack) ? "L" : "R";
        String cps = keyString.equals("L") ? String.valueOf(cpsLeft) : String.valueOf(cpsRight);
        clamp_color();
        float expandSize = 8.0f;
        float x = absoluteX + (expandSize / 2.0f) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(keyString)) + relativeX;
        float y = absoluteY + (expandSize / 2.0f) - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + relativeY;
        float expandSizeX = 4.0f;
        float size = getSize();
        float renderedRelativeX = (this.relativeX) * (size);
        float renderedRelativeY = (this.relativeY) * (size);
        expandSize = expandSize * (size);
        //size(size,absoluteX,absoluteY);
        Gui.drawRect(absoluteX - expandSize + renderedRelativeX,absoluteY - expandSize + renderedRelativeY, absoluteX + expandSize + renderedRelativeX + (expandSizeX * 2 * size),absoluteY + expandSize + renderedRelativeY
                ,new Color(this.white, this.white, this.white, 90).getRGB());
        float textExpand = expandSize / size;
        if(this.pressed){
            pressCol = pressCol;
        }else{
            pressCol = new Color(255,255,255).getRGB();
        }
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(keyString,absoluteX + (textExpand) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(keyString)) + (renderedRelativeX),
                absoluteY + (textExpand / 2.0f) - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT*1.5f) + renderedRelativeY, pressCol);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("[" + cps + "]",absoluteX + (textExpand) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth("[" + cps + "]")/1.75f) + (renderedRelativeX),
                absoluteY + (textExpand / 2.0f) - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT/2.75f) + renderedRelativeY, pressCol);

        //GlStateManager.translate(-x,-y,1.0f);
        //GlStateManager.popMatrix();
    }

    public void size(float amplifier, float absoluteX, float absoluteY){
        boolean isLeft = keyBinding.equals(Minecraft.getMinecraft().gameSettings.keyBindAttack);
        boolean isRight = keyBinding.equals(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
        boolean isKeyMouse = isLeft || isRight;
        String keyString = isKeyMouse ? isLeft ? "L" : "R" : Keyboard.getKeyName(this.keyBinding.getKeyCode());
        float expandSize = 8.0f;
        float x = absoluteX + (expandSize / 2.0f) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(keyString)) + relativeX;
        float y = absoluteY + (expandSize / 2.0f) - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + relativeY;
        GlStateManager.pushMatrix();

        //GlStateManager.translate(x,y,1.0f);
        GlStateManager.scale(amplifier,amplifier,amplifier);
    }

    public float getSize(){
        return size;
    }
    public void setSize(float size){
        this.size = size;
    }

    public void whiten(int whiteGoal){
        this.whiteEnd = whiteGoal;
        this.white = (int) MathHelper.lerp(0.25f,this.white,this.whiteEnd);
    }

}
