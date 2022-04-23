package uwu.flauxy.module.impl.visuals;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "TimeChanger", displayName = "Time Changer", key = -1, cat = Category.Visuals)
public class TimeChanger extends Module {

    public static NumberSetting time = new NumberSetting("Time", 19000, 0, 24000, 250);
    public TimeChanger(){
        addSettings(time);
    }

    @Override
    public void onEvent(Event e){
        if(e instanceof EventReceivePacket){
            EventReceivePacket event = (EventReceivePacket) e;
            if(event.getPacket() instanceof S03PacketTimeUpdate){
                S03PacketTimeUpdate s03 = (S03PacketTimeUpdate) event.getPacket();
                setWorldTime((long)time.getValue());
            }
        }
    }

    public void setWorldTime(long time){
        mc.theWorld.setWorldTime(time);
        mc.thePlayer.worldObj.setWorldTime(time);
    }

}
