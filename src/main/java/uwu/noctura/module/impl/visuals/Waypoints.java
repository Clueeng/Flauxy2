package uwu.noctura.module.impl.visuals;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.event.impl.EventRender3D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.waypoint.Waypoint;

import java.awt.*;


@ModuleInfo(name = "Waypoints", displayName = "Waypoints", key = -1, cat = Category.Visuals)
public class Waypoints extends Module {

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender3D) {
            for (Waypoint w : Noctura.INSTANCE.waypointManager.waypoints) {
                if(!wayCheck(w)  || mc.thePlayer == null || mc.thePlayer.ticksExisted <= 1
                        || mc.thePlayer.getDistance(w.x,w.y,w.z) < 12){
                    continue;
                }
                renderBeaconBeam(w.x,w.y,w.z,((EventRender3D) e).getParticalTicks());
            }
        }
        if(e instanceof EventRender2D){
            for(Waypoint w : Noctura.INSTANCE.waypointManager.waypoints){
                if(!wayCheck(w) || mc.thePlayer == null || mc.thePlayer.ticksExisted <= 1
                || mc.thePlayer.getDistance(w.x,w.y,w.z) < 12 || mc.thePlayer.rotationPitch >= 88 || mc.thePlayer.rotationPitch <= -88){
                    continue;
                }
                float[] coords = new float[2];
                convertTo2D(w.x, w.y, w.z, coords);
                String text = w.name + " (" + (int)w.x + " " + (int)w.y + " " + (int)w.z + ")";
                text = w.name + " (" + (int)mc.thePlayer.getDistance(w.x,w.y,w.z) + "m)";
                float stWidth = mc.fontRendererObj.getStringWidth(text);
                float x = coords[0] - ((float) stWidth /2);
                float y = coords[1];
                int size = 6;
                if(y == 0){
                    return;
                }
                if(!Float.isNaN(x) && !Float.isNaN(y)){
                    Gui.drawRect(x - size, y - size, x + stWidth + size, y + mc.fontRendererObj.FONT_HEIGHT + size,new Color(0, 0, 0, 90).getRGB());
                    RenderUtil.drawUnfilledRectangle(x - size,y - size,x + stWidth + size,y + mc.fontRendererObj.FONT_HEIGHT + size, 0,new Color(255,255,255,255).getRGB());
                    mc.fontRendererObj.drawStringWithShadow(text, x, y, -1);
                }
            }
        }
    }

    @Override
    public void onEnable() {
    }

    private void convertTo2D(double x3D, double y3D, double z3D, float[] coords) {
        if (coords == null) return;
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        double translatedX = x3D - camX;
        double translatedY = y3D - camY;
        double translatedZ = z3D - camZ;
        double[] screenPos = RenderUtil.project2D(translatedX, translatedY, translatedZ);
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float top = (mc.displayHeight / (float) scaledResolution.getScaleFactor());
        float w = (float) screenPos[2];
        if (w > 1 || w < 0) return;
        coords[0] = (float) screenPos[0]; // X coordinate
        coords[1] = top - (float) screenPos[1]; // Y coordinate (flipped)
    }

    private void renderBeaconBeam(double x, double y, double z, float partialTicks) {
        double xRender = x - mc.getRenderManager().viewerPosX;
        double yRender = y - mc.getRenderManager().viewerPosY;
        double zRender = z - mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/entity/beacon_beam.png"));

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        double beamHeight = 256.0;
        float beamRotation = (float) (mc.theWorld.getTotalWorldTime() % 40L + partialTicks);
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        double d1 = -0.8D;
        double d2 = 0.8D;

        double d3 = beamHeight * 2.5D + beamRotation;
        double d4 = 0.0D;

        worldrenderer.pos(xRender + d1, yRender, zRender + d1).tex(1.0D, d3).color(255, 255, 255, 128).endVertex();
        worldrenderer.pos(xRender + d1, yRender + beamHeight, zRender + d1).tex(1.0D, d4).color(255, 255, 255, 128).endVertex();
        worldrenderer.pos(xRender + d2, yRender + beamHeight, zRender + d2).tex(0.0D, d4).color(255, 255, 255, 128).endVertex();
        worldrenderer.pos(xRender + d2, yRender, zRender + d2).tex(0.0D, d3).color(255, 255, 255, 128).endVertex();

        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    boolean wayCheck(Waypoint waypoint){
        if(mc.isSingleplayer()){
            return waypoint.identifier.equals(Noctura.INSTANCE.getWaypointManager().getWorldName());
        }else{
            return waypoint.identifier.equals(mc.getCurrentServerData().serverIP);
        }
    }

}
