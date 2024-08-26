package uwu.flauxy.module.impl.display;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.movement.Longjump;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.utils.InfoEntry;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.ViaUtil;
import uwu.flauxy.utils.render.RenderUtil;
import viamcp.ViaMCP;

import java.awt.*;
import java.util.List;

import static uwu.flauxy.utils.font.FontManager.getFont;

@ModuleInfo(name = "InfoHUD", displayName = "Info HUD", key = -1, cat = Category.Display)
public class InfoHUD extends Module {

    public List<InfoEntry> infoEntries = new java.util.ArrayList<>();

    public BooleanSetting showPosition = new BooleanSetting("Show Position",true);
    public BooleanSetting showVelocity = new BooleanSetting("Show Velocity",true);
    public BooleanSetting showAction = new BooleanSetting("Show Action",true); // vlock sneak ground
    public BooleanSetting showSpeed = new BooleanSetting("Show Speed",true);
    public BooleanSetting customFont = new BooleanSetting("Custom Font",true);
    public long lastBlockPlace, lastKnockback;
    public float deltaYaw = 0.0f, deltaPitch = 0.0f, velocityX, velocityZ, velocityY, maxSpeed, resetSpeed;

    public InfoHUD(){
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(300);
        addSettings(customFont, showPosition, showVelocity, showSpeed, showAction);
    }

    @Override
    public void onEvent(Event e) {

        if(e instanceof EventReceivePacket) {
            EventReceivePacket event = (EventReceivePacket) e;
            if(event.getPacket() instanceof S12PacketEntityVelocity){
                S12PacketEntityVelocity s = (S12PacketEntityVelocity) event.getPacket();
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
                    }
                }

            }
        }
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(ev.isPre()){
                deltaYaw = mc.thePlayer.rotationYaw - mc.thePlayer.prevPrevRotationYaw;
                deltaPitch = mc.thePlayer.rotationPitch - mc.thePlayer.prevPrevRotationPitch;
                if(Math.abs(System.currentTimeMillis() - lastKnockback) > 2500){
                    velocityZ = 0;
                    velocityY = 0;
                    velocityX = 0;
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
        }
        if(e instanceof EventRender2D){
            if (mc.thePlayer.ticksExisted % 5 == 0) {
                loadInformation();
            }
            int infocount = infoEntries.size(); // 20 for blocking sneaking ground
            int maxWidth = infoEntries.stream()
                    .mapToInt(entry -> customFont.getValue() ? (int) getFont().getWidth(entry.getFormattedText()) : mc.fontRendererObj.getStringWidth(entry.getFormattedText()))
                    .max().orElse(0);

            int offsetY = 0;
            setMoveW(maxWidth);
            setMoveH(12 * infocount);
            RenderUtil.drawRoundedRect2(getMoveX()-2,getMoveY()-2,getMoveX() + getMoveW(),
                    getMoveY() + getMoveH(), 4, new Color(0, 0, 0, 120).getRGB());

            for (InfoEntry entry : infoEntries) {
                if (customFont.getValue()) {
                    getFont().drawStringWithShadow(entry.getFormattedText(), getMoveX(), getMoveY() + offsetY, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(entry.getFormattedText(), getMoveX(), getMoveY() + offsetY, Flauxy.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor);
                }
                offsetY += 12;
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
}
