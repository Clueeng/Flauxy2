package uwu.noctura.module.impl.display;

import org.lwjgl.opengl.GL11;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;

import java.awt.*;


@ModuleInfo(name = "Coords", key = -1, cat = Category.Display, displayName = "Coords")
public class CoordsMod extends Module {

    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue); // sat bri
    public BooleanSetting background = new BooleanSetting("Background",true);
    BooleanSetting blur = new BooleanSetting("Blur",true);

    public CoordsMod(){
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
            String coords = "X: " + x + " Y: " + y + " Z: " + z;
            int padding = 6;
            setMoveW(mc.fontRendererObj.getStringWidth(coords) + padding);
            setMoveH(mc.fontRendererObj.FONT_HEIGHT + padding);

            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.prepareScissorBox(getMoveX()
                    ,getMoveY()
                    ,getMoveX() + getMoveW(),
                    getMoveY() + getMoveH());
            //StencilUtil.initStencilToWrite();
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
