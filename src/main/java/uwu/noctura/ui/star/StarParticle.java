package uwu.noctura.ui.star;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import uwu.noctura.utils.render.RenderUtil;

public class StarParticle {
    private float x, y;
    private float alpha;
    private float size;
    private float alphaChangeRate;

    public StarParticle(float x, float y, float size, float alphaChangeRate) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.alphaChangeRate = alphaChangeRate;
        this.alpha = new Random().nextFloat();
    }

    public void update() {
        alpha += alphaChangeRate;
        if (alpha > 1.0f) {
            alpha = 1.0f;
            alphaChangeRate = -alphaChangeRate;
        } else if (alpha < 0.01f) {
            alpha = 0.01f;
            alphaChangeRate = -alphaChangeRate;
        }
    }

    public void render(float mouseX, float mouseY, List<StarParticle> allParticles) {
        // Set up blending and color for the star
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set star color
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);

        // Draw the star
        drawStar(x, y,size);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    private static double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private void drawStar(float x, float y, float size) {
        RenderUtil.drawFilledCircle(x, y, MathHelper.clamp_float(this.size, 0.5f, 1.0f), new Color(255,255,255, MathHelper.clamp_int((int)(alpha * 255f), 0, 255)));
    }

    public static List<StarParticle> getNearestParticles(float mouseX, float mouseY, List<StarParticle> allParticles, int count) {
        // Sort particles by distance from the mouse and select the nearest ones
        List<StarParticle> sortedParticles = new ArrayList<>(allParticles);
        sortedParticles.sort(Comparator.comparingDouble(p -> distance(mouseX, mouseY, p.x, p.y)));
        return sortedParticles.subList(0, Math.min(count, sortedParticles.size()));
    }

    public static void drawLinesToNearestParticles(float mouseX, float mouseY, List<StarParticle> nearestParticles) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set line color to white with 40% opacity
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);

        GL11.glBegin(GL11.GL_LINES);
        for (StarParticle particle : nearestParticles) {
            float distance = (float) Math.sqrt(Math.pow(mouseX - particle.x, 2) + Math.pow(mouseY - particle.y, 2));
            // Draw lines only if within max distance (e.g., 100 pixels)
            if (distance < 100) {
                GL11.glVertex2f(mouseX, mouseY);
                GL11.glVertex2f(particle.x, particle.y);
            }
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
