package uwu.noctura.module.impl.ghost;

import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;

@ModuleInfo(name = "LegitSprint", displayName = "Legit Sprint", key = -1, cat = Category.Ghost)
public class LegitSprint extends Module {

    BooleanSetting showInHUD = new BooleanSetting("Show HUD",true);
    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1).setCanShow(s -> showInHUD.isEnabled());
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue).setCanShow(s -> showInHUD.isEnabled()); // sat bri
    public BooleanSetting drawBackground = new BooleanSetting("Background",true).setCanShow(s -> showInHUD.isEnabled());
    public LegitSprint(){
        addSettings(showInHUD, hue, saturationValue, drawBackground);
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(250);
        hue.setColorDisplay(true);
        saturationValue.setColorDisplay(true);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onEvent(Event e) {
        Color c = getColorFromSettings(hue,saturationValue);
        if(e instanceof EventRender2D){
            String enabled = !Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()) ? "Enabled" : "Manual";
            String text = "ToggleSprint [" + enabled + "]";
            if(showInHUD.isEnabled()){
                int size = 6;
                setMoveW(mc.fontRendererObj.getStringWidth(text) + size);
                setMoveH(mc.fontRendererObj.FONT_HEIGHT + size);
                if(drawBackground.isEnabled()){
                    RenderUtil.drawRoundedRect2(getMoveX(),getMoveY(),getMoveX() + getMoveW(),getMoveY() + getMoveH(), 4,new Color(0,0,0,90).getRGB());
                }
                mc.fontRendererObj.drawStringWithShadow(text,getMoveX() + (size/2),getMoveY() + (size/2),c.getRGB());
            }
        }
        if(e instanceof EventUpdate){
            mc.gameSettings.keyBindSprint.pressed = !Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
    }
}
