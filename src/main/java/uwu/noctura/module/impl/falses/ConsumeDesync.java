package uwu.noctura.module.impl.falses;

import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;

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
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, getDisplayName(), "Disabled the module, you are desynced until you finish drinking serverside", 5000));
                this.toggle();
            }
        }
    }
}
