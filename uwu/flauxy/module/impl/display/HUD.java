package uwu.flauxy.module.impl.display;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.text.DecimalFormat;

import static uwu.flauxy.utils.font.FontManager.getFont;

@ModuleInfo(name = "HUD", displayName = "HUD", key = -1, cat = Category.Display)
public class HUD extends Module {

    public ModeSetting watermark = new ModeSetting("Watermark", "Flauxy", "Flauxy", "Onetap", "Skeet");
    BooleanSetting customfont = new BooleanSetting("Custom Font", true);
    //public BooleanSetting glow = new BooleanSetting("Glow", true);

    public HUD() {
        addSettings(watermark, customfont);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRender2D){
            ScaledResolution sr = new ScaledResolution(mc);

            switch (watermark.getMode()) {
                case "Flauxy":
                    if(customfont.isEnabled()) {
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawString("" + Flauxy.INSTANCE.getName().charAt(0) + EnumChatFormatting.WHITE + "lauxy", 4, 4, Color.GREEN.getRGB());
                    } else {
                        mc.fontRendererObj.drawStringWithShadow("" + Flauxy.INSTANCE.getName().charAt(0) + EnumChatFormatting.WHITE + "lauxy", 4, 4, Color.GREEN.getRGB());
                    }
                    break;
                case "Onetap":
                    if(customfont.isEnabled()) {
                        String server = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                        String text = "Flauxy | " + mc.thePlayer.getName() + " | " + server;
                        float width = Flauxy.INSTANCE.getFontManager().getFont("auxy 21").getWidth(text) + 6;
                        RenderUtil.drawRect(2, 2, width, 3, Color.ORANGE.getRGB());
                        RenderUtil.drawRect(2, 3, width, 15, new Color(67, 67, 67, 190).getRGB());
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawString(text, 4F, (float) (4.5 - 2), -1);

                        break;
                    } else {
                        String server = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                        String text = "Flauxy | " + mc.thePlayer.getName() + " | " + server;
                        float width = Flauxy.INSTANCE.getFontManager().getFont("auxy 21").getWidth(text) + 6;
                        RenderUtil.drawRect(2, 2, width, 3, Color.ORANGE.getRGB());
                        RenderUtil.drawRect(2, 3, width, 15, new Color(67, 67, 67, 190).getRGB());
                        mc.fontRendererObj.drawStringWithShadow(text, 4F, (float) (4.5 - 2), -1);

                        break;
                    }
                case "Skeet":
                    if(customfont.isEnabled()) {
                        String skeetserver = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                        String skeettext = "Flauxy | " + mc.getDebugFPS() + " fps | " + skeetserver;
                        float skeetwidth = Flauxy.INSTANCE.getFontManager().getFont("auxy 21").getWidth(skeettext) + 6;
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

                        Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawString(skeettext, (float) (4 + posX), (float) (8 + posY - 2.3), -1);
                        break;
                    } else {
                        String skeetserver = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                        String skeettext = "Flauxy | " + mc.getDebugFPS() + " fps | " + skeetserver;
                        float skeetwidth = Flauxy.INSTANCE.getFontManager().getFont("auxy 21").getWidth(skeettext) + 6;
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

                        mc.fontRendererObj.drawStringWithShadow(skeettext, (float) (4 + posX), (float) (8 + posY - 2.3), -1);
                        break;
                    }
            }


            final double xz = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
            final DecimalFormat bpsFormat = new DecimalFormat("#.##");
            final String bps = bpsFormat.format(xz);
            String drawBPS = "Blocks/sec: " + EnumChatFormatting.GRAY + bps;
            Flauxy.INSTANCE.getFontManager().getFont("auxy 16").drawStringWithShadow(drawBPS, 4, sr.getScaledHeight() - 25, -1);
        }

    }



    @Override
    public void onEnable() {
        super.onEnable();
    }
}
