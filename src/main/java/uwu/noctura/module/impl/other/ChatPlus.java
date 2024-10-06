package uwu.noctura.module.impl.other;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;

@ModuleInfo(name = "ChatPlus", key = -1, cat = Category.Other, displayName = "ChatPlus")
public class ChatPlus extends Module {

    public BooleanSetting infiniteChat = new BooleanSetting("Infinite Chat",true);
    public BooleanSetting pingUser = new BooleanSetting("Ping on mention",true);
    public BooleanSetting smoothChat = new BooleanSetting("Smooth Scroll",true);
    public BooleanSetting stackMessage = new BooleanSetting("Stack Messages",true);

    public ChatPlus(){
        addSettings(infiniteChat, pingUser, stackMessage);
    }

    int tickToNotPing;
    boolean ping;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate) e;
            if(ev.getType().equals(EventType.PRE)){
                tickToNotPing = Math.max(0,tickToNotPing - 1);
                if(ping){
                    mc.thePlayer.playSound("random.orb", 1.5f, 1.0f);
                    ping = false;
                }
            }
        }
        if(e instanceof EventSendPacket){
            EventSendPacket event = (EventSendPacket) e;
            if(event.getPacket() instanceof C01PacketChatMessage){
                C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
                tickToNotPing = 4;
            }
        }
        if(e instanceof EventReceivePacket){
            EventReceivePacket event = (EventReceivePacket) e;
            if(event.getPacket() instanceof S02PacketChat){
                S02PacketChat chat = (S02PacketChat) event.getPacket();
                if(chat.getChatComponent().getUnformattedText().toLowerCase().contains(mc.session.getUsername().toLowerCase()) && pingUser.getValue() && tickToNotPing <= 0){
                    ping = true;
                }
            }
        }
    }
}
