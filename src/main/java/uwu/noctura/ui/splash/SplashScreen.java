package uwu.noctura.ui.splash;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.utils.font.TTFFontRenderer;

import java.awt.*;

import static uwu.noctura.utils.MoveUtils.mc;

public class SplashScreen {

    /*
    This class is from eric golde's tutorial, credits to him
     */

    public static final int MAX = 7;
    public static int PROGRESS = 0;
    public static String CURRENT = "";
    public static ResourceLocation splash;
    public static UnicodeFontRenderer font;

    public static void update(){
        if(mc == null || mc.getLanguageManager() == null){
            return;
        }
        draw(mc.getTextureManager());
    }

    public static void set(int x, String info){
        PROGRESS = x;
        CURRENT = info;
        update();
    }

    public static void draw(TextureManager tm){
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();

        Framebuffer fb = new Framebuffer(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor, true);
        fb.bindFramebuffer(false);

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double) sr.getScaledWidth(), (double) sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        if(splash == null){
            splash = new ResourceLocation("background/bg_1.png");
        }

        tm.bindTexture(splash);

        GlStateManager.resetColor();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, sr.getScaledWidth(), sr.getScaledHeight(), 1920, 1080);
        drawProg();
        fb.unbindFramebuffer();
        fb.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);

        mc.updateDisplay();
    }

    private static void drawProg(){
        if(mc.gameSettings == null || mc.getTextureManager() == null){
            return;
        }
        if(font == null){
            font = UnicodeFontRenderer.getFontOnPC("Arial", 20);
        }
        ScaledResolution sr = new ScaledResolution(mc);

        double nProgress = (double) PROGRESS;
        double calc = (nProgress / MAX) * sr.getScaledWidth();

        Gui.drawRect(0, sr.getScaledHeight() - 35, sr.getScaledWidth(), sr.getScaledHeight(), new Color(90, 90, 90, 58).getRGB());
        GlStateManager.resetColor();
        resetState();

        font.drawStringWithShadow(CURRENT, 20,  sr.getScaledHeight() - 25, -1);

        String step = PROGRESS + " / " + MAX;
        font.drawStringWithShadow(step, (sr.getScaledWidth() / 2f) - (font.getStringWidth(step) / 2f), sr.getScaledHeight() - 25, new Color(68, 68, 68).getRGB());
        GlStateManager.resetColor();
        resetState();

        Gui.drawRect(0, sr.getScaledHeight() - 2, (float)calc, sr.getScaledHeight(), new Color(73, 147, 191).getRGB());
        Gui.drawRect(0, sr.getScaledHeight() - 2, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 10).getRGB());

    }
    private static void resetState(){
        GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = -1;
    }

}
