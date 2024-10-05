package uwu.flauxy.module.impl.visuals;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventRender3D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import uwu.flauxy.module.impl.other.ReportAlert;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.WorldUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.nio.channels.WritePendingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ESP", displayName = "ESP", cat = Category.Visuals, key = -1)
public class ESP extends Module {

    private ModeSetting mode = new ModeSetting("Mode", "Box", "Box");
    public NumberSetting thickness = new NumberSetting("Thickness", 1, 1, 5, 0.25);
    BooleanSetting nametags = new BooleanSetting("Nametags", true);
    public ModeSetting color = new ModeSetting("Color", "Astolfo", "Astolfo", "Rainbow", "Custom", "Blend");

    public NumberSetting red = new NumberSetting("Red", 194, 0, 255, 1).setCanShow((m) -> color.is("Custom") || color.is("Blend"));
    public NumberSetting green = new NumberSetting("Green", 82, 0, 255, 1).setCanShow((m) -> color.is("Custom") ||  color.is("Blend"));
    public NumberSetting blue = new NumberSetting("Blue", 226, 0, 255, 1).setCanShow((m) -> color.is("Custom") ||  color.is("Blend"));
    public NumberSetting red2 = new NumberSetting("Red 2", 228, 0, 255, 1).setCanShow((m) ->  color.is("Blend"));
    public NumberSetting green2 = new NumberSetting("Green 2", 139, 0, 255, 1).setCanShow((m) ->  color.is("Blend"));
    public NumberSetting blue2 = new NumberSetting("Blue 2", 243, 0, 255, 1).setCanShow((m) -> color.is("Blend"));
    BooleanSetting showTargets = new BooleanSetting("Show Targets", true);
    BooleanSetting players = new BooleanSetting("Players", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting mobs = new BooleanSetting("Mobs", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting animals = new BooleanSetting("Animals", true).setCanShow(m -> showTargets.getValue());
    BooleanSetting shop = new BooleanSetting("NCP's", false).setCanShow(m -> showTargets.getValue());

    public ESP(){
        addSettings(mode, nametags, thickness, color, red, green, blue, red2, green2, blue2, showTargets, players, mobs, animals, shop);
    }


    @Override
    public void onEvent(Event e) {


        if(e instanceof EventRender2D){
            EventRender2D event = (EventRender2D)e;

            final List<EntityLivingBase> livingEntities = WorldUtil.getLivingEntities(Predicates.and(entity -> true));



            try{
                for (EntityLivingBase entity : livingEntities) {
                    int fontSize = (int) mc.thePlayer.getDistanceSqToEntity(entity);
                    TTFFontRenderer fontRenderer = Flauxy.INSTANCE.fontManager.getFont("arial " + fontSize);
                    // if (!RenderUtil.isEntityInFrustum(entity)) continue;
                    final double diffX = entity.posX - entity.lastTickPosX;
                    final double diffY = entity.posY - entity.lastTickPosY;
                    final double diffZ = entity.posZ - entity.lastTickPosZ;
                    final double deltaX = mc.thePlayer.posX - entity.posX;
                    final double deltaY = mc.thePlayer.posY - entity.posY;
                    final double deltaZ = mc.thePlayer.posZ - entity.posZ;
                    final float partialTicks = event.getParticalTicks();
                    final AxisAlignedBB interpolatedBB = new AxisAlignedBB(
                            entity.lastTickPosX - entity.width / 2 + diffX * partialTicks,
                            entity.lastTickPosY + diffY * partialTicks,
                            entity.lastTickPosZ - entity.width / 2 + diffZ * partialTicks,
                            entity.lastTickPosX + entity.width / 2 + diffX * partialTicks,
                            entity.lastTickPosY + entity.height + diffY * partialTicks,
                            entity.lastTickPosZ + entity.width / 2 + diffZ * partialTicks);
                    final double[][] vectors = new double[8][2];
                    final float[] coords = new float[4];
                    convertTo2D(interpolatedBB, vectors, coords);
                    float minX = coords[0], minY = coords[1], maxX = coords[2], maxY = coords[3];
                    float opacity = 255 - MathHelper.clamp_float(MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 4, 0, 255);
                    opacity = 255;

                    Color c;
                    switch(color.getMode()){
                        case "Blend":{
                            Color col1 = new Color((int)red.getValue(),(int)green.getValue(),(int) blue.getValue());
                            Color col2 = new Color((int)red2.getValue(), (int)green2.getValue(), (int)blue2.getValue());
                            int off = (int) (2 * 75);
                            c = ColorUtils.blendThingC(2F, (long) (8 * off), col1, col2);
                            break;
                        }
                        case "Rainbow":{
                            c = ColorUtils.getRainbowC(3, 0.40f, 1, 8 * 180L);
                            break;
                        }
                        case "Custom":{
                            c = new Color((int) red.getValue(), (int) blue.getValue(), (int) green.getValue());
                            break;
                        }
                        case "Astolfo":{
                            c = ColorUtils.astolfoC(3, 0.45f, 1, 8 * 180L);
                            break;
                        }
                        default:
                            c = new Color(0, 0, 0);
                    }

                    if(!isValid(entity)) continue;
                    if(nametags.getValue()){
                        //if(entity instanceof EntityPlayer){
                        float bottom = maxY + ((minY + maxY) / maxY);
                        float top = minY - ((maxY / minY)*10);
                        boolean hacking = ReportAlert.hackers.contains(entity.getName());
                        fontRenderer.drawStringWithShadow(hacking ? entity.getName() + " [HACKER]" : entity.getName(), (minX + maxX) / 2 - (fontRenderer.getWidth(hacking ? entity.getName() + " [HACKER]" : entity.getName()) / 2), top, hacking ? Color.red.getRGB() : -1);
                        //D}
                    }

                    switch(mode.getMode()){
                        case "Box":{
                            RenderUtil.pre3D();
                            glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, opacity / 255f);
                            glLineWidth((float) (thickness.getValue() * 4f));
                            glBegin(GL_LINE_LOOP);

                            glVertex2f(minX, minY);
                            glVertex2f(maxX, minY);
                            glVertex2f(maxX, maxY);
                            glVertex2f(minX, maxY);
                            glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 255);
                            glEnd();

                            glLineWidth((float) thickness.getValue());
                            GlStateManager.color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, (float) c.getAlpha() / 255);
                            glBegin(GL_LINE_LOOP);
                            glVertex2f(minX, minY);
                            glVertex2f(maxX, minY);
                            glVertex2f(maxX, maxY);
                            glVertex2f(minX, maxY);
                            glEnd();
                            RenderUtil.post3D();
                            break;
                        }
                    }

                }
            }catch (NullPointerException ed){
                ed.printStackTrace();
            }
        }

