package uwu.flauxy.utils.font;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import uwu.flauxy.Flauxy;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
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

    public static TTFFontRenderer getFont() {
        return Flauxy.INSTANCE.fontManager.getFont("arial 21");
    }
    public static TTFFontRenderer getFont(String fontName, int size){
        return Flauxy.INSTANCE.fontManager.getFont(fontName + " " + size);
    }

    public FontManager() {
        instance = this;
        //10 cuz more threads = better performance
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        ConcurrentLinkedQueue<fr.flailyclient.utils.fonts.TextureData> textureQueue = new ConcurrentLinkedQueue<>();
        defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("Verdana", Font.PLAIN, 18));
        try {
            for(int i = 8; i < 49; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Main.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Main " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for(int i = 8; i < 49; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Japan.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Japan " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for(int i = 8; i < 49; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/iskpota.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Iskpota " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for(int i = 8; i < 49; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Good.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Good " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
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