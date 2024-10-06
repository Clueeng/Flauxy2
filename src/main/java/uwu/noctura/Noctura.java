package uwu.noctura;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import org.lwjgl.opengl.Display;
import uwu.noctura.alts.AltManager;
import uwu.noctura.cape.CapeManager;
import uwu.noctura.commands.CommandManager;
import uwu.noctura.commands.impl.CommandGhost;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleManager;
import uwu.noctura.module.impl.display.ArrayList;
import uwu.noctura.module.impl.display.HUD;
import uwu.noctura.module.impl.player.Noslow;
import uwu.noctura.module.impl.player.Sprint;
import uwu.noctura.module.impl.visuals.Animations;
import uwu.noctura.notification.NotificationManager;
import uwu.noctura.utils.MinecraftInstance;
import uwu.noctura.utils.config.ConfigManager;
import uwu.noctura.utils.config.ConfigUtil;
import uwu.noctura.utils.config.Folder;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.waypoint.WaypointManager;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public enum Noctura implements MinecraftInstance {
    INSTANCE;

    public String name = "Noctura", version = "1.4 (New Name Edition)";
    public Path clientDirectory = Paths.get(mc.mcDataDir.getAbsolutePath(), name), configsDirectory = Paths.get(clientDirectory.toFile().getAbsolutePath(), "configs");
    public ModuleManager moduleManager;
    public FontManager fontManager;
    public AltManager altManager;
    public final NotificationManager notificationManager = new NotificationManager();
    private ConfigUtil configManager;
    private CapeManager capeManager;
    private CommandManager commandManager;
    public WaypointManager waypointManager;
    private final ConfigManager nonShittyConfigManager = new ConfigManager();
    @Getter
    public DiscordRPC discordRPC;
    @Getter
    public boolean initialized = false;
    @Getter
    private DiscordRP discordRP = new DiscordRP();

    public Color copiedColor = new Color(0, 0, 0);

    public void init(){
        discordRPC = DiscordRPC.INSTANCE;
        initDiscordApp();
        try
        {
            ViaMCP.create();
            //ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
            ViaMCP.INSTANCE.initAsyncSlider(4, 4, 120, 20); // For custom position and size slider
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // inits shit
        Folder.init();
        uwu.noctura.module.impl.other.util.Folder.init(); // for killsults
        moduleManager = new ModuleManager();
        configManager = new ConfigUtil();
        commandManager = new CommandManager();
        waypointManager = new WaypointManager();
        setGhost(false);
        fontManager = new FontManager();
        // Trying to add before altmanager i guess
        altManager = new AltManager();
        altManager.loadAlts();
        capeManager = new CapeManager();
        Display.setTitle("Noctura [1.8.8] V" + version);
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
        loadHudPosition();
        initialized = true;
    }

    public void loadHudPosition(){
        for(Module hudMod : getModuleManager().getHudModules()){
            int[] pos = getModuleManager().load(hudMod.getName());
            if(pos != null){
                hudMod.setMoveX(pos[0]);

                hudMod.setMoveY(pos[1]);
                System.out.println("Loaded " + hudMod.getDisplayName() + " at " + pos[0] + " " + pos[1]);
            }else{
                System.out.println("No position for " + hudMod.getDisplayName() + ", returning default");
            }
        }
    }

    public java.util.ArrayList<Changelog> getLogs(){
        java.util.ArrayList<Changelog> list = new java.util.ArrayList<>();
        list.add(new Changelog("1.3", Changelog.Type.TITLE));
        list.add(new Changelog("Fixed killaura", Changelog.Type.ADDED));
        list.add(new Changelog("Renamed to Noctura", Changelog.Type.ADDED));
        list.add(new Changelog("Hypixel Ground Strafe", Changelog.Type.ADDED));
        list.add(new Changelog("Hypixel Velocity", Changelog.Type.ADDED));
        list.add(new Changelog("Telly Scaffold", Changelog.Type.ADDED));
        list.add(new Changelog("Reworked Name Randomness", Changelog.Type.EDITED));
        list.add(new Changelog("Changed Main menu", Changelog.Type.ADDED));
        return list;
    }

    public void initDiscordApp(){
        String applicationID = "972902027300581406";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        discordRPC.Discord_Initialize(applicationID, handlers, false, ""); // empty string is steam id
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.largeImageKey = "image1";
        presence.largeImageText = "Noctura";
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

    public static String oldIP;

    public void onShutDownApplet(){
        discordRP.close();
        waypointManager.saveWaypoints();

        // here we're gonna try to save all the modules that aare moveable in the gui
        getModuleManager().saveHudPosition();
    }


    public boolean isGhost() {
        return Noctura.INSTANCE.getCommandManager().getCommand(CommandGhost.class).getSafeMode();
    }
    public void setGhost(boolean g){
        Noctura.INSTANCE.getCommandManager().getCommand(CommandGhost.class).setGhostmode(g);
    }
}