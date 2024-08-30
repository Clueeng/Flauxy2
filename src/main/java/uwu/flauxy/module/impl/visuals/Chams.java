package uwu.flauxy.module.impl.visuals;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventPostRenderPlayer;
import uwu.flauxy.event.impl.EventRenderPlayer;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

import java.awt.*;

@ModuleInfo(name = "Chams", displayName = "Chams", key = -1, cat = Category.Visuals)
public class Chams extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Colored", "Colored", "Basic");
    public NumberSetting hue1 = new NumberSetting("Visible Color",0,0,360,1).setCanShow(m -> mode.is("Colored"));
    public GraphSetting sat1 = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue1).setCanShow(m -> mode.is("Colored"));
    public NumberSetting hue2 = new NumberSetting("Hidden Color",0,0,360,1).setCanShow(m -> mode.is("Colored"));
    public GraphSetting sat2 = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue2).setCanShow(m -> mode.is("Colored"));
    public NumberSetting alpha = new NumberSetting("Alpha", 1, 0, 255, 1).setCanShow(m -> mode.is("Colored"));
    public BooleanSetting showTargets = new BooleanSetting("Show Targets", false);
    public BooleanSetting players = new BooleanSetting("Players", true).setCanShow(m -> showTargets.getValue());
    public BooleanSetting animals = new BooleanSetting("Animals", true).setCanShow(m -> showTargets.getValue());
    public BooleanSetting mobs = new BooleanSetting("Mobs", true).setCanShow(m -> showTargets.getValue());

    public Chams(){
        addSettings(mode, hue1, sat1, hue2, sat2, alpha, showTargets, players, animals, mobs);
        hue1.setColorDisplay(true);
        hue2.setColorDisplay(true);
        sat1.setColorDisplay(true);
        sat2.setColorDisplay(true);
    }

    String res;

    // RendererLivingEntity.java ln 275

    @Override
    public void onEnable() {

    }

    @Override
    public void onEvent(Event e) {
        switch(mode.getMode()){
            case "Colored":{
                //coloredMode(e);
                break;
            }
            case "Basic":{
                //defaultMode(e);
                break;
            }
        }
    }

    public void defaultMode(Event e){
        if(e instanceof EventRenderPlayer) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, -1100000.0F);
        }
        if(e instanceof EventPostRenderPlayer) {
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1100000.0F);
        }
    }
    public void coloredMode(Event e){
        if(e instanceof EventRenderPlayer) {
            EventRenderPlayer ev = (EventRenderPlayer)e;
            AbstractClientPlayer entity = (AbstractClientPlayer) ev.getEntity();
            GlStateManager.enableColorLogic();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            Color c = getColorFromSettings(hue1,sat1);

            color(c.getRed(), c.getGreen(), c.getBlue(), alpha.getValue());
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, -1100000.0F);
            color(c.getRed(), c.getGreen(), c.getBlue(), alpha.getValue());
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.disableColorLogic();
        }
        if(e instanceof EventPostRenderPlayer) {
            EventPostRenderPlayer ev = (EventPostRenderPlayer)e;
            AbstractClientPlayer entity = (AbstractClientPlayer) ev.getEntity();
            Color c = getColorFromSettings(hue1,sat1);
            GlStateManager.enableColorLogic();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            color(c.getRed(), c.getGreen(), c.getBlue(), alpha.getValue());
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1100000.0F);
            color(c.getRed(), c.getGreen(), c.getBlue(), alpha.getValue());
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.disableColorLogic();
        }
    }

    public void color(double r, double g, double b, double a){
        GlStateManager.color((float) (r / 255f), (float) (g / 255f), (float) (b / 255f), (float) (a / alpha.getMaximum()));

    }

}
