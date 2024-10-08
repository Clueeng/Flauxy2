package uwu.noctura.module.impl.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

import java.awt.*;

import static uwu.noctura.utils.render.ColorUtils.getGradientOffset;

@ModuleInfo(name = "PlayerList", displayName = "Player List", key = 0, cat = Category.Display)
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
            if(mc.theWorld == null || mc.thePlayer == null) return;
            float offset = 17;
            setX(X.getValue());
            setY(Y.getValue());

            Gui.drawRect((float) (3 + getX()), (float) (-1 + getY()), (float) (151 + getX()), (float) (0 + getY()), new Color(35, 35, 35).getRGB());
            if (Alpha.getValue() > 210) {
                Gui.drawRect((float) (3 + getX()), (float) (0 + getY()), (float) (151 + getX()), (float) (17 + getY()), new Color(12, 12, 12, 255).getRGB());
            } else {
                Gui.drawRect((float) (3 + getX()), (float) (0 + getY()), (float) (151 + getX()), (float) (17 + getY()), new Color(12, 12, 12, (int) (Alpha.getValue() + 45)).getRGB());
            }
            int count = mc.theWorld.playerEntities.size();
            mc.fontRendererObj.drawStringWithShadow("Player List (" + count + ")", (int) getX() + 8, (int) getY() + 4, -1);

            for (EntityPlayer entity : mc.theWorld.playerEntities) {
                GlStateManager.resetColor();
                if (validname.isEnabled()) {
                    if (entity.isInvisible()  || entity.getName().contains("(") || entity.getName().contains(")") || entity.getName().contains("-") || entity.getName().contains("§")|| entity.getName().contains("[")|| entity.getName().contains("]")) {
                        return;
                    }
                }

                if (entity.isDead)
                    return;

                Gui.drawRect((float) (3 + getX()), (float) (0 + getY() + offset), (float) (151 + getX()), (float) (17 + getY() + offset), new Color(20, 20, 20, (int) Alpha.getValue()).getRGB());
                if (entity.getName().equals(mc.thePlayer.getName())) {
                    mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW + "\u272A " + EnumChatFormatting.RESET + entity.getName(), (float) getX() + 12, (float) getY() + offset + 3, -1);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(entity.getName() + " (" + Math.round(mc.thePlayer.getDistanceToEntity(entity)) + ")", (float) getX() + 12, (float) getY() + offset + 3, -1);
                }
                offset = offset + 17;
            }
        }
    }

    public String getDistance(EntityPlayer entity) {
        return distance.isEnabled() ? EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + Math.round(mc.thePlayer.getDistanceToEntity(entity)) + "m" + EnumChatFormatting.GRAY + "]" : "" + (hp.isEnabled() ? EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + Math.round(entity.getMaxHealth()) + "HP" + EnumChatFormatting.GRAY + "]" : "");
    }

    public Color getColor() {
        return color.is("Rainbow") ? getGradientOffset(new Color(255, 60, 234, 255), new Color(27, 179, 255, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (3 / (Noctura.INSTANCE.getFontManager().getFont("auxy 21").getHeight("A") + 6) / 2)) : new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue());
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


}
