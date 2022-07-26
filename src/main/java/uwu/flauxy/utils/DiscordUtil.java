package uwu.flauxy.utils;

import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import uwu.flauxy.DiscordRP;
import uwu.flauxy.Flauxy;

public class DiscordUtil{

    public static String getDiscordName(){
        return Flauxy.INSTANCE.getDiscordRP().currentUser == null ? "" : Flauxy.INSTANCE.getDiscordRP().currentUser.username;
    }
    public static String getDiscordHash(){
        return Flauxy.INSTANCE.getDiscordRP().currentUser == null ? "" : Flauxy.INSTANCE.getDiscordRP().currentUser.discriminator;
    }
    public static DiscordUser getUser(){
        return Flauxy.INSTANCE.getDiscordRP().currentUser;
    }

}
