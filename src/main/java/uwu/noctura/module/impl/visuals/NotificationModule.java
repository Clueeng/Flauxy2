package uwu.noctura.module.impl.visuals;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.NotificationType;

import java.awt.*;

@ModuleInfo(name = "Notification", displayName = "Notification", cat = Category.Display, key = -1)
public class NotificationModule extends Module {

    NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    GraphSetting saturation = new GraphSetting("Saturation",0,0,0,100,0,100,1,1, hue);
    public BooleanSetting outdatedNotification = new BooleanSetting("Show Outdated", false);
    public NotificationModule(){
        hue.setColorDisplay(true);
        saturation.setColorDisplay(true);
        addSettings(hue, saturation, outdatedNotification);
    }

    public static Color notifColor = NotificationType.INFO.getColor();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            notifColor = getColorFromSettings(hue,saturation);
        }
    }
}
