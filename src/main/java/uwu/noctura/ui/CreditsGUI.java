package uwu.noctura.ui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.luaj.vm2.ast.Str;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.ui.star.StarParticle;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CreditsGUI extends GuiScreen {

    ArrayList<StarParticle> stars = new ArrayList<>();
    @Override
    public void initGui() {
        peopleReasons.put("Error", "Helping me whenever I had issues with anything <3");
        peopleReasons.put("G8LOL", "Helped me with many things, very cool guy in general");
        peopleReasons.put("Roses", "Made me want to revive this client, definitely wouldn't be the same without him");
        peopleReasons.put("Valrod", "Helped me lots when this client was new, in 2022");
        peopleReasons.put("quick/Charlotte", "Same reasons, helped me lots in the early days");
        peopleReasons.put("ric447/Yescheatplus", "Scaffold and other things, cool guy too");
        peopleReasons.put("Amilie", "Hosting the installer on her website   ");
        RenderUtil.generateStars(200, stars, width + 400, height + 400);
    }

    @Override
    public void updateScreen() {

    }

    HashMap<String, String> peopleReasons = new HashMap<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawNocturaScreen();

        // Credits Title
        String s = "Without these people, the client would not be where it is today";
        String big = "Thank you to";


        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(0
                ,0
                ,width,
                height);

        GaussianBlur.renderBlur(7f);
        for(StarParticle ds : stars){
            ds.update(width, height, mouseX, mouseY);
            ds.render(mouseX, mouseY, stars);
        }
        GaussianBlur.renderBlur(5f);

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();
        GlStateManager.resetColor();
        GlStateManager.color(1f,1f,1f,1f);

        ScaledResolution sr = new ScaledResolution(mc);
        int middle = (int)(sr.getScaledWidth() / 2f);
        TTFFontRenderer bigFont = Noctura.INSTANCE.getFontManager().getFont("Good 36");
        TTFFontRenderer smallerFont = Noctura.INSTANCE.getFontManager().getFont("Good 18");
        TTFFontRenderer authorFont = Noctura.INSTANCE.getFontManager().getFont("Good 48");
        TTFFontRenderer reasonFont = Noctura.INSTANCE.getFontManager().getFont("Good 24");

        Gui.drawRect(0,0,0,0,0);
        bigFont.drawStringWithShadow(big, middle - (bigFont.getWidth(big)/2f), 42, -1);
        smallerFont.drawStringWithShadow(s, middle - (smallerFont.getWidth(s)/2f), 24, -1);

        int y = 96;

        for(String author : peopleReasons.keySet()){
            GlStateManager.resetColor();
            String reason = peopleReasons.get(author);
            int xAuthor = (int) ((width / 2f) - authorFont.getWidth(author)/2f);
            int xReason = (int) ((width / 2f) - reasonFont.getWidth(reason)/2f);
            int min = Math.min(xAuthor, xReason);
            int endX = width - min;
            int rectX = (int) ((int) reasonFont.getWidth(longestReason()) / 2f);
            int endRectX = (int) (width - (int) reasonFont.getWidth(longestReason())/2f);

            //Gui.drawRect(rectX - 4, y - 4, endRectX + 4, y + 24 + reasonFont.getHeight("A") + 4 + 4, new Color(0, 0, 0, 90).getRGB());
            Gui.drawRect(4, y - 4, width - 4, y + 24 + reasonFont.getHeight("A") + 4 + 4, new Color(0, 0, 0, 90).getRGB());
            GlStateManager.resetColor();
            authorFont.drawString(author, xAuthor, y, -1);
            reasonFont.drawString(reason, xReason, y + 26, -1);


            y += 52;
        }
    }

    public String longestReason(){
        String longestReason = "";
        for (String reason : peopleReasons.values()) {
            if (reason.length() > longestReason.length()) {
                longestReason = reason;
            }
        }
        return longestReason;
    }
}
