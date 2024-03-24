package uwu.flauxy.module.impl.other;

import net.minecraft.network.play.server.S02PacketChat;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "AutoRegister", displayName = "Auto Register", cat = Category.Other, key = -1)
public class AutoRegister extends Module {
    public static String password = "Flauxy2024!";
    @Override
    public void onEnable() {
        Wrapper.instance.log("You can set a password using .password <pass>. Default password is Flauxy2024!");
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventReceivePacket){
            EventReceivePacket eventReceivePacket = (EventReceivePacket) e;
            if(eventReceivePacket.getPacket() instanceof S02PacketChat){
                S02PacketChat packet = (S02PacketChat) eventReceivePacket.getPacket();
                String chat = packet.getChatComponent().getUnformattedText();
                String cmd = "/register";

                if(chat.contains("register")){
                    mc.thePlayer.sendChatMessage(cmd + " " + password + " " + password);
                    Wrapper.instance.log("Registered as " + mc.thePlayer.getName() + " using your password");
                }
                if(chat.contains("login")){
                    cmd = "/login";
                    mc.thePlayer.sendChatMessage(cmd + " " + password);
                    Wrapper.instance.log("Logged in as " + mc.thePlayer.getName() + " using your password");
                }
            }
        }
    }
}
