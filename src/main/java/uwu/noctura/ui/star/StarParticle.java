package uwu.noctura.ui.star;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.utils.render.RenderUtil;

@Getter
public class StarParticle {

    public StarParticle setX(float x) {
        this.x = x;
        return this;
    }

    public StarParticle setY(float y) {
        this.y = y;
        return this;
    }

    public StarParticle setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public StarParticle setSize(float size) {
        this.size = size;
        return this;
    }

    public StarParticle setAlphaChangeRate(float alphaChangeRate) {
        this.alphaChangeRate = alphaChangeRate;
        return this;
    }

    public StarParticle setVelocityY(float velocityY) {
        this.velocityY = velocityY;
        return this;
    }

    public StarParticle setVelocityX(float velocityX) {
        this.velocityX = velocityX;
        return this;
    }

    public StarParticle setOldVelocityX(float oldVelocityX) {
        this.oldVelocityX = oldVelocityX;
        return this;
    }

    public StarParticle setOldVelocityY(float oldVelocityY) {
        this.oldVelocityY = oldVelocityY;
        return this;
    }

    public float x, y;
    public float alpha;
    public float size;
    public float alphaChangeRate;
    public float velocityY, velocityX;
    public float oldVelocityX, oldVelocityY;
    public int color;

    public StarParticle(float x, float y, float size, float alphaChangeRate) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.alphaChangeRate = alphaChangeRate;
        this.alpha = new Random().nextFloat();
        this.velocityY = ((new Random().nextFloat()) - 0.5f) * new Random().nextFloat();
        this.velocityX = ((new Random().nextFloat()) - 0.5f) * new Random().nextFloat();
        this.oldVelocityX = velocityX;
        this.oldVelocityY = velocityY;
        this.color = -1;
    }
    public StarParticle(float x, float y) {
        this.x = x;
        this.y = y;
        this.alpha = new Random().nextFloat();
        this.velocityY = ((new Random().nextFloat()) - 0.5f) * new Random().nextFloat();
        this.velocityX = ((new Random().nextFloat()) - 0.5f) * new Random().nextFloat();
        this.oldVelocityX = velocityX;
        this.oldVelocityY = velocityY;
        this.color = -1;
    }

    public StarParticle(float x, float y, float size, float alphaChangeRate, int color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.alphaChangeRate = alphaChangeRate;
        this.alpha = new Random().nextFloat();
        this.velocityY = ((new Random().nextFloat()) - 0.5f) * new Random().nextFloat();
        this.velocityX = ((new Random().nextFloat()) - 0.5f) * new Random().nextFloat();
        this.oldVelocityX = velocityX;
        this.oldVelocityY = velocityY;
        this.color = color;
    }

    public StarParticle setColor(int col){
        this.color = col;
        return this;
    }

    public void setColorInstant(int col){
        this.color = col;
    }

    public StarParticle setColor(Color c){
        this.color = c.getRGB();
        return this;
    }

    private void resetPosition(int width, int height) {
        Random random = new Random();
        x = random.nextInt(width);
        y = height + random.nextInt(100);
    }
    private void resetPosition(int minX, int minY, int maxX, int maxY) {
        Random random = new Random();
        x = minX + random.nextFloat() * (maxX - minX);
        y = minY + random.nextFloat() * (maxY - minY);
    }

    public void update(int width, int height, int mouseX, int mouseY) {
        alpha += alphaChangeRate;
        if (alpha > 1.0f) {
            alpha = 1.0f;
            alphaChangeRate = -alphaChangeRate;
        } else if (alpha < 0.01f) {
            alpha = 0.01f;
            alphaChangeRate = -alphaChangeRate;
        }
        y -= velocityY;
        x -= velocityX;
        if (y < -200 || y >= height + 200 || x <= -200 || x >= width + 200) {
            resetPosition(width, height);
        }
    }
    public void update(int minX, int minY, int maxX, int maxY, List<StarParticle> particles) {

        alpha = MathHelper.clamp_float((float) uwu.noctura.utils.MathHelper.lerp(alphaChangeRate, alpha, 0), 0, 1);
        y -= velocityY;
        x -= velocityX;
        if (x < minX || x > maxX || y < minY || y > maxY) {
            resetPosition(minX, minY, maxX, maxY);
        }

    }

    public void render(float mouseX, float mouseY, List<StarParticle> allParticles) {
        float dx = mouseX - x;
        float dy = mouseY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float parallaxFactor = 0.04f;
        float newX = x + dx * parallaxFactor;
        float newY = y + dy * parallaxFactor;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Color col = new Color(this.color);
        GL11.glColor4f(col.getRed() / 255f, col.getGreen() / 255f, col.getBlue() / 255f, alpha);
        // i will violently touch myself if this does not work
        drawStar(newX, newY, size);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    public void render() {
        float newX = x;
        float newY = y;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Color col = new Color(this.color);
        GL11.glColor4f(col.getRed() / 255f, col.getGreen() / 255f, col.getBlue() / 255f, alpha);
        drawStar(newX, newY, size, this.color);

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

    private void drawStar(float x, float y, float size, int col) {
        RenderUtil.drawFilledCircle(x, y, MathHelper.clamp_float(this.size, 0.5f, 1.0f), new Color(col));
    }

    public static List<StarParticle> getNearestParticles(float mouseX, float mouseY, List<StarParticle> allParticles, int count) {
        List<StarParticle> sortedParticles = new ArrayList<>(allParticles);
        sortedParticles.sort(Comparator.comparingDouble(p -> distance(mouseX, mouseY, p.x, p.y)));
        return sortedParticles.subList(0, Math.min(count, sortedParticles.size()));
    }

    public static void drawLinesToNearestParticles(float mouseX, float mouseY, List<StarParticle> nearestParticles) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);

        GL11.glBegin(GL11.GL_LINES);
        for (StarParticle particle : nearestParticles) {
            float distance = (float) Math.sqrt(Math.pow(mouseX - particle.x, 2) + Math.pow(mouseY - particle.y, 2));
            if (distance < 100) {
                float parallaxFactor = 0.04f;
                float dx = mouseX - particle.x;
                float dy = mouseY - particle.y;
                float newX = particle.x + dx * parallaxFactor;
                float newY = particle.y + dy * parallaxFactor;
                GL11.glVertex2f(mouseX, mouseY);
                GL11.glVertex2f(newX, newY);
            }
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
