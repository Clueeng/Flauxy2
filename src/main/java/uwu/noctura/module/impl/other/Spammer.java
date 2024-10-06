package uwu.noctura.module.impl.other;

import uwu.noctura.Noctura;
import uwu.noctura.commands.impl.CommandSpammer;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;

@ModuleInfo(name = "Spammer", displayName = "Spammer", cat = Category.Other, key = -1)
public class Spammer extends Module {

    public NumberSetting ticks = new NumberSetting("Tick delay",1,0,50,1);

    public Spammer(){
        addSettings(ticks);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            if(mc.thePlayer.ticksExisted < 80){
                if(mc.thePlayer.ticksExisted < 1){
                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Spammer", "Disabled spammer due to new world"));
                }
                return;
            }
            if(CommandSpammer.spammerCommand != null){
                String cmd = CommandSpammer.spammerCommand;
                if(cmd.isEmpty())return;
                if(mc.thePlayer.ticksExisted % ticks.getValue() == 0){
                    mc.thePlayer.sendChatMessage(cmd);
                }
            }
        }
    }
}
