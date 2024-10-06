package uwu.noctura.module.impl.other;

import net.minecraft.network.play.server.S02PacketChat;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.utils.Wrapper;

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
