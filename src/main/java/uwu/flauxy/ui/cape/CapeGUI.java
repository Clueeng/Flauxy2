package uwu.flauxy.ui.cape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.Flauxy;
import uwu.flauxy.cape.Cape;
import uwu.flauxy.cape.CapeManager;
import uwu.flauxy.module.impl.display.ModuleCapeGUI;
import uwu.flauxy.utils.MathHelper;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class CapeGUI extends GuiScreen {

    public CapeGUI(ScaledResolution sr){
        this.sr = sr;
    }
    private ScaledResolution sr;
    private int capeHeight = 32, capeWidth = 64, boxExpand = 8, maxPerRow = 5;
    private int startX = 0, endX = 0,
            startY = 0, endY = 0;
    private final CapeManager capeManager = Flauxy.INSTANCE.getCapeManager();

    private final Color outline = new Color(23,23,23), // outline bg
    bg = new Color(33,33,33),
    capeBack = new Color(44, 44, 44), // bg
    accent = new Color(70, 47, 106), // outline ?
    brightAccent = new Color(123, 89, 176); // text ?


    @Override
    public void initGui() {
        super.initGui();
        ModuleCapeGUI c = Flauxy.INSTANCE.getModuleManager().getModule(ModuleCapeGUI.class);
        c.setToggled(false);
        capesYoffset = 0;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    int OFFSET_TOP = 24;

    float capesYoffset = 0, capesYoffsetEnd;
    float opacityEnd, opacity;

    // IDEA: MAKE A GUI OPENING ANIMATION USING LERP/OR SMTH WITH AN OVERLAY ABOVE THE GUI

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        /*
        if(Mouse.hasWheel() && RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, height))
        {
            int wheel = Mouse.getDWheel();
            if(wheel > 0 && offset - (moduleHeight - 1) > 0) {
                offset -= moduleHeight;
            } else if(wheel < 0 && offset + (moduleHeight - 1) <= offCat.get() - height + categoryNameHeight) {
                offset += moduleHeight;
            }
        }
         */

        sr = new ScaledResolution(mc);
        startX = sr.getScaledWidth() / 5;
        endX = sr.getScaledWidth() - startX - startX;
        startY = sr.getScaledHeight() / 4;
        endY = sr.getScaledHeight() - startY - startY;
        int rows = (capeManager.capes.size() + maxPerRow - 1) / maxPerRow;

        if(Mouse.hasWheel() && RenderUtil.hover(startX, startY, mouseX, mouseY, endX, endY)){
            int wheel = Mouse.getDWheel();
            if(wheel > 0){
                // up so we subtract
                capesYoffsetEnd += 24;
            }else if(wheel < 0){
                capesYoffsetEnd -= 24;
            }
            capesYoffset = (float) MathHelper.lerp(0.1,capesYoffset,capesYoffsetEnd);
            if(capesYoffset > 0){
                capesYoffset = 0;
                capesYoffsetEnd = 0;
                opacityEnd = 0;
            } // 6 * 24 * rows
            //
            //opacityEnd = 1;
            if(Math.abs(capesYoffset) > 10){
                opacityEnd = Math.min(1,Math.abs(capesYoffset) / 20);
            }
            int capped  = -Math.abs(capeHeight * (rows-1));
            if(capesYoffset < capped){
                capesYoffset = capped;
                capesYoffsetEnd = capped;
            }
        }
        opacity = (float) MathHelper.lerp(0.01f,opacity,opacityEnd);


        RenderUtil.drawRoundedRect3(startX-3,startY-3,endX+6,endY+6, 4,outline.darker().getRGB());
        RenderUtil.drawRoundedRect3(startX,startY,endX,endY, 4,bg.getRGB());
        RenderUtil.drawRoundedRect3(startX,startY,endX,16, 0,outline.getRGB());
        mc.fontRendererObj.drawString("Cape Manager",startX + (endX / 2) - (mc.fontRendererObj.getStringWidth("Cape Manager")/2), startY + 4,-1);

        float addedX = capeWidth + (boxExpand * 6);
        float addedY = capeHeight + boxExpand;
        int index = 0;
        float capeX = startX + (boxExpand * 4);
        float capeY = (int) (startY + boxExpand + OFFSET_TOP + capesYoffset);



        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(startX-3,startY+16,endX+6+startX,endY+startY);
        // fade in
        for(Cape c : capeManager.capes){
            index++;
            // GuiInventory.drawEntityOnScreen((int) x + 16, (int) y + 55, 25, target.rotationYaw, -target.rotationPitch, target);
            //RenderUtil.drawRoundedRect2(capeX-1,capeY-1,capeWidth+1,capeHeight+1,4, brightAccent.getRGB());
            int modelWidth = boxExpand * 2;
            //RenderUtil.drawRoundedRect3(capeX,capeY + 1,capeX,capeY + 1,4, capeBack.getRGB());
            //Gui.drawRect(capeX, capeY, capeX + capeWidth, capeY + capeHeight,-1);
            // capeManager.CURRENT_CAPE = index-1;
            if(capeManager.CURRENT_CAPE == index-1){
                RenderUtil.drawRoundedRect2(capeX-1,capeY-1, capeX + capeWidth + 1, capeY  + capeHeight + 1,4,brightAccent.getRGB());
            }else{
                RenderUtil.drawRoundedRect2(capeX-1,capeY-1, capeX + capeWidth + 1, capeY  + capeHeight + 1,4,accent.getRGB());
            }
            RenderUtil.drawRoundedRect2(capeX,capeY, capeX + capeWidth, capeY  + capeHeight,4,capeBack.getRGB());
            mc.fontRendererObj.drawString(c.getName(), (int) (capeX + (capeWidth / 2) - (mc.fontRendererObj.getStringWidth(c.getName())/2)), (int) capeY + 4, brightAccent.getRGB());
            // scale to 90%
            //GlStateManager.scale(0.9f,0.9f,0.9f);
            mc.fontRendererObj.drawString(c.getSource(), (int) (capeX + (capeWidth / 2) - (mc.fontRendererObj.getStringWidth(c.getSource())/2)), (int) capeY + 4 + 16, accent.getRGB());

            GlStateManager.color(1f,1f,1f,1f);

            capeX += addedX;
            if(index % maxPerRow == 0){ // its the forth so we draw before that
                capeY += addedY;
                capeX = startX + (boxExpand * 4);
            }
        }

        GL11.glDisable(3089);
        GL11.glPopMatrix();


        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(sr.getScaledWidth() - (capeWidth * 2) - 48
                ,(int) startY + 16,sr.getScaledWidth() - (capeWidth * 2) + 48
                ,(int) startY + 32 + (32 * 6)   );

        EntityPlayerSP fake = mc.thePlayer;
        RenderUtil.drawRoundedRect2(sr.getScaledWidth() - (capeWidth * 2) - 48,(int) startY + 16, sr.getScaledWidth() - (capeWidth * 2) + 48,  (int) startY + 32 + (32 * 6), 7, new Color(0, 0, 0, 120).getRGB());
        GuiInventory.drawEntityOnScreenCape((int) sr.getScaledWidth() - (capeWidth * 2), (int) (startY) + (capeHeight * 7) - 12, 32 * 3, mouseX, -mc.thePlayer.rotationPitch, fake);


        GL11.glDisable(3089);
        GL11.glPopMatrix();

        RenderUtil.drawGradientRect(startX,startY + 14, endX + startX,startY + 64, new Color(outline.getRed()/255f,outline.getGreen()/255f,outline.getBlue()/255f,0).getRGB(),new Color(outline.getRed()/255f,outline.getGreen()/255f,outline.getBlue()/255f,opacity).getRGB());

    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        sr = new ScaledResolution(mc);
        startX = sr.getScaledWidth() / 5;
        endX = sr.getScaledWidth() - startX - startX;
        startY = sr.getScaledHeight() / 4;
        endY = sr.getScaledHeight() - startY - startY;
        int rows = (capeManager.capes.size() + maxPerRow - 1) / maxPerRow;
        float addedX = capeWidth + (boxExpand * 6);
        float addedY = capeHeight + boxExpand;
        int index = 0;
        float capeX = startX + (boxExpand * 4);
        float capeY = (int) (startY + boxExpand + OFFSET_TOP + capesYoffset);
        for(Cape c : capeManager.capes){
            index++;
            int modelWidth = boxExpand * 2;

            if(RenderUtil.hover((int) capeX, (int) capeY, mouseX,mouseY,capeWidth,capeHeight)){
                //System.out.println(c.getName() + " " + capeManager.capes.get(index-1).getName());
                capeManager.CURRENT_CAPE = index-1;
            }
            capeX += addedX;
            if(index % maxPerRow == 0){ // its the forth so we draw before that
                capeY += addedY;
                capeX = startX + (boxExpand * 4);
            }
        }
    }

}
