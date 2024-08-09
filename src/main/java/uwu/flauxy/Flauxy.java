package uwu.flauxy;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.lwjgl.opengl.Display;
import uwu.flauxy.alts.Alt;
import uwu.flauxy.alts.AltManager;
import uwu.flauxy.cape.CapeManager;
import uwu.flauxy.commands.CommandManager;
import uwu.flauxy.commands.impl.CommandGhost;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Getter
public enum Flauxy implements MinecraftInstance {
    INSTANCE;

    public String name = "Flauxy", version = "1.0";
    public Path clientDirectory = Paths.get(mc.mcDataDir.getAbsolutePath(), name), configsDirectory = Paths.get(clientDirectory.toFile().getAbsolutePath(), "configs");
    public ModuleManager moduleManager;
    public FontManager fontManager;
    public AltManager altManager;
    private ConfigUtil configManager;
    private CapeManager capeManager;
    private CommandManager commandManager;
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
        commandManager = new CommandManager();
        setGhost(false);
        fontManager = new FontManager();
        // Trying to add before altmanager i guess
        altManager = new AltManager();
        File altsFile = new File(clientDirectory + "/alts.txt");
        if(altsFile.exists()){
            System.out.println("[FLAUXY DEBUG] Found alts file containing at least one alt");
            try{
                List<String> alts = Files.readAllLines(altsFile.toPath());
                for(String alt : alts){
                    String mail = alt.split(":", 2)[0];
                    String pass = alt.split(":", 2)[1];
                    AltManager.registry.add(new Alt(mail, pass));
                }
                if(!Objects.isNull(alts.get(0))){
                    String alt = alts.get(0);
                    String mail = alt.split(":", 2)[0];
                    String pass = alt.split(":", 2)[1];
                    AltManager.lastAlt = new Alt(mail, pass);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        capeManager = new CapeManager();
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

    public java.util.ArrayList<Changelog> getLogs(){
        java.util.ArrayList<Changelog> list = new java.util.ArrayList<>();
        list.add(new Changelog("1.1", Changelog.Type.TITLE));
        list.add(new Changelog("Added FastDrop", Changelog.Type.ADDED));
        list.add(new Changelog("Added HotbarScroller", Changelog.Type.ADDED));
        list.add(new Changelog("Added AutoDismount", Changelog.Type.ADDED));
        list.add(new Changelog("Fully fixed autoheadhitter", Changelog.Type.EDITED));
        list.add(new Changelog("Fixed delta rotation", Changelog.Type.EDITED));
        list.add(new Changelog("1.0", Changelog.Type.TITLE));
        list.add(new Changelog("Added .sens command", Changelog.Type.ADDED));
        list.add(new Changelog("Added Twitch Optimization", Changelog.Type.ADDED));
        list.add(new Changelog("Added Packet mode to Criticals", Changelog.Type.ADDED));
        list.add(new Changelog("Removed TargetStrafe due to bugs", Changelog.Type.REMOVED));
        list.add(new Changelog("Added Log4J exploit patcher", Changelog.Type.ADDED));
        list.add(new Changelog("Added resource pack exploit patcher", Changelog.Type.ADDED));
        list.add(new Changelog("Added pointed entity display", Changelog.Type.ADDED));
        list.add(new Changelog("Added display yaw option to HUD", Changelog.Type.ADDED));
        list.add(new Changelog("Fixed ESP color glitch", Changelog.Type.EDITED));
        list.add(new Changelog("Fixed capes not showing if already have official cape", Changelog.Type.EDITED));
        return list;
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
        if(mc.thePlayer == null && (e instanceof EventReceivePacket)){
            return;
        }
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


    public boolean isGhost() {
        return Flauxy.INSTANCE.getCommandManager().getCommand(CommandGhost.class).getSafeMode();
    }
    public void setGhost(boolean g){
        Flauxy.INSTANCE.getCommandManager().getCommand(CommandGhost.class).setGhostmode(g);
    }
}