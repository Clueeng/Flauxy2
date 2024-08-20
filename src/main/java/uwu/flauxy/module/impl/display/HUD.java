package uwu.flauxy.module.impl.display;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.movement.Longjump;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.*;
import uwu.flauxy.utils.render.RenderUtil;
import viamcp.ViaMCP;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uwu.flauxy.utils.font.FontManager.getFont;

@ModuleInfo(name = "HUD", displayName = "HUD", key = -1, cat = Category.Display)
public class HUD extends Module {

    public ModeSetting watermark = new ModeSetting("Watermark", "Flauxy", "Flauxy", "Onetap", "Skeet", "Astolfo");
    public BooleanSetting waterMarkToggled = new BooleanSetting("Watermark", true);

    public BooleanSetting fps = new BooleanSetting("FPS", true);
    public ModeSetting positionFps = new ModeSetting("FPS Position", "Top-Left", "Top-Left", "Bottom-Left", "Bottom-Right").setCanShow(m -> fps.getValue());
    public BooleanSetting showRotations = new BooleanSetting("Show Rotations",true);

    public BooleanSetting customfont = new BooleanSetting("Custom Font", true);
    public BooleanSetting showPosition = new BooleanSetting("Show Position",true).setCanShow(s -> showRotations.getValue());
    public BooleanSetting showVelocity = new BooleanSetting("Show Velocity",true).setCanShow(s -> showRotations.getValue());
    public BooleanSetting showAction = new BooleanSetting("Show Action",true).setCanShow(s -> showRotations.getValue()); // vlock sneak ground
    public BooleanSetting showSpeed = new BooleanSetting("Show Speed",true).setCanShow(s -> showRotations.getValue());

    public int infocount;
    //public BooleanSetting glow = new BooleanSetting("Glow", true);
    float deltaYaw = 0.0f, deltaPitch = 0.0f;
    float velocityX, velocityZ, velocityY, opacityEnd, opacity = 0.0f;
    long lastKnockback = 0;
    public long lastBlockPlace;
    boolean updatePing;

