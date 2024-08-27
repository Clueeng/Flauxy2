package uwu.flauxy.module.impl.display;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MathHelper;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

@ModuleInfo(name = "PotionHUD", displayName = "Potion HUD", key = -1, cat = Category.Display)
public class PotionHUD extends Module {
    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting saturationValue = new GraphSetting("Saturation", 0, 0, 0, 100, 0, 100, 1, 1, hue);
    public BooleanSetting drawBackground = new BooleanSetting("Background",true);

    protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    public PotionHUD(){
        addSettings(hue, saturationValue, drawBackground);
        setHudMoveable(true);
        hue.setColorDisplay(true);
        saturationValue.setColorDisplay(true);
        setMoveX(10);
        setMoveY(200);
    }
    float lerpedHeight = 0, lerpedWidth = 0;

    ArrayList<PotionEffect> idlePotions = new ArrayList<>();

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRender2D) {
            EventRender2D event = (EventRender2D) e;

            // Retrieve the color based on settings
            Color color = getColorFromSettings(hue, saturationValue);
            int heightGap = 24;
            int padding = 10; // Padding around the text

            //
            int fullHeight = 0;
            int maxTextWidth = 0;
            idlePotions = copied(mc.thePlayer.getActivePotionEffects());
            if((mc.thePlayer.getActivePotionEffects() == null || mc.thePlayer.getActivePotionEffects().isEmpty()) && mc.currentScreen instanceof GuiChat){
                idlePotions.add(new PotionEffect(1, 20, 1, true, true));
            }
            for (PotionEffect p : idlePotions) {
                fullHeight += heightGap;
                String name = StatCollector.translateToLocal(p.getEffectName());
                int amplifier = p.getAmplifier() + 1;
                String text = name + " " + romanNumber(amplifier) + " (" + Potion.getDurationString(p) + ")";
                int textWidth = mc.fontRendererObj.getStringWidth(text);
                maxTextWidth = Math.max(maxTextWidth, textWidth);
            }

            // Adjust the width to be the maximum text width plus padding
            int fullWidth = Math.max(0, maxTextWidth + 2 * padding); // Initial width is 120; adjust as needed

            // Draw the background rectangle
            int x = (int) getMoveX();
            int y = (int) getMoveY();

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST); // Enable scissor test for clipping

            lerpedHeight = (float) MathHelper.lerp(0.06f, lerpedHeight, fullHeight);
            lerpedWidth = (float) MathHelper.lerp(0.06f, lerpedWidth, fullWidth);

            RenderUtil.prepareScissorBox(x, y, x + (int) lerpedWidth, y + (int) lerpedHeight);
            Gui.drawRect(x, y, x + (int) lerpedWidth, y + (int) lerpedHeight, new Color(0, 0, 0, 90).getRGB());

            int currentY = y + 5;
            for (PotionEffect p : mc.thePlayer.getActivePotionEffects()) {
                String name = StatCollector.translateToLocal(p.getEffectName());
                int amplifier = p.getAmplifier() + 1;
                String text = name + " " + romanNumber(amplifier) + " (" + Potion.getDurationString(p) + ")";

                mc.fontRendererObj.drawStringWithShadow(text, x + padding, currentY + 2, color.getRGB());
                currentY += heightGap;
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST); // Disable scissor test
            GL11.glPopMatrix();

            setMoveW(fullWidth);
            setMoveH(fullHeight);
        }
    }

    public ArrayList<PotionEffect> copied(Collection<PotionEffect> original){
        ArrayList<PotionEffect> copy = new ArrayList<>();
        for(PotionEffect o : original){
            PotionEffect copied = new PotionEffect(o);
            copy.add(copied);
        }
        return copy;
    }

    public String romanNumber(int amplifier){
        switch (amplifier){
            default:
                return String.valueOf(amplifier);
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
        }
    }

}
