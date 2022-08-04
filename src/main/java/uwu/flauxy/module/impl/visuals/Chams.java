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
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Chams", displayName = "Chams", key = -1, cat = Category.Visuals)
public class Chams extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Colored", "Colored", "Basic");
    public NumberSetting red = new NumberSetting("Red", 1, 0, 255, 1).setCanShow(m -> mode.is("Colored"));
    public NumberSetting green = new NumberSetting("Green", 1, 0, 255, 1).setCanShow(m -> mode.is("Colored"));
    public NumberSetting blue = new NumberSetting("Red", 1, 0, 255, 1).setCanShow(m -> mode.is("Colored"));
    public NumberSetting alpha = new NumberSetting("Alpha", 1, 0, 100, 1).setCanShow(m -> mode.is("Colored"));

    public Chams(){
        addSettings(mode, red, green, blue, alpha);
    }

    String res;

    @Override
    public void onEnable() {

    }

    @Override
    public void onEvent(Event e) {
        switch(mode.getMode()){
            case "Colored":{
                coloredMode(e);
                break;
            }
            case "Basic":{
                defaultMode(e);
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
            color(red.getValue(), blue.getValue(), green.getValue(), alpha.getValue());
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, -1100000.0F);
            color(red.getValue(), blue.getValue(), green.getValue(), alpha.getValue());
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.disableColorLogic();
        }
        if(e instanceof EventPostRenderPlayer) {
            EventPostRenderPlayer ev = (EventPostRenderPlayer)e;
            AbstractClientPlayer entity = (AbstractClientPlayer) ev.getEntity();
            GlStateManager.enableColorLogic();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            color(red.getValue(), blue.getValue(), green.getValue(), alpha.getValue());
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0F, 1100000.0F);
            color(red.getValue(), blue.getValue(), green.getValue(), alpha.getValue());
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.disableColorLogic();
        }
    }

    public void color(double r, double g, double b, double a){
        GlStateManager.color((float) (r / red.getMaximum()), (float) (g / green.getMaximum()), (float) (b / blue.getMaximum()), (float) (a / alpha.getMaximum()));

    }

}
