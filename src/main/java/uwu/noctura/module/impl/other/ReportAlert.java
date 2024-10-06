package uwu.noctura.module.impl.other;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.Wrapper;

import java.util.ArrayList;

@ModuleInfo(name = "ReportAlert", displayName = "ReportAlert", key = -1, cat = Category.Other)
public class ReportAlert extends Module {

    public static ArrayList<String> hackers = new ArrayList<>();
    public String lastKiller = "";

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventSendPacket){
            EventSendPacket ev = (EventSendPacket) e;
            if(ev.getPacket() instanceof C01PacketChatMessage){
                C01PacketChatMessage chat = (C01PacketChatMessage) ev.getPacket();
                String msg = chat.getMessage();
                if(msg.startsWith("/report " + lastKiller)){
                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Report Alert", "Reported " + lastKiller + " for cheating"));
                    hackers.add(lastKiller);
                }
            }
        }
        if(e instanceof EventReceivePacket){
            EventReceivePacket ev = (EventReceivePacket) e;
            Packet p = ev.getPacket();
            if(p instanceof S02PacketChat){
                S02PacketChat chat = (S02PacketChat) p;
                String message = chat.getChatComponent().getUnformattedText();
                if(message.startsWith("Cages opened! FIGHT!")){
                    for(NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()){
                        String playerName = networkPlayerInfo.getGameProfile().getName();
                        if(hackers.contains(playerName)){
                            Wrapper.instance.log(playerName + " is a known cheater, leave if you want to avoid");
                        }
                    }
                }
                if(message.startsWith(mc.session.getUsername() + " was killed by")){
                    String keyword = "killed by ";
                    int index = message.indexOf(keyword);
                    String killer = message.substring(index + keyword.length());

                    ChatComponentText reportComp = new ChatComponentText(EnumChatFormatting.DARK_PURPLE
                            + "[" + EnumChatFormatting.LIGHT_PURPLE + "Noctura" + EnumChatFormatting.DARK_PURPLE + "]" + EnumChatFormatting.WHITE
                            + " You were killed by " + EnumChatFormatting.RED + killer + EnumChatFormatting.WHITE + ", click here to report him");

                    ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + killer + " hacking");
                    ChatComponentText hoverText = new ChatComponentText("Clicking this message will report the player");
                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
                    ChatStyle chatStyle = new ChatStyle();
                    lastKiller = killer;
                    chatStyle.setChatClickEvent(clickEvent);
                    chatStyle.setChatHoverEvent(hoverEvent);
                    reportComp.setChatStyle(chatStyle);
                    mc.thePlayer.addChatComponentMessage(reportComp);
                }
            }
        }
    }
}
