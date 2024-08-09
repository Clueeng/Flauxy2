package uwu.flauxy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Wrapper {

    public static Wrapper instance = new Wrapper();
    public void log(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "Flauxy" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE + message));
    }
}
