package uwu.noctura.module.impl.visuals;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.event.impl.EventTime;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "TimeChanger", displayName = "Time Changer", key = -1, cat = Category.Visuals)
public class TimeChanger extends Module {

    public static NumberSetting time = new NumberSetting("Time", 19000, 0, 24000, 250);
    public BooleanSetting removeLimit = new BooleanSetting("Unlimit CustomSky", true);
    public TimeChanger(){
        addSettings(time, removeLimit);
    }

    @Override
    public void onEvent(Event e){
        if(e instanceof EventReceivePacket){
            EventReceivePacket event = (EventReceivePacket) e;
            if(event.getPacket() instanceof S03PacketTimeUpdate){
                event.setCancelled(true);
            }
        }
        if(e instanceof EventUpdate){
            setWorldTime((long)time.getValue());
        }
        if(e instanceof EventTime){
            EventTime et = (EventTime) e;
            et.setTime((int) time.getValue());
            //mc.theWorld.setWorldTime(et.getTime());
        }
    }

    public void setWorldTime(long time){
        if(mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.ticksExisted < 2) return;
        mc.theWorld.setWorldTime(time);
        mc.thePlayer.worldObj.setWorldTime(time);
    }

}
