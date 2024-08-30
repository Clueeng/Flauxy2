package uwu.flauxy.module.impl.visuals;

import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.GraphSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.notification.Notification;
import uwu.flauxy.notification.NotificationType;

import java.awt.*;

@ModuleInfo(name = "Notification", displayName = "Notification", cat = Category.Display, key = -1)
public class NotificationModule extends Module {

    NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    GraphSetting saturation = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue);

    public NotificationModule(){
        hue.setColorDisplay(true);
        saturation.setColorDisplay(true);
        addSettings(hue, saturation);
    }

    public static Color notifColor = NotificationType.INFO.getColor();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            notifColor = getColorFromSettings(hue,saturation);
        }
    }
}
