package uwu.noctura.utils;

import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class DiscordPresenceUtil {
    public static void setPresence(String line1, String line2, boolean showTime) {
        DiscordRPC discordRPC = DiscordRPC.INSTANCE;
        DiscordRichPresence presence = new DiscordRichPresence();
        if (showTime){
            presence.startTimestamp = System.currentTimeMillis() / 1000;
        }
        presence.largeImageKey = "image1";
        presence.largeImageText = "Noctura";
        presence.details = line1;
        presence.state = line2;
        discordRPC.Discord_UpdatePresence(presence);
    }

}
