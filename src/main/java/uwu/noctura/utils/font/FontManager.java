package uwu.noctura.utils.font;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.ui.splash.SplashScreen;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.*;

public class FontManager {

    public TTFFontRenderer defaultFont;

    public FontManager getInstance() {
        return instance;
    }

    public TTFFontRenderer getFont(String key) {
        return fonts.getOrDefault(key, defaultFont);
    }

    private FontManager instance;

    private HashMap<String, TTFFontRenderer> fonts = new HashMap<>();

    public static TTFFontRenderer getFont() {
        return Noctura.INSTANCE.fontManager.getFont("arial 21");
    }
    public static TTFFontRenderer getFont(String fontName, int size){
        return Noctura.INSTANCE.fontManager.getFont(fontName + " " + size);
    }

    public FontManager() {
        instance = this;
        //10 cuz more threads = better performance
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        ConcurrentLinkedQueue<TextureData> textureQueue = new ConcurrentLinkedQueue<>();
        defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("Verdana", Font.PLAIN, 18));
        try {
            for(int i = 8; i < 49; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Main.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Main " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                SplashScreen.set(6, "Loading Main (" + i + ") font");
            }
            for(int i = 8; i < 25; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Poppins-Regular.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Poppins " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                SplashScreen.set(6, "Loading Poppins (" + i + ") font");
            }
            for(int i = 8; i < 28; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/iskpota.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Iskpota " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                SplashScreen.set(6, "Loading Iskpota (" + i + ") font");
            }
            for(int i = 8; i < 49; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/Good.ttf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("Good " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                SplashScreen.set(6, "Loading Good (" + i + ") font");
            }
            for(int i = 8; i < 25; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/icons.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("icons " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                SplashScreen.set(6, "Loading Icons (" + i + ") font");
            }
            for(int i = 8; i < 25; i++){
                InputStream istream = getClass().getResourceAsStream("/assets/minecraft/icons2.otf");
                Font myFont = Font.createFont(Font.PLAIN, istream);
                myFont = myFont.deriveFont(Font.PLAIN, i);
                fonts.put("icons2 " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
                SplashScreen.set(6, "Loading Icons2 (" + i + ") font");
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
                TextureData textureData = textureQueue.poll();
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