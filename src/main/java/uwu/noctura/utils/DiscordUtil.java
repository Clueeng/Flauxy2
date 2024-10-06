package uwu.noctura.utils;

import net.arikia.dev.drpc.DiscordUser;
import uwu.noctura.Noctura;

public class DiscordUtil{

    public static String getDiscordName(){
        return Noctura.INSTANCE.getDiscordRP().currentUser == null ? "" : Noctura.INSTANCE.getDiscordRP().currentUser.username;
    }
    public static String getDiscordHash(){
        return Noctura.INSTANCE.getDiscordRP().currentUser == null ? "" : Noctura.INSTANCE.getDiscordRP().currentUser.discriminator;
    }
    public static DiscordUser getUser(){
        return Noctura.INSTANCE.getDiscordRP().currentUser;
    }

}
