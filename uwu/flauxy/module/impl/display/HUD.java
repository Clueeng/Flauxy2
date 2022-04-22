package uwu.flauxy.module.impl.display;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.font.FontManager;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.render.RenderUtil;
import uwu.flauxy.utils.shader.impl.GlowUtil;

import java.awt.*;
import java.util.ArrayList;

import static uwu.flauxy.utils.font.FontManager.getFont;

@ModuleInfo(name = "HUD", displayName = "HUD", key = -1, cat = Category.Display)
public class HUD extends Module {

    public ModeSetting watermark = new ModeSetting("Watermark", "Flauxy", "Flauxy", "Onetap", "Skeet");
    public BooleanSetting glow = new BooleanSetting("Glow", true);

    public HUD() {
        addSettings(watermark, glow);
    }

    @EventTarget
    public void onRender(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);

        switch (watermark.getMode()) {
            case "Flauxy":
                if (glow.isEnabled()) {
                    GlowUtil.drawAndBloom(() -> getFont().drawString("" + Flauxy.INSTANCE.getName().charAt(0) + EnumChatFormatting.WHITE + "lauxy", 4, 4, Color.GREEN.getRGB()));
                }else {
                    getFont().drawString("" + Flauxy.INSTANCE.getName().charAt(0) + EnumChatFormatting.WHITE + "lauxy", 4, 4, Color.GREEN.getRGB());
                }
                break;
            case "Onetap":
                String server = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                String text = "Flauxy | " + mc.thePlayer.getName() + " | " + server;
                float width = Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(text) + 6;
                RenderUtil.drawRect(2, 2, width, 3, Color.ORANGE.getRGB());
                RenderUtil.drawRect(2, 3, width, 13, new Color(67, 67, 67, 190).getRGB());
                if (glow.isEnabled()) {
                    GlowUtil.drawAndBloom(() -> Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(text, 4F, (float) (4.5 - 2), -1));
                }else {
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(text, 4F, (float) (4.5 - 2), -1);
                }
                break;
            case "Skeet":
                String skeetserver = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                String skeettext = "Flauxy | " + mc.getDebugFPS() + " fps | " + skeetserver;
                float skeetwidth = Flauxy.INSTANCE.getFontManager().getFont("auxy 40").getWidth(skeettext) + 6;
                int height = 20;
                int posX = 2;
                int posY = 2;
                RenderUtil.drawRect(posX, posY, posX + skeetwidth + 2, posY + height, new Color(5, 5, 5, 255).getRGB());
                RenderUtil.drawBorderedRect(posX + .5, posY + .5, posX + skeetwidth + 1.5, posY + height - .5, 0.5, new Color(40, 40, 40, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
                RenderUtil.drawBorderedRect(posX + 2, posY + 2, posX + skeetwidth, posY + height - 2, 0.5, new Color(22, 22, 22, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
                RenderUtil.drawRect(posX + 2.5, posY + 2.5, posX + skeetwidth - .5, posY + 4.5, new Color(9, 9, 9, 255).getRGB());
                RenderUtil.drawGradientSideways(4, posY + 3, 4 + (skeetwidth / 3), posY + 4, new Color(81, 149, 219, 255).getRGB(), new Color(180, 49, 218, 255).getRGB());
                RenderUtil.drawGradientSideways(4 + (skeetwidth / 3), posY + 3, 4 + ((skeetwidth / 3) * 2), posY + 4, new Color(180, 49, 218, 255).getRGB(), new Color(236, 93, 128, 255).getRGB());
                RenderUtil.drawGradientSideways(4 + ((skeetwidth / 3) * 2), posY + 3, ((skeetwidth / 3) * 3) + 1, posY + 4, new Color(236, 93, 128, 255).getRGB(), new Color(167, 171, 90, 255).getRGB());
                if (glow.isEnabled()) {
                    GlowUtil.drawAndBloom(() -> Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(skeettext, (float) (4 + posX), (float) (8 + posY - 2.3), -1));
                }else {
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawString(skeettext, (float) (4 + posX), (float) (8 + posY - 2.3), -1);
                }

                break;
        }

        String bps = String.format("%.2f", Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed * 20.0D);
        String drawBPS = "Blocks/sec: " + EnumChatFormatting.GRAY + bps;
        if (glow.isEnabled()) {
            GlowUtil.drawAndBloom(() -> Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawStringWithShadow(drawBPS, 0, sr.getScaledHeight() - 24, -1));
        }else {
            Flauxy.INSTANCE.getFontManager().getFont("auxy 40").drawStringWithShadow(drawBPS, 0, sr.getScaledHeight() - 35, -1);
        }

    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
