package uwu.flauxy.module.impl.falses;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.notification.Notification;
import uwu.flauxy.notification.NotificationType;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "ConsumeDesync", key = -1, cat = Category.False, displayName = "Consume Desync")
public class ConsumeDesync extends Module {

    int consumeTick = 0;
    boolean eaten;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate)e;
            if(ev.isPre())return;
            if(mc.thePlayer.isEating()){
                consumeTick ++;
            }else{
                consumeTick = 0;
            }
            if(consumeTick >= 31){
                eaten = true;
            }
            if(eaten){
                mc.thePlayer.inventory.currentItem += 1;
                consumeTick = 0;
                eaten = false;
                Flauxy.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, getDisplayName(), "Disabled the module, you are desynced until you finish drinking serverside", 5000));
                this.toggle();
            }
        }
    }
}
