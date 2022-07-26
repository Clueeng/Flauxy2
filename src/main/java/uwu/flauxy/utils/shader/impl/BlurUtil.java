package uwu.flauxy.utils.shader.impl;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.utils.render.RenderUtil;

import java.io.IOException;

public final class BlurUtil {

    private static final Minecraft MC = Minecraft.getMinecraft();
    private final ResourceLocation resourceLocation;
    private ShaderGroup shaderGroup;
    private Framebuffer framebuffer;

    private int lastFactor;
    private int lastWidth;
    private int lastHeight;

    public BlurUtil() {
        this.resourceLocation = new ResourceLocation("Flauxy/blur.json");
    }

    public void init() {
        try {
            this.shaderGroup = new ShaderGroup(MC.getTextureManager(), MC.getResourceManager(), MC.getFramebuffer(), resourceLocation);
            this.shaderGroup.createBindFramebuffers(MC.displayWidth, MC.displayHeight);
            this.framebuffer = shaderGroup.mainFramebuffer;
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setValues(int strength) {
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(strength);
        this.shaderGroup.getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(strength);
        this.shaderGroup.getListShaders().get(2).getShaderManager().getShaderUniform("Radius").set(strength);
        this.shaderGroup.getListShaders().get(3).getShaderManager().getShaderUniform("Radius").set(strength);
    }

    public void blur(int blurStrength) {
        final ScaledResolution scaledResolution = new ScaledResolution(MC);

        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }

        this.lastFactor = scaleFactor;
        this.lastWidth = width;
        this.lastHeight = height;

        setValues(blurStrength);
        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(MC.timer.renderPartialTicks);
        MC.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableAlpha();
    }

    public void blur(double x, double y, double areaWidth, double areaHeight, int blurStrength) {
        final ScaledResolution scaledResolution = new ScaledResolution(MC);

        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }

        this.lastFactor = scaleFactor;
        this.lastWidth = width;
        this.lastHeight = height;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.prepareScissorBox((int) x, (int) (y + 1), (int) areaWidth, (int) (areaHeight - 1));
        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(MC.timer.renderPartialTicks);
        setValues(blurStrength);
        MC.getFramebuffer().bindFramebuffer(false);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);


    }

    private boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return (lastFactor != scaleFactor || lastWidth != width || lastHeight != height);
    }

}

