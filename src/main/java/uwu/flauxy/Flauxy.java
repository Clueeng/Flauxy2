package uwu.flauxy;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
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
import uwu.flauxy.utils.MinecraftInstance;
import uwu.flauxy.utils.config.ConfigManager;
import uwu.flauxy.utils.config.ConfigUtil;
import uwu.flauxy.utils.config.Folder;
import uwu.flauxy.utils.font.FontManager;
import viamcp.ViaMCP;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public enum Flauxy implements MinecraftInstance {
    INSTANCE;

    public String name = "Flauxy", version = "1.0";
    public Path clientDirectory = Paths.get(mc.mcDataDir.getAbsolutePath(), name), configsDirectory = Paths.get(clientDirectory.toFile().getAbsolutePath(), "configs");
    public ModuleManager moduleManager;
    public FontManager fontManager;
    public AltManager altManager;
    private ConfigUtil configManager;
    private final ConfigManager nonShittyConfigManager = new ConfigManager();
    @Getter
    public DiscordRPC discordRPC;
    @Getter
    private DiscordRP discordRP = new DiscordRP();

    public void init(){
        discordRPC = DiscordRPC.INSTANCE;
        initDiscordApp();
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
        uwu.flauxy.module.impl.other.util.Folder.init(); // for killsults
        moduleManager = new ModuleManager();
        configManager = new ConfigUtil();
        fontManager = new FontManager();
        altManager = new AltManager();
        Display.setTitle("Minecraft 1.8.9");
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
        nonShittyConfigManager.init();
    }

    public void initDiscordApp(){
        String applicationID = "972902027300581406";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        discordRPC.Discord_Initialize(applicationID, handlers, true, ""); // empty string is steam id
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.largeImageKey = "image1";
        presence.largeImageText = "Flauxy";
        presence.details = "Loading Client";
        presence.state = "Build ver 0";
        discordRPC.Discord_UpdatePresence(presence);
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