    @Getter @Setter
    public int absoluteX, absoluteY;
    public long ping;
    public float maxSpeed, resetSpeed;
    private final Map<Integer, Long> keepAliveTimestamps = new HashMap<>();
    public float width;
    public List<InfoEntry> infoEntries = new java.util.ArrayList<>();
    public HUD() {
        addSettings(watermark, showRotations, showPosition, showVelocity, showAction, showSpeed, customfont, fps, positionFps);
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
            if(e.getPacket() instanceof S12PacketEntityVelocity){
                S12PacketEntityVelocity s = (S12PacketEntityVelocity) e.getPacket();
                if(s.getEntityID() == mc.thePlayer.getEntityId()){
                    if(Math.abs(s.getMotionY()) > 1E-3f){
                        velocityY = s.getMotionY() / 8000f;
                    }
                    if(Math.abs(s.getMotionX()) > 1E-3f){
                        velocityX = s.getMotionX() / 8000f;
                    }
                    if(Math.abs(s.getMotionZ()) > 1E-3f){
                        velocityZ = s.getMotionZ() / 8000f;
                    }
                    if(Math.abs(s.getMotionZ()) > 1E-3f || Math.abs(s.getMotionX()) > 1E-3f || Math.abs(s.getMotionY()) > 1E-3f){
                        lastKnockback = System.currentTimeMillis();
                        opacityEnd = 255;
                        opacity = 255;
                    }

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
                    System.out.println("Ping: " + ping + "ms");
                }
            }
        }
        if(event instanceof EventUpdate){
            EventUpdate e = (EventUpdate)event;
            if(e.getType().equals(EventType.PRE)){

                if (mc.thePlayer.ticksExisted % 5 == 0) {
                    loadInformation();
                }
                opacity = (float) MathHelper.lerp(0.2f,opacity,opacityEnd);
                deltaYaw = mc.thePlayer.rotationYaw - mc.thePlayer.prevPrevRotationYaw;
                deltaPitch = mc.thePlayer.rotationPitch - mc.thePlayer.prevPrevRotationPitch;
                if(Math.abs(System.currentTimeMillis() - lastKnockback) > 2500){
                    velocityZ = 0;
                    velocityY = 0;
                    velocityX = 0;
                    opacityEnd = 0;
                }
                float threshold = 1e-4f;
                if(maxSpeed + threshold < MoveUtils.getSpeed()){
                    maxSpeed = (float) MoveUtils.getSpeed();
                    resetSpeed = 2.5f * 20;
                }
                resetSpeed -= 1;
                if(resetSpeed <= 0){
                    maxSpeed = 0;
                }
                resetSpeed = Math.max(0, resetSpeed);
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
                    if(customfont.getValue()){
                        Flauxy.INSTANCE.getFontManager().getFont("auxy 19").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                (Flauxy.INSTANCE.getFontManager().getFont("auxy 19").getWidth("Ghost mode is disabled")), sr.getScaledHeight() - 14, Color.RED.getRGB());
                    }else{
                        mc.fontRendererObj.drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                (mc.fontRendererObj.getStringWidth("Ghost mode is disabled")), sr.getScaledHeight() - 14, Color.RED.getRGB());
                    }

                }else{
                    if(mc.currentScreen instanceof GuiChat){
                        if(customfont.getValue()){
                            Flauxy.INSTANCE.getFontManager().getFont("auxy 19").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                    (Flauxy.INSTANCE.getFontManager().getFont("auxy 19").getWidth("Ghost mode is disabled")), sr.getScaledHeight() - 25, Color.RED.getRGB());
                        }else{
                            mc.fontRendererObj.drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() -
                                    (mc.fontRendererObj.getStringWidth("Ghost mode is disabled")), sr.getScaledHeight() - 25, Color.RED.getRGB());
                        }
                    }
                    if(mc.currentScreen instanceof GuiInventory){
                        double len = (Flauxy.INSTANCE.getFontManager().getFont("auxy 24").getWidth("Ghost mode is disabled"));
                        if(customfont.getValue()){
                            Flauxy.INSTANCE.getFontManager().getFont("auxy 24").drawStringWithShadow("Ghost mode is disabled", sr.getScaledWidth() - ((double) sr.getScaledWidth() / 2) - (len / 2)
                                    , 100, Color.RED.getRGB());
                        }else{
                            mc.fontRendererObj.drawStringWithShadow("Ghost mode is disabled", (float) (sr.getScaledWidth() - ((double) sr.getScaledWidth() / 2) - (len / 2))
                                    , 100, Color.RED.getRGB());
                        }
                    }
                }
            }

            if(showRotations.getValue()){
                infocount = infoEntries.size(); // 20 for blocking sneaking ground
                int maxWidth = infoEntries.stream()
                        .mapToInt(entry -> customfont.getValue() ? (int) getFont().getWidth(entry.getFormattedText()) : mc.fontRendererObj.getStringWidth(entry.getFormattedText()))
                        .max().orElse(0);

                int offsetY = 0;
                width = absoluteX + maxWidth;
                RenderUtil.drawRoundedRect2(absoluteX-2,absoluteY-2,width,
                        absoluteY + (12 * infocount), 4, new Color(0, 0, 0, 120).getRGB());

                for (InfoEntry entry : infoEntries) {
                    if (customfont.getValue()) {
                        getFont().drawStringWithShadow(entry.getFormattedText(), absoluteX, absoluteY + offsetY, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
                    } else {
                        mc.fontRendererObj.drawStringWithShadow(entry.getFormattedText(), absoluteX, absoluteY + offsetY, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
                    }
                    offsetY += 12;
                }
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
            if(customfont.getValue()){
                Flauxy.INSTANCE.getFontManager().getFont("auxy 16").drawStringWithShadow(drawBPS, 4, sr.getScaledHeight() - 4 - Flauxy.INSTANCE.getFontManager().getFont("auxy 16").getHeight(drawBPS), scol);
            }else{
                mc.fontRendererObj.drawStringWithShadow(drawBPS, 4, sr.getScaledHeight() - 4 - mc.fontRendererObj.FONT_HEIGHT, scol);
            }
        }

    }

    public void loadInformation(){
        infoEntries.clear();

        infoEntries.add(new InfoEntry("Client Brand", ClientBrandRetriever::getClientModName));
        if(showPosition.getValue()){
            infoEntries.add(new InfoEntry("Yaw / Pitch", () -> mc.thePlayer.rotationYaw + " / " + mc.thePlayer.rotationPitch));
            infoEntries.add(new InfoEntry("Delta Yaw", () -> String.valueOf(deltaYaw)));
            infoEntries.add(new InfoEntry("Delta Pitch", () -> String.valueOf(deltaPitch)));
            infoEntries.add(new InfoEntry("X", () -> String.valueOf(mc.thePlayer.posX)));
            infoEntries.add(new InfoEntry("Y", () -> String.valueOf(mc.thePlayer.posY)));
            infoEntries.add(new InfoEntry("Z", () -> String.valueOf(mc.thePlayer.posZ)));
            infoEntries.add(new InfoEntry("Facing", () -> mc.thePlayer.getHorizontalFacing().toString()));
        }
        infoEntries.add(new InfoEntry("Fast Math", () -> net.minecraft.util.MathHelper.fastMath + " / " + mc.gameSettings.ofFastMath));
        infoEntries.add(new InfoEntry("Protocol Version", () -> ViaUtil.toReadableVersion(ViaMCP.getInstance().getVersion())));
        if(showVelocity.getValue()){
            infoEntries.add(new InfoEntry("Velocity X", () -> String.valueOf(velocityX)));
            infoEntries.add(new InfoEntry("Velocity Y", () -> String.valueOf(velocityY)));
            infoEntries.add(new InfoEntry("Velocity Z", () -> String.valueOf(velocityZ)));
        }
        infoEntries.add(new InfoEntry("Ping", () -> String.valueOf(ping)));
        if(showSpeed.getValue()){
            infoEntries.add(new InfoEntry("Speed", () -> String.valueOf(MoveUtils.getMotion())));
            infoEntries.add(new InfoEntry("MotionX", () -> String.valueOf(mc.thePlayer.motionX)));
            infoEntries.add(new InfoEntry("MotionZ", () -> String.valueOf(mc.thePlayer.motionZ)));
            infoEntries.add(new InfoEntry("Max Speed", () -> String.valueOf(maxSpeed)));
        }
        if(showAction.isEnabled()){
            infoEntries.add(new InfoEntry("Sneaking", () -> String.valueOf(mc.thePlayer.isSneaking())));
            infoEntries.add(new InfoEntry("Ground", () -> String.valueOf(mc.thePlayer.onGround)));
            infoEntries.add(new InfoEntry("Blocking", () -> String.valueOf(mc.thePlayer.isBlocking())));
            infoEntries.add(new InfoEntry("Last Place", () -> String.valueOf(Math.abs(lastBlockPlace - System.currentTimeMillis()))));
        }

    }


    @Override
    public void onEnable() {
        super.onEnable();
    }
}
