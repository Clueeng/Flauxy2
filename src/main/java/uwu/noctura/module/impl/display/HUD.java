package uwu.noctura.module.impl.display;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.movement.Longjump;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.*;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static uwu.noctura.utils.font.FontManager.getFont;

@ModuleInfo(name = "HUD", displayName = "HUD", key = -1, cat = Category.Display)
public class HUD extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Old", "Old", "New");
    public ModeSetting watermark = new ModeSetting("Watermark", "Flauxy", "Flauxy", "Onetap", "Skeet", "Astolfo", "Noctura").setCanShow(m -> mode.is("Old"));
    public BooleanSetting waterMarkToggled = new BooleanSetting("Watermark", true).setCanShow(m -> mode.is("Old"));

    public BooleanSetting fps = new BooleanSetting("FPS", true).setCanShow(m -> mode.is("Old"));
    public ModeSetting positionFps = new ModeSetting("FPS Position", "Top-Left", "Top-Left", "Bottom-Left", "Bottom-Right").setCanShow(m -> fps.getValue() && mode.is("Old"));

    public BooleanSetting customfont = new BooleanSetting("Custom Font", true);


    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1).setCanShow(m -> mode.is("New"));
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue).setCanShow(m -> mode.is("New")); // sat bri


    float opacityEnd, opacity = 0.0f;
    public float maxSpeed;
    public long ping;
    private final Map<Integer, Long> keepAliveTimestamps = new HashMap<>();
    public float width;
    public HUD() {
        addSettings(mode, watermark, customfont, fps, positionFps, hue, saturationValue);
        hue.setColorDisplay(true);
        saturationValue.setColorDisplay(true);
    }

    public static String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.now();
        return currentTime.format(formatter);
    }

    public void hudDraw(Event e){
        if(e instanceof EventRender2D){
            if(customfont.isEnabled()){
                int hudCol = getColorFromSettings(hue, saturationValue).getRGB();
                TTFFontRenderer medium = getFont("Good", 23);
                String title = Noctura.INSTANCE.getName();
                String firstLetter = title.substring(0, 1);
                String rest = title.substring(1);
                medium.drawStringWithShadow(firstLetter + EnumChatFormatting.RESET + rest + " (" + getTime() + ")", 4, 4, hudCol);


                ScaledResolution sr = new ScaledResolution(mc);

                int bottomScreen = sr.getScaledHeight();
                int rightScreen = sr.getScaledWidth();

                TTFFontRenderer small = getFont("Good", 16);
                small.drawStringWithShadow("BPS: " + getBPS(), 4, bottomScreen - small.getHeight("BPS") - 4, hudCol);


                TTFFontRenderer icon = getFont("icons2", 16);
                icon.drawString("a", 4, 24, -1);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventReceivePacket){
            Longjump longjump = Noctura.INSTANCE.getModuleManager().getModule(Longjump.class);
            EventReceivePacket e = (EventReceivePacket) event;
            if(e.getPacket() instanceof S02PacketChat){
                S02PacketChat p = (S02PacketChat) e.getPacket();
                String msg = p.getChatComponent().getUnformattedText();
                if(msg.contains("O jogo começa em 1 segundo") || msg.contains("Conectando") || msg.contains(mc.thePlayer.getName() + " foi")){
                    longjump.seconds = 0;
                    longjump.shouldWait = false;
                }
            }
            if (e.getPacket() instanceof S00PacketKeepAlive) {
                S00PacketKeepAlive keepAlivePacket = (S00PacketKeepAlive) e.getPacket();
                int id = keepAlivePacket.func_149134_c();
                keepAliveTimestamps.put(id, System.currentTimeMillis());
            }
        }
        if(event instanceof EventSendPacket){
            EventSendPacket e = (EventSendPacket)event;
            if(e.getPacket() instanceof C00PacketLoginStart){
                maxSpeed = 0;
            }
            if (e.getPacket() instanceof C00PacketKeepAlive) {
                C00PacketKeepAlive keepAlivePacket = (C00PacketKeepAlive) e.getPacket();
                int id = keepAlivePacket.getKey();
                Long sentTime = keepAliveTimestamps.remove(id);
                if (sentTime != null) {
                    ping = System.currentTimeMillis() - sentTime;
                }
            }
        }
        if(event instanceof EventUpdate){
            EventUpdate e = (EventUpdate)event;
            if(e.getType().equals(EventType.PRE)){
                opacity = (float) MathHelper.lerp(0.2f,opacity,opacityEnd);

            }
            Longjump longjump = Noctura.INSTANCE.getModuleManager().getModule(Longjump.class);
            if(longjump.shouldWait) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    longjump.seconds -= 1;
                }
            }
        }
        if(event instanceof EventRender2D){
            if(mode.is("New")){
                hudDraw(event);
                return;
            }
            ScaledResolution sr = new ScaledResolution(mc);
            Longjump longjump = Noctura.INSTANCE.getModuleManager().getModule(Longjump.class);
            if(longjump.shouldWait){
                Noctura.INSTANCE.getFontManager().getFont("auxy 21").drawStringWithShadow(longjump.seconds + "s left before next longjump", sr.getScaledWidth() / 2 - Noctura.INSTANCE.getFontManager().getFont("auxy 21").getWidth(longjump.seconds + "s left before next longjump") / 2, sr.getScaledHeight() / 2 + 120, new Color(227, 113, 113, 255).getRGB());
                if(longjump.seconds < 0 && longjump.shouldWait){
                    longjump.shouldWait = false;
                    Wrapper.instance.log("You may longjump");
                }
            }
            if(!Noctura.INSTANCE.isGhost()){
                if(mc.currentScreen == null){
                    if(customfont.getValue()){
                        Noctura.INSTANCE.getFontManager().getFont("auxy 19").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                (Noctura.INSTANCE.getFontManager().getFont("auxy 19").getWidth("Ghost mode is disabled")), sr.getScaledHeight() - 14, Color.RED.getRGB());
                    }else{
                        mc.fontRendererObj.drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                (mc.fontRendererObj.getStringWidth("Ghost mode is disabled")), sr.getScaledHeight() - 14, Color.RED.getRGB());
                    }

                }else{
                    if(mc.currentScreen instanceof GuiChat){
                        if(customfont.getValue()){
                            Noctura.INSTANCE.getFontManager().getFont("auxy 19").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                    (Noctura.INSTANCE.getFontManager().getFont("auxy 19").getWidth("Ghost mode is disabled")), sr.getScaledHeight() - 25, Color.RED.getRGB());
                        }else{
                            mc.fontRendererObj.drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                    (mc.fontRendererObj.getStringWidth("Ghost mode is disabled")), sr.getScaledHeight() - 25, Color.RED.getRGB());
                        }
                    }
                    if(mc.currentScreen instanceof GuiInventory){
                        double len = (Noctura.INSTANCE.getFontManager().getFont("auxy 24").getWidth("Ghost mode is disabled"));
                        if(customfont.getValue()){
                            Noctura.INSTANCE.getFontManager().getFont("auxy 24").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() - ((double) sr.getScaledWidth() / 2) - (len / 2)
                                    , 100, Color.RED.getRGB());
                        }else{
                            mc.fontRendererObj.drawStringWithShadow("Ghost mode is disabled", (float) (sr.getScaledWidth() - ((double) sr.getScaledWidth() / 2) - (len / 2))
                                    , 100, Color.RED.getRGB());
                        }
                    }
                }
            }

            if(waterMarkToggled.getValue()){
                switch (watermark.getMode()) {

                    case "Astolfo":{
                        Noctura.INSTANCE.getFontManager().getFont("auxy 21").drawString("Joe's Client", 5, 5, new Color(0, 0, 0, 150).getRGB());
                        Noctura.INSTANCE.getFontManager().getFont("auxy 21").drawString("Joe's Client", 4, 4, new Color(148, 105, 187).getRGB());
                        break;
                    }

                    case "Flauxy":
                        if(customfont.isEnabled()) {
                            Noctura.INSTANCE.getFontManager().getFont("auxy 21").drawStringWithShadow("" + Noctura.INSTANCE.getName().charAt(0) + EnumChatFormatting.WHITE + "octura", 4, 4, Color.GREEN.getRGB());
                        } else {
                            mc.fontRendererObj.drawStringWithShadow("F" + EnumChatFormatting.GRAY + "octura [" + mc.getDebugFPS() + "]", 4, 4, Color.GREEN.getRGB());
                        }
                        break;
                    case "Onetap":
                        if(customfont.isEnabled()) {
                            String server = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                            String text = "Noctura | " + mc.thePlayer.getName() + " | " + server;
                            float width = Noctura.INSTANCE.getFontManager().getFont("auxy 21").getWidth(text) + 6;
                            RenderUtil.drawRect(2, 2, width, 3, Color.ORANGE.getRGB());
                            RenderUtil.drawRect(2, 3, width, 15, new Color(67, 67, 67, 190).getRGB());
                            Noctura.INSTANCE.getFontManager().getFont("auxy 21").drawString(text, 4F, (float) (4.5 - 2), -1);

                            break;
                        } else {
                            String server = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                            String text = "Noctura | " + mc.thePlayer.getName() + " | " + server;
                            float width = Noctura.INSTANCE.getFontManager().getFont("auxy 21").getWidth(text) + 6;
                            RenderUtil.drawRect(2, 2, width, 3, Color.ORANGE.getRGB());
                            RenderUtil.drawRect(2, 3, width, 15, new Color(67, 67, 67, 190).getRGB());
                            mc.fontRendererObj.drawStringWithShadow(text, 4F, (float) (4.5 - 2), -1);

                            break;
                        }
                    case "Skeet":
                        if(customfont.isEnabled()) {
                            String skeetserver = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                            String skeettext = "Noctura | " + mc.getDebugFPS() + " fps | " + skeetserver;
                            float skeetwidth = Noctura.INSTANCE.getFontManager().getFont("auxy 21").getWidth(skeettext) + 6;
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

                            Noctura.INSTANCE.getFontManager().getFont("auxy 21").drawString(skeettext, (float) (4 + posX), (float) (8 + posY - 2.3), -1);
                            break;
                        } else {
                            String skeetserver = mc.isSingleplayer() ? "local server" : mc.getCurrentServerData().serverIP.toLowerCase();
                            String skeettext = "Noctura | " + mc.getDebugFPS() + " fps | " + skeetserver;
                            float skeetwidth = Noctura.INSTANCE.getFontManager().getFont("auxy 21").getWidth(skeettext) + 6;
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
            int scol = Noctura.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor;
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
                        y = sr.getScaledHeight() - (customfont.getValue() ? Noctura.INSTANCE.getFontManager().getFont("auxy 16").getHeight(txt)*2.5f : mc.fontRendererObj.FONT_HEIGHT*2 - 12);
                        break;
                    }
                    case "Bottom-Right":{

                        x = sr.getScaledWidth() - (customfont.getValue() ? Noctura.INSTANCE.getFontManager().getFont("auxy 16").getWidth(txt) : mc.fontRendererObj.getStringWidth(txt)) - 2;
                        y = sr.getScaledHeight() - (customfont.getValue() ? Noctura.INSTANCE.getFontManager().getFont("auxy 16").getHeight(txt) : mc.fontRendererObj.FONT_HEIGHT) - 2;
                        if(!Noctura.INSTANCE.isGhost()){
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
                    Noctura.INSTANCE.getFontManager().getFont("auxy 16").drawStringWithShadow(txt, x, y, scol);
                }else{
                    mc.fontRendererObj.drawString(txt, (int) x, (int) y, scol);
                }
            }


            final double xz = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
            final DecimalFormat bpsFormat = new DecimalFormat("#.##");
            final String bps = bpsFormat.format(xz);
            String drawBPS = "Blocks/s: " + EnumChatFormatting.WHITE + bps;
            if(customfont.getValue()){
                Noctura.INSTANCE.getFontManager().getFont("auxy 16").drawStringWithShadow(drawBPS, 4, sr.getScaledHeight() - 4 - Noctura.INSTANCE.getFontManager().getFont("auxy 16").getHeight(drawBPS), scol);
            }else{
                mc.fontRendererObj.drawStringWithShadow(drawBPS, 4, sr.getScaledHeight() - 4 - mc.fontRendererObj.FONT_HEIGHT, scol);
            }
        }

    }

    public String getBPS(){
        final double xz = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        final DecimalFormat bpsFormat = new DecimalFormat("#.##");
        return bpsFormat.format(xz);
    }




    @Override
    public void onEnable() {
        super.onEnable();
    }
}