        if(e instanceof EventRender3D){

        }
    }

    private void convertTo2D(AxisAlignedBB interpolatedBB, double[][] vectors, float[] coords) {
        if(coords == null || vectors == null || interpolatedBB == null) return;
        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        vectors[0] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[1] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[2] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[3] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[4] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[5] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[6] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);
        vectors[7] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);
        try{
            float minW = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
            float maxW = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
            if (maxW > 1 || minW < 0) return;
            float minX = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
            float maxX = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
            final float top = (mc.displayHeight / (float) new ScaledResolution(mc).getScaleFactor());
            float minY = (float) (top - Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
            float maxY = (float) (top - Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
            coords[0] = minX;
            coords[1] = minY;
            coords[2] = maxX;
            coords[3] = maxY;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public boolean isValid(Entity e){
        boolean finalValid = false;
        if(e instanceof EntityPlayer && players.getValue()){
            if((e.getName().equals("UPGRADES") || e.getName().equals("SHOP"))){
                finalValid = !shop.getValue();
            }else{
                finalValid = true;
            }
        }
        if(e instanceof EntityMob && mobs.getValue()){
            finalValid = true;
        }
        if(e instanceof EntityAnimal && animals.getValue()){
            finalValid = true;
        }

        return finalValid;
    }

    private void renderNameTag(String text, double x, double y, double z, Entity entity) {
    }

}
