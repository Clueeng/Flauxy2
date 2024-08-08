package uwu.flauxy.utils.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ColorUtils {

    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    public static int getRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 750)) / (float)(seconds*750);
        int color = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }
    public static Color getRainbowC(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 750)) / (float)(seconds*750);
        Color color = new java.awt.Color(hue, saturation, brightness);
        return color;
    }

    public static Color blend(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }
    public static Color blend(Color color1, Color color2, double offset, float a) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart, a);
    }

    public static int astolfo(float seconds, float saturation, float brightness, float index) {
        float speed = 3000f;
        float hue = (System.currentTimeMillis() % (int) (seconds * 1000)) + index;
        while (hue > speed)
            hue -= speed;
        hue /= speed;
        if (hue > 0.5)
            hue = 0.5F - (hue - 0.5f);
        hue += 0.5F;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }


    public static Color astolfoC(float seconds, float saturation, float brightness, float index) {
        float speed = 3000f;
        float hue = (System.currentTimeMillis() % (int) (seconds * 1000)) + index;
        while (hue > speed)
            hue -= speed;
        hue /= speed;
        if (hue > 0.5)
            hue = 0.5F - (hue - 0.5f);
        hue += 0.5F;
        return new Color(hue, saturation, brightness);
    }

    public static Color blendMultiple(float seconds, long index, Color... colors) {
        if (colors == null || colors.length == 0) {
            throw new IllegalArgumentException("At least one color must be provided");
        }
        if (colors.length == 1) {
            return colors[0];
        }

        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if (hue2 > 1) {
            hue2 = 2 - hue2;
        }

        int numberOfColors = colors.length;
        float step = 1.0f / (numberOfColors - 1);
        int segment = (int)(hue2 / step);
        float blendFactor = (hue2 % step) / step;

        return blend(colors[segment], colors[segment + 1], blendFactor);
    }

    public static Color blend(Color col1, Color col2, float ratio) {
        int r = (int)(col1.getRed() * (1 - ratio) + col2.getRed() * ratio);
        int g = (int)(col1.getGreen() * (1 - ratio) + col2.getGreen() * ratio);
        int b = (int)(col1.getBlue() * (1 - ratio) + col2.getBlue() * ratio);
        int a = (int)(col1.getAlpha() * (1 - ratio) + col2.getAlpha() * ratio);
        return new Color(r, g, b, a);
    }


    public static int blendThing(float seconds, long index, Color col1, Color col2) {
        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if(hue2 > 1) {
            hue2 = 2 - hue2;
        }
        return blend(col1, col2, hue2).getRGB();
    }
    public static Color blendThingC(float seconds, long index, Color col1, Color col2) {
        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if(hue2 > 1) {
            hue2 = 2 - hue2;
        }
        return blend(col1, col2, hue2);
    }
    public static int blendThing(float seconds, long index, Color col1, Color col2, float a) {
        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if(hue2 > 1) {
            hue2 = 2 - hue2;
        }
        return blend(col1, col2, hue2, a).getRGB();
    }
    public static int rainbow(final int count, final float bright, final float st) {
        double v1 = Math.ceil((double)(System.currentTimeMillis() + count * 109)) / 5.0;
        return Color.getHSBColor(((float)((v1 %= 360.0) / 360.0) < 0.5) ? (-(float)(v1 / 360.0)) : ((float)(v1 / 360.0)), st, bright).getRGB();
    }


    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;

        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) | 0xFF000000;
    }

    public static int getColor(final Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(final int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(final int brightness, final int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }
}
