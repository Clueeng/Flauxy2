package uwu.flauxy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Wrapper {

    public static Wrapper instance = new Wrapper();
    public void log(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§dFlauxy §7| §f" + message));
    }
}
