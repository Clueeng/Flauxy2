package uwu.noctura.module.impl.display;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.text.DecimalFormat;


@ModuleInfo(name = "Speedometer", key = -1, cat = Category.Display, displayName = "Speedometer")
public class Speed extends Module {

    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue); // sat bri
    public BooleanSetting background = new BooleanSetting("Background",true);
    public ModeSetting unit = new ModeSetting("Unit","BPS","BPS", "m/s", "km/h", "mph");

    public Speed(){
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(20);
        hue.setColorDisplay(true);
        saturationValue.setColorDisplay(true);
        addSettings(unit, hue, saturationValue);
    }

    @Override
    public void onEvent(Event e) {
        Color c = getColorFromSettings(hue,saturationValue);
        if(e instanceof EventRender2D){
            String speed = "Speed: " + formattedSpeed();
            int padding = 6;
            setMoveW(mc.fontRendererObj.getStringWidth(speed) + padding);
            setMoveH(mc.fontRendererObj.FONT_HEIGHT + padding);
            if(background.isEnabled()){
                RenderUtil.drawRoundedRect2(getMoveX(),getMoveY(),getMoveX() + getMoveW(),getMoveY() + getMoveH(), 4,new Color(0,0,0,90).getRGB());
            }
            mc.fontRendererObj.drawStringWithShadow(speed,getMoveX()+(padding / 2f),getMoveY()+(padding / 2f),c.getRGB());
        }
    }

    public String formattedSpeed(){
        // Get the unit mode from the unit object
        String units = unit.getMode();

        // Calculate speed in BPS
        final double xz = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;

        // Format speed to two decimal places
        final DecimalFormat bpsFormat = new DecimalFormat("#.##");
        String formattedSpeed = "";
        switch (units.toLowerCase()) {
            case "bps":
                formattedSpeed = bpsFormat.format(xz) + " BPS";
                break;

            case "m/s":
                formattedSpeed = bpsFormat.format(xz) + " m/s";
                break;

            case "km/h":
                double kmph = xz * 3.6;
                formattedSpeed = bpsFormat.format(kmph) + " km/h";
                break;

            case "mph":
                double mph = xz * 2.23694;
                formattedSpeed = bpsFormat.format(mph) + " mph";
                break;

            default:
                formattedSpeed = bpsFormat.format(xz) + " BPS";
                break;
        }

        return formattedSpeed;
    }

}
