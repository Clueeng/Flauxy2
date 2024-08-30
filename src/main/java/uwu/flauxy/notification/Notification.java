package uwu.flauxy.notification;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.Flauxy;
import uwu.flauxy.module.impl.visuals.NotificationModule;
import uwu.flauxy.utils.MathHelper;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.font.FontManager;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.RenderUtil;
import uwu.flauxy.utils.render.shader.StencilUtil;
import uwu.flauxy.utils.render.shader.blur.GaussianBlur;

import java.awt.*;

@Getter
public class Notification {

    String title, description;
    NotificationType type;
    float x, y, length;
    boolean goBack;
    long msCreated, msNow, time;

    public Notification(NotificationType type, String title, String description, long time){
        this.type = type;
        this.time = time;
        this.title = title;
        this.description = description;
        this.msCreated = this.msNow = System.currentTimeMillis();
        String longer = getFont().getWidth(title) > getFont().getWidth(description) ? title : description;
        length = getFont().getWidth(longer) + 8 + 2; // 4 gap on each side + 2 for the little bar
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        y = (sr.getScaledHeight() - (getFont().getHeight(longer) * 2) - 12);
        x = sr.getScaledWidth();
        goBack = false;
    }
    public Notification(NotificationType type, String title, String description){
        this.type = type;
        this.title = title;
        this.time = 2500;
        this.description = description;
        this.msCreated = this.msNow = System.currentTimeMillis();
        String longer = getFont().getWidth(title) > getFont().getWidth(description) ? title : description;
        length = (int) getFont().getWidth(longer) + 8 + 2; // 4 gap on each side + 2 for the little bar
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        y =(sr.getScaledHeight() - (getFont().getHeight(longer) * 2) - 12);
        x = sr.getScaledWidth();
        goBack = false;
    }
    public void render(float partialTicks){
        long dif = Math.abs(msCreated - msNow);

        updatePosition();

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(getX() - 4
                ,getY() - 4
                ,getX() + length + 12,
                getY() + getFont().getHeight(title)*2 + 8);

        GaussianBlur.renderBlur(3f);

        RenderUtil.drawRoundRect(getX() - 4,getY() - 4,getX() + length + 12,getY() + getFont().getHeight(title)*2 + 8,new Color(0,0,0,90).getRGB());
        RenderUtil.drawRect(getX() - 4,getY() + getFont().getHeight(title)*2 + 6, Math.max(getX() - 4,getX() - 4 - ((length * ((double) dif / getTime()))) + length),getY() + getFont().getHeight(title)*2 + 8, NotificationModule.notifColor.getRGB());
        getFont().drawStringWithShadow(getTitle(), getX(), getY(), NotificationModule.notifColor.getRGB());
        getFont().drawStringWithShadow(getDescription(), getX(), getY() + getFont().getHeight(getDescription()) + 2, -1);

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();
    }

    public void updatePosition() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        int screenWidth = sr.getScaledWidth();
        msNow = System.currentTimeMillis();
        if (x <= screenWidth - length + 1 && !goBack && Math.abs(msCreated - msNow) > getTime() ) {
            goBack = true;
        }

        float delta = 0.03f;
        float lerpEnd = goBack ? screenWidth + 3 : screenWidth - length;
        this.x = (float) MathHelper.lerp(delta, this.x, lerpEnd);
        if (x >= screenWidth && goBack) {
            removeSelf();
        }
    }

    public TTFFontRenderer getFont(String name, int size){
        return Flauxy.INSTANCE.getFontManager().getFont(name + " " + size);
    }

    public TTFFontRenderer getFont(){
        return getFont("arial",19);
    }

    public void removeSelf(){
        Flauxy.INSTANCE.getNotificationManager().getQueuedNotifications().remove(this);
    }

}
