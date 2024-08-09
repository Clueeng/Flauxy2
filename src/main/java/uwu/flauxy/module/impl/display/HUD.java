package uwu.flauxy.module.impl.display;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.movement.Longjump;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.text.DecimalFormat;

import static uwu.flauxy.utils.font.FontManager.getFont;

@ModuleInfo(name = "HUD", displayName = "HUD", key = -1, cat = Category.Display)
public class HUD extends Module {

    public ModeSetting watermark = new ModeSetting("Watermark", "Flauxy", "Flauxy", "Onetap", "Skeet", "Astolfo");
    public BooleanSetting waterMarkToggled = new BooleanSetting("Watermark", true);

    public BooleanSetting fps = new BooleanSetting("FPS", true);
    public ModeSetting positionFps = new ModeSetting("FPS Position", "Top-Left", "Top-Left", "Bottom-Left", "Bottom-Right").setCanShow(m -> fps.getValue());
    public BooleanSetting showRotations = new BooleanSetting("Show Rotations",true);

    BooleanSetting customfont = new BooleanSetting("Custom Font", true);
    //public BooleanSetting glow = new BooleanSetting("Glow", true);
    float deltaYaw = 0.0f, deltaPitch = 0.0f;
    public HUD() {
        addSettings(watermark, showRotations, customfont, fps, positionFps);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventReceivePacket){
            Longjump longjump = Flauxy.INSTANCE.getModuleManager().getModule(Longjump.class);
            EventReceivePacket e = (EventReceivePacket) event;
            if(e.getPacket() instanceof S02PacketChat){
                S02PacketChat p = (S02PacketChat) e.getPacket();
                String msg = p.getChatComponent().getUnformattedText();
                if(msg.contains("O jogo começa em 1 segundo") || msg.contains("Conectando") || msg.contains(mc.thePlayer.getName() + " foi")){
                    longjump.seconds = 0;
                    longjump.shouldWait = false;
                }
            }
        }
        if(event instanceof EventUpdate){
            EventUpdate e = (EventUpdate)event;
            if(e.getType().equals(EventType.PRE)){
                deltaYaw = mc.thePlayer.rotationYaw - mc.thePlayer.prevPrevRotationYaw;
                deltaPitch = mc.thePlayer.rotationPitch - mc.thePlayer.prevPrevRotationPitch;
            }
            Longjump longjump = Flauxy.INSTANCE.getModuleManager().getModule(Longjump.class);
            if(longjump.shouldWait) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    longjump.seconds -= 1;
                }
            }
        }
        if(event instanceof EventRender2D){
            ScaledResolution sr = new ScaledResolution(mc);
            Longjump longjump = Flauxy.INSTANCE.getModuleManager().getModule(Longjump.class);
            if(longjump.shouldWait){
                Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawStringWithShadow(longjump.seconds + "s left before next longjump", sr.getScaledWidth() / 2 - Flauxy.INSTANCE.getFontManager().getFont("auxy 21").getWidth(longjump.seconds + "s left before next longjump") / 2, sr.getScaledHeight() / 2 + 120, new Color(227, 113, 113, 255).getRGB());
                if(longjump.seconds < 0 && longjump.shouldWait){
                    longjump.shouldWait = false;
                    Wrapper.instance.log("You may longjump");
                }
            }
            if(!Flauxy.INSTANCE.isGhost()){
                if(mc.currentScreen == null){
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 19").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                            (Flauxy.INSTANCE.getFontManager().getFont("auxy 19").getWidth("Ghost mode is disabled")), sr.getScaledHeight() - 14, Color.RED.getRGB());
                }else{
                    if(mc.currentScreen instanceof GuiChat){
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 19").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                (Flauxy.INSTANCE.getFontManager().getFont("auxy 19").getWidth("Ghost mode is disabled")), sr.getScaledHeight() - 25, Color.RED.getRGB());
                    }
                    if(mc.currentScreen instanceof GuiInventory){
                        double len = (Flauxy.INSTANCE.getFontManager().getFont("auxy 24").getWidth("Ghost mode is disabled"));
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 24").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() - ((double) sr.getScaledWidth() / 2) - (len / 2)
                                , 100, Color.RED.getRGB());
                    }
                }
            }

            if(showRotations.getValue()){
                mc.fontRendererObj.drawStringWithShadow("Yaw / Pitch : " + mc.thePlayer.rotationYaw + " / " + mc.thePlayer.rotationPitch, 4,36, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
                mc.fontRendererObj.drawStringWithShadow("Delta Yaw : " + deltaYaw, 4,48, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
                mc.fontRendererObj.drawStringWithShadow("Delta Pitch : " + deltaPitch, 4,60, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
            }

            if(waterMarkToggled.getValue()){
                switch (watermark.getMode()) {


                    case "Astolfo":{
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawString("Joe's Client", 5, 5, new Color(0, 0, 0, 150).getRGB());
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawString("Joe's Client", 4, 4, new Color(148, 105, 187).getRGB());
                        break;
                    }

                    case "Flauxy":
                        if(customfont.isEnabled()) {
                            Flauxy.INSTANCE.getFontManager().getFont("auxy 21").drawStringWithShadow("" + Flauxy.INSTANCE.getName().charAt(0) + EnumChatFormatting.WHITE + "lauxy", 4, 4, Color.GREEN.getRGB());
                        } else {
                            mc.fontRendererObj.drawStringWithShadow("F" + EnumChatFormatting.GRAY + "lauxy [" + mc.getDebugFPS() + "]", 4, 4, Color.GREEN.getRGB());
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
            }
            int scol = Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor;
            if(fps.getValue()){
                String txt = "FPS:  " + EnumChatFormatting.WHITE + mc.getDebugFPS();
                double x = 0, y = 0;
                switch (positionFps.getMode()){
                    case "Top-Left":{
                        x = 4;
                        y = 4;
                        if(waterMarkToggled.getValue()){
                            y += 26;
                        }
                        break;
                    }
                    case "Bottom-Left":{
                        x = 4;
                        y = sr.getScaledHeight() - (customfont.getValue() ? Flauxy.INSTANCE.getFontManager().getFont("auxy 16").getHeight(txt)*2.5f : mc.fontRendererObj.FONT_HEIGHT*2 - 12);
                        break;
                    }
                    case "Bottom-Right":{

                        x = sr.getScaledWidth() - (customfont.getValue() ? Flauxy.INSTANCE.getFontManager().getFont("auxy 16").getWidth(txt) : mc.fontRendererObj.getStringWidth(txt)) - 2;
                        y = sr.getScaledHeight() - (customfont.getValue() ? Flauxy.INSTANCE.getFontManager().getFont("auxy 16").getHeight(txt) : mc.fontRendererObj.FONT_HEIGHT) - 2;
                        if(!Flauxy.INSTANCE.isGhost()){
                            y -= 14;
                        }
                        if(mc.currentScreen != null){
                            if(mc.currentScreen instanceof GuiChat){
                                y -= 12;
                            }
                        }
                        break;
                    }
                }
                if(customfont.getValue()){
                    Flauxy.INSTANCE.getFontManager().getFont("auxy 16").drawStringWithShadow(txt, x, y, scol);
                }else{
                    mc.fontRendererObj.drawString(txt, (int) x, (int) y, scol);
                }
            }


            final double xz = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
            final DecimalFormat bpsFormat = new DecimalFormat("#.##");
            final String bps = bpsFormat.format(xz);
            String drawBPS = "Blocks/s: " + EnumChatFormatting.WHITE + bps;
            Flauxy.INSTANCE.getFontManager().getFont("auxy 16").drawStringWithShadow(drawBPS, 4, sr.getScaledHeight() - 4 - Flauxy.INSTANCE.getFontManager().getFont("auxy 16").getHeight(drawBPS), scol);
        }

    }



    @Override
    public void onEnable() {
        super.onEnable();
    }
}
