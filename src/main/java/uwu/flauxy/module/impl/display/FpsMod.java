package uwu.flauxy.module.impl.display;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.render.RenderUtil;
import uwu.flauxy.utils.render.shader.StencilUtil;
import uwu.flauxy.utils.render.shader.blur.GaussianBlur;

import java.awt.*;


@ModuleInfo(name = "FPS", key = -1, cat = Category.Display, displayName = "FPS")
public class FpsMod extends Module {

    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue); // sat bri
    public BooleanSetting background = new BooleanSetting("Background",true);
    BooleanSetting blur = new BooleanSetting("Blur",true);

    public FpsMod(){
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(500);
        hue.setColorDisplay(true);
        saturationValue.setColorDisplay(true);
        addSettings(hue, saturationValue, blur);
    }

    @Override
    public void onEvent(Event e) {
        Color c = getColorFromSettings(hue,saturationValue);
        if(e instanceof EventRender2D){
            int x = mc.thePlayer.getPosition().getX();
            int y = mc.thePlayer.getPosition().getY();
            int z = mc.thePlayer.getPosition().getZ();
            String coords = "FPS: " + Minecraft.debugFPS;
            int padding = 6;
            setMoveW(mc.fontRendererObj.getStringWidth(coords) + padding);
            setMoveH(mc.fontRendererObj.FONT_HEIGHT + padding);

            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.prepareScissorBox(getMoveX()
                    ,getMoveY()
                    ,getMoveX() + getMoveW(),
                    getMoveY() + getMoveH());
            if(blur.isEnabled()){
                GaussianBlur.renderBlur(5f);
            }

            if(background.isEnabled()){
                RenderUtil.drawRoundedRect2(getMoveX(),getMoveY(),getMoveX() + getMoveW(),getMoveY() + getMoveH(), 4,new Color(0,0,0,90).getRGB());
            }

            mc.fontRendererObj.drawStringWithShadow(coords,getMoveX()+(padding / 2f),getMoveY()+(padding / 2f),c.getRGB());

            GL11.glDisable(3089);
            GL11.glPopMatrix();
            StencilUtil.uninitStencilBuffer();
        }
    }
}
