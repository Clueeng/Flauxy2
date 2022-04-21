package uwu.flauxy.utils.font;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.utils.font.TTFFontRenderer;

import java.awt.*;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class FontManager {

    private TTFFontRenderer defaultFont;

    public FontManager getInstance() {
        return instance;
    }

    public TTFFontRenderer getFont(String key) {
        return fonts.getOrDefault(key, defaultFont);
    }

    private FontManager instance;

    private HashMap<String, TTFFontRenderer> fonts = new HashMap<>();

    public FontManager() {
        instance = this;
        //10 cuz more threads = better performance
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        ConcurrentLinkedQueue<fr.flailyclient.utils.fonts.TextureData> textureQueue = new ConcurrentLinkedQueue<>();
        defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("Verdana", Font.PLAIN, 18));
        try {
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/SF-UI-Display-Regular.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("SFR " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/Comfortaa.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Cum " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/Astomero.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Astomero " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/sfui.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("SFUI " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/SF-UI-Display-Bold.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("SFB " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/SF-UI-Display-Medium.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("SFM " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/SF-UI-Display-Light.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("SFL " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/Jigsaw-Regular.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("JIGR " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}) {
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/ttf/Insidious1-Regular.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Ins " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for(int i : new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}){

                fonts.put("Verdana " + i, new TTFFontRenderer(executorService, textureQueue, new Font("Verdana", Font.PLAIN, i)));
            }

            fonts.put("Verdana Bold 16", new TTFFontRenderer(executorService, textureQueue, new Font("Verdana Bold", Font.PLAIN, 16)));
            fonts.put("Verdana Bold 20", new TTFFontRenderer(executorService, textureQueue, new Font("Verdana Bold", Font.PLAIN, 20)));
        } catch (Exception ignored) {

        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                fr.flailyclient.utils.fonts.TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());

                // Sets the texture parameter stuff.
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                // Uploads the texture to opengl.
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureData.getBuffer());
            }
        }
    }
}