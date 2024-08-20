package uwu.flauxy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Wrapper {

    public static Wrapper instance = new Wrapper();
    public void log(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GRAY + "[" + EnumChatFormatting.LIGHT_PURPLE + "Flauxy" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.WHITE + message));
    }
    public void logHover(String mess, String hover){
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hover));
        ChatComponentText chat = new ChatComponentText(mess);
        chat.getChatStyle().setChatHoverEvent(hoverEvent);
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
    }
}
