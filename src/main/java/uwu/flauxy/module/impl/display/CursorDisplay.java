package uwu.flauxy.module.impl.display;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MouseHelper;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MathHelper;
import uwu.flauxy.utils.render.RenderUtil;
import uwu.flauxy.utils.render.shader.StencilUtil;
import uwu.flauxy.utils.render.shader.blur.BloomUtil;
import uwu.flauxy.utils.render.shader.blur.GaussianBlur;
import uwu.flauxy.utils.render.shader.blur.KawaseBlur;

import java.awt.*;

@ModuleInfo(name = "CursorDisplay", cat = Category.Display, displayName = "Cursor Display", key = -1)
public class CursorDisplay extends Module {

    NumberSetting size = new NumberSetting("Size", 1.0, 0.5, 2.0, 0.05);
    BooleanSetting blur = new BooleanSetting("Blur",true);

    public CursorDisplay(){
        setHudMoveable(true);
        setMoveX(20);
        setMoveY(100);
        addSettings(size, blur);
    }

    float deltaX, deltaY;
    float cursorPosX, cursorPosY;
    float middleX, middleY; // goal end pos
    float lerpedW, lerpedH;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender2D){
            lerpedW = (float) MathHelper.lerp(0.1,lerpedW,(float) (150 * size.getValue()));
            lerpedH = (float) MathHelper.lerp(0.1,lerpedH,(float) (100f * size.getValue()));
            setMoveW(lerpedW);
            setMoveH(lerpedH);

            deltaX = (float) (mc.mouseHelper.deltaX * size.getValue());
            deltaY = (float) (-mc.mouseHelper.deltaY * size.getValue());
            middleY = getMoveY() + (getMoveH() / 2f);
            middleX = getMoveX() + (getMoveW() / 2f);

            cursorPosX = (float) MathHelper.lerp(0.1,cursorPosX,middleX + deltaX);
            cursorPosY = (float) MathHelper.lerp(0.1,cursorPosY,middleY + deltaY);
            cursorPosX = net.minecraft.util.MathHelper.clamp_float(cursorPosX, getMoveX(), getMoveX() + getMoveW());
            cursorPosY = net.minecraft.util.MathHelper.clamp_float(cursorPosY, getMoveY(), getMoveY() + getMoveH());


            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.prepareScissorBox(getMoveX()
                    ,getMoveY()
                    ,getMoveX() + getMoveW(),
            getMoveY() + getMoveH());


           // StencilUtil.initStencilToWrite();
            //StencilUtil.readStencilBuffer(1);

            //StencilUtil.initStencilToWrite();
            //StencilUtil.readStencilBuffer(1);
            if(blur.isEnabled()){
                GaussianBlur.renderBlur(3f);
            }

            Gui.drawRect(getMoveX(),getMoveY(),getMoveX() + getMoveW(),getMoveY() + getMoveH(),new Color(0, 0, 0, 90).getRGB());
            Gui.drawRect(getMoveX(),getMoveY() + (getMoveH()/2f) - 0.5f, getMoveX() + getMoveW(), getMoveY() + (getMoveH()/2f) + 0.5f, new Color(60,60,60,120).getRGB());
            Gui.drawRect(getMoveX() + (getMoveW() / 2f) - 0.5f,getMoveY(),getMoveX() + (getMoveW() / 2f) + 0.5f,getMoveY() + getMoveH(),new Color(60,60,60,120).getRGB());
            RenderUtil.drawCircle(cursorPosX,cursorPosY,3,-1);



            GL11.glDisable(3089);
            GL11.glPopMatrix();
            StencilUtil.uninitStencilBuffer();
        }
    }
}
