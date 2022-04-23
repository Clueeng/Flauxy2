package uwu.flauxy.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.utils.PacketUtil;

@ModuleInfo(name = "Nofall", displayName = "No Fall", key = -1, cat = Category.Player)
public class Nofall extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "On Ground");

    public Nofall(){
        addSettings(mode);
    }

    @Override
    public void onEvent(Event ev){
        if(ev instanceof EventMotion){
            EventMotion event = (EventMotion)ev;
            switch(mode.getMode()){
                case "Packet":{
                    if(mc.thePlayer.fallDistance > 2.5f) PacketUtil.packetNoEvent(new C03PacketPlayer(true));
                    break;
                }
                case "On Ground":{
                    event.onGround = true;
                    break;
                }
            }
        }
    }

}
