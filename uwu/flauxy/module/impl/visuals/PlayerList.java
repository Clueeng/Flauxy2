package uwu.flauxy.module.impl.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

import java.awt.*;

import static uwu.flauxy.utils.render.ColorUtils.getGradientOffset;

@ModuleInfo(name = "PlayerList", displayName = "Player List", key = 0, cat = Category.Visuals)
public class PlayerList extends Module {

    static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    private ModeSetting color = new ModeSetting("Color", "Rainbow", "Custom", "Rainbow");
    private NumberSetting red = new NumberSetting("Red", 33, 0, 250, 1).setCanShow(m -> color.is("Custom"));
    private NumberSetting green = new NumberSetting("Green", 94, 0, 250, 1).setCanShow(m -> color.is("Custom"));
    private NumberSetting blue = new NumberSetting("Blue", 181, 0, 250, 1).setCanShow(m -> color.is("Custom"));
    private NumberSetting X = new NumberSetting("X", 3, 0, sr.getScaledWidth() * 2, 1);
    private NumberSetting Y = new NumberSetting("Y", 45, 0, sr.getScaledHeight() * 2, 1);
    private NumberSetting Alpha = new NumberSetting("Alpha", 125, 0, 255, 1);
    private BooleanSetting distance = new BooleanSetting("Distance", false);
    private BooleanSetting hp = new BooleanSetting("Health", false);
    private BooleanSetting validname = new BooleanSetting("Valid Name", false);

    private double x, y;

    public PlayerList() {
        this.addSettings(color,distance, hp , validname,  X, Y, Alpha, red, green, blue);
    }

    public void onEvent(Event e) {
        if (e instanceof EventRender2D) {
            float offset = 17;
            setX(X.getValue());
            setY(Y.getValue());

            drawRect(3 + getX(), -1 + getY(), 151 + getX(), 0 + getY(), getColor().getRGB());
            if (Alpha.getValue() > 210) {
                drawRect(3 + getX(), 0 + getY(), 151 + getX(), 17 + getY(), new Color(12, 12, 12, 255).getRGB());
            } else {
                drawRect(3 + getX(), 0 + getY(), 151 + getX(), 17 + getY(), new Color(12, 12, 12, (int) (Alpha.getValue() + 45)).getRGB());
            }

            Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawCenteredString("Players", (int) (148 / 2 + getX()), (int) (2.5 + getY()), -1);

            for (EntityPlayer entity : mc.theWorld.playerEntities) {

                if (validname.isEnabled()) {
                    if (entity.isInvisible()  || entity.getName().contains("(") || entity.getName().contains(")") || entity.getName().contains("-") || entity.getName().contains("§")) {
                        return;
                    }
                }

                if (entity.isDead)
                    return;

                drawRect(3 + getX(), 0 + getY() + offset, 151 + getX(), 17 + getY() + offset, new Color(20, 20, 20, (int) Alpha.getValue()).getRGB());
                if (entity.getName().equals(mc.thePlayer.getName())) {
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawCenteredString(entity.getName() + EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + "You" + EnumChatFormatting.GRAY + "]", (int) (148 / 2 + getX()), (int) (2.5 + offset + getY()), -1);
                } else {
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawCenteredString(entity.getName() + getDistance(entity), (int) (148 / 2 + getX()), (int) (2.5 + offset + getY()), -1);
                }
                offset = offset + 17;
            }
        }
    }

    public String getDistance(EntityPlayer entity) {
        return distance.isEnabled() ? EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + Math.round(mc.thePlayer.getDistanceToEntity(entity)) + "m" + EnumChatFormatting.GRAY + "]" : "" + (hp.isEnabled() ? EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + Math.round(entity.getMaxHealth()) + "HP" + EnumChatFormatting.GRAY + "]" : "");
    }

    public Color getColor() {
        return color.is("Rainbow") ? getGradientOffset(new Color(255, 60, 234, 255), new Color(27, 179, 255, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (3 / (Flauxy.INSTANCE.getFontManager().getFont("auxy 21").getHeight("A") + 6) / 2)) : new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue());
    }

    public static boolean hovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
        return (mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public static void drawRect(double left, double top, double right, double bottom, int color) {
        double j;
        if (left < right) {
            j = left;
            left = right;
            right = j;
        }

        if (top < bottom) {
            j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (color >> 24 & 255) / 255.0F;
        float f = (color >> 16 & 255) / 255.0F;
        float f1 = (color >> 8 & 255) / 255.0F;
        float f2 = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

}