package uwu.flauxy.module.impl.visuals;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;

@ModuleInfo(name = "Radar", displayName = "Radar", key = 0, cat = Category.Display)
public class Radar extends Module {

    public NumberSetting x = new NumberSetting("X", 15, 1.0, 1920.0, 5.0);
    public NumberSetting y = new NumberSetting("Y", 80, 0.0, 1080.0, 5.0);
    public NumberSetting size = new NumberSetting("Size", 125.0, 50.0, 500.0, 5.0);
    float hue;

    public Radar() {
        addSettings(x, y, size);
    }

    public void onEvent(Event e) {
        if (e instanceof EventRender2D) {
            final int size1 = (int) size.getValue();
            final float xOffset = (float) x.getValue();
            final float yOffset = (float) y.getValue();
            final float playerOffsetX = (float) mc.thePlayer.posX;
            final float playerOffSetZ = (float) mc.thePlayer.posZ;


            if (hue > 255.0f) {
                hue = 0.0f;
            }

            hue += (float) 0.1;

            RenderUtil.drawRoundedRect2(xOffset + 3.0f, yOffset + 3.0f, xOffset + size.getValue() - 3.0, yOffset + size.getValue() - 3.0, 5, new Color(13, 13, 13, 140).getRGB());
            RenderUtil.drawRoundedRect2(xOffset, yOffset, xOffset + size.getValue(), yOffset + size.getValue(), 10, new Color(1, 1, 1, 140).getRGB());
            for (final Object o : mc.theWorld.getLoadedEntityList()) {
                final EntityPlayer ent;
                if (o instanceof EntityPlayer && (ent = (EntityPlayer) o).isEntityAlive() && ent != mc.thePlayer && !ent.isInvisible()) {
                    if (ent.isInvisibleToPlayer(mc.thePlayer)) {
                        continue;
                    }
                    final float pTicks = mc.timer.renderPartialTicks;
                    final float posX = (float) ((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks - playerOffsetX) * 1);
                    final float posZ = (float) ((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks - playerOffSetZ) * 1);
                    final String formattedText = ent.getDisplayName().getFormattedText();
                    int color39 = mc.thePlayer.canEntityBeSeen(ent) ? new Color(255, 255, 255).getRGB() : new Color(120, 120, 120).getRGB();
                    if (ent.hurtTime > 0) {
                        color39 = new Color(255, 0, 0).getRGB();
                    }
                    final float cos = (float) Math.cos(mc.thePlayer.rotationYaw * 0.017453292519943295);
                    final float sin = (float) Math.sin(mc.thePlayer.rotationYaw * 0.017453292519943295);
                    float rotY = -posZ * cos - posX * sin;
                    float rotX = -posX * cos + posZ * sin;
                    if (rotY > size1 / 2 - 5) {
                        rotY = size1 / 2 - 5.0f;
                    } else if (rotY < -size1 / 2 - 5) {
                        rotY = (float) (-size1 / 2 - 5);
                    }
                    if (rotX > size1 / 2 - 5.0f) {
                        rotX = (float) (size1 / 2 - 5);
                    } else if (rotX < -size1 / 2 - 5) {
                        rotX = -(size1 / 2) - 5.0f;
                    }
                    RenderUtil.drawBorderedRectangle(xOffset + 4.0f + size1 / 2 + rotX - 1.5, yOffset + 4.0f + size1 / 2 + rotY - 1.5, xOffset + 4.0f + size1 / 2 + rotX + 1.5, yOffset + 4.0f + size1 / 2 + rotY + 1.5, 0.5, color39, ColorUtils.getColor(46));
                }
            }
        }
    }

    public int getColor(final int p_clamp_int_0_, final int p_clamp_int_0_2, final int p_clamp_int_0_3, final int p_clamp_int_0_4) {
        return MathHelper.clamp_int(p_clamp_int_0_4, 0, 255) << 24 | MathHelper.clamp_int(p_clamp_int_0_, 0, 255) << 16 | MathHelper.clamp_int(p_clamp_int_0_2, 0, 255) << 8 | MathHelper.clamp_int(p_clamp_int_0_3, 0, 255);
    }

    public Object castNumber(final String newValueText, final Object currentValue) {
        if (newValueText.contains(".")) {
            if (newValueText.toLowerCase().contains("f")) {
                return Float.parseFloat(newValueText);
            }
            return Double.parseDouble(newValueText);
        } else {
            if (isNumeric(newValueText)) {
                return Integer.parseInt(newValueText);
            }
            return newValueText;
        }
    }

    public boolean isNumeric(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }
}