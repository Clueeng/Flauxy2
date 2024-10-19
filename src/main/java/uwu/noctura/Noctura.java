package uwu.noctura;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
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
import uwu.noctura.module.impl.other.Killsults;
import uwu.noctura.module.impl.player.Noslow;
import uwu.noctura.module.impl.player.Sprint;
import uwu.noctura.module.impl.visuals.Animations;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationManager;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.MinecraftInstance;
import uwu.noctura.utils.config.ConfigManager;
import uwu.noctura.utils.config.ConfigUtil;
import uwu.noctura.utils.config.Folder;
import uwu.noctura.utils.config.KeyLoader;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.waypoint.WaypointManager;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@Getter
public enum Noctura implements MinecraftInstance {
    INSTANCE;

    public UserConnection userConnection;
    public String name = "Noctura", version = "1.5";
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
    public boolean isUpToDate;
    @Getter
    public String currentVer, remoteVer;
    @Getter
    public DiscordRPC discordRPC;
    @Getter
    public boolean initialized = false;
    @Getter
    private DiscordRP discordRP;

    public Color copiedColor = new Color(0, 0, 0);

    public void init(){
        discordRP = new DiscordRP();
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
        System.out.println("[Noctura] Starting discord rp");
        discordRP.init();
        nonShittyConfigManager.init();
        loadHudPosition();
        initialized = true;
        isUpToDate = upToDate();
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
//        list.add(new Changelog("1.3", Changelog.Type.TITLE));
//        list.add(new Changelog("Fixed killaura", Changelog.Type.ADDED));
//        list.add(new Changelog("Renamed to Noctura", Changelog.Type.ADDED));
//        list.add(new Changelog("Hypixel Ground Strafe", Changelog.Type.ADDED));
//        list.add(new Changelog("Hypixel Velocity", Changelog.Type.ADDED));
//        list.add(new Changelog("Telly Scaffold", Changelog.Type.ADDED));
//        list.add(new Changelog("Reworked Name Randomness", Changelog.Type.EDITED));
//        list.add(new Changelog("Changed Main menu", Changelog.Type.ADDED));
        return list;
    }

    public void initDiscordApp(){
        String applicationID = "1292931247190052914";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        discordRPC.Discord_Initialize(applicationID, handlers, false, ""); // empty string is steam id
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.largeImageKey = "large";
        presence.largeImageText = "Noctura";
        presence.details = "Loading Client";
        presence.state = "V" + version;
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

    public static String oldIP;

    public void onShutDownApplet(){
        discordRP.close();
        waypointManager.saveWaypoints();
        getModuleManager().saveHudPosition();
        Noctura.INSTANCE.getConfigManager().save("Default");
        KeyLoader.save(ModuleManager.modules);
    }


    public boolean isGhost() {
        return Noctura.INSTANCE.getCommandManager().getCommand(CommandGhost.class).getSafeMode();
    }
    public void setGhost(boolean g){
        Noctura.INSTANCE.getCommandManager().getCommand(CommandGhost.class).setGhostmode(g);
    }

    public void cycleSave(){
        getConfigManager().save(getConfigManager().getCurrentloadedConfig);
        getConfigManager().save("Default");
        getModuleManager().saveHudPosition();
        KeyLoader.save(Arrays.asList(getModuleManager().getModules()));
        getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Auto-Save", "Saved Settings"));
    }

    private boolean upToDate() {
        System.out.println("Checking for client vertsion");
        File nocturaFolder = new File(Minecraft.getMinecraft().mcDataDir, "Noctura");
        if (!nocturaFolder.exists() || !nocturaFolder.isDirectory()) {
            nocturaFolder.mkdirs();
        }
        File localFile = new File(nocturaFolder, "latest.txt");
        String remoteFileUrl = "https://raw.githubusercontent.com/Clueeng/flauxy-files/main/latest.txt";
        String remoteVersion = "";
        try {
            URL url = new URL(remoteFileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                remoteVersion = reader.readLine();
                System.out.println("Fetched " + remoteVersion + " as remote version");
                reader.close();
            } else {
                System.out.println("oops " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!localFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(localFile))) {
                writer.write(remoteVersion);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }
        String localVersion = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(localFile))) {
            localVersion = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Local Version: " + localVersion);
        currentVer = localVersion;
        System.out.println("Remote Version: " + remoteVersion);
        this.remoteVer = remoteVersion;
        if (!Objects.equals(localVersion, remoteVersion)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(localFile))) {
                writer.write(remoteVersion);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        return true;
    }
}