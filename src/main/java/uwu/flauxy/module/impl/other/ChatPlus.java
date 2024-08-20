package uwu.flauxy.module.impl.other;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventSendPacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.utils.Wrapper;

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
