package uwu.flauxy;

import lombok.Getter;
import org.lwjgl.opengl.Display;
import uwu.flauxy.alts.AltManager;
import uwu.flauxy.event.Event;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.display.ArrayList;
import uwu.flauxy.module.impl.display.HUD;
import uwu.flauxy.module.impl.player.Noslow;
import uwu.flauxy.module.impl.player.Sprint;
import uwu.flauxy.module.impl.visuals.Animations;
import uwu.flauxy.utils.config.ConfigUtil;
import uwu.flauxy.utils.config.Folder;
import uwu.flauxy.utils.font.FontManager;
import viamcp.ViaMCP;

import java.io.File;

@Getter
public enum Flauxy {
    INSTANCE;

    public String name = "Flauxy", version = "1.0";
    public ModuleManager moduleManager;
    public FontManager fontManager;
    public AltManager altManager;
    private ConfigUtil configManager;
    @Getter
    private DiscordRP discordRP = new DiscordRP();

    public void init(){
        try
        {
            ViaMCP.getInstance().start();
            ViaMCP.getInstance().initAsyncSlider();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // inits shit
        Folder.init();
        moduleManager = new ModuleManager();
        configManager = new ConfigUtil();
        fontManager = new FontManager();
        altManager = new AltManager();
        Display.setTitle(name + " - " + version);
        moduleManager.getModule(HUD.class).toggle();
        moduleManager.getModule(ArrayList.class).toggle();
        moduleManager.getModule(Noslow.class).toggle();
        moduleManager.getModule(Sprint.class).toggle();
        moduleManager.getModule(Animations.class).toggle();
        File file = new File(Folder.auxware + "/Configs/Default.txt");
        if (file.exists()) {
            configManager.load("Default");
        }
        discordRP.init();
    }

    public static void onEvent(Event e){
        for(Module m : ModuleManager.modules){
            if(m.isToggled()) m.onEvent(e);
        }
    }

    public static void onEventIgnore(Event e){
        for(Module m : ModuleManager.modules){
            m.onEventIgnore(e);
        }
    }

    public void onShutDownApplet(){
        discordRP.close();
    }


}