package uwu.noctura.module.impl.falses;

import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.Wrapper;

@ModuleInfo(name = "SpamDrop", displayName = "Spam Drop", key = -1, cat = Category.False)
public class SpamDrop extends Module {
    // spam drop, spam block
    // hold key with Keyboard. and get the key from gamesettings

    private NumberSetting timing = new NumberSetting("Timing (tick)", 5,1,20,1);
    private BooleanSetting onSwingDrop = new BooleanSetting("On Swing",true);
    public SpamDrop(){
        addSettings(timing);
    }

    @Override
    public void onEnable() {
        Wrapper.instance.log("Usage: Hold the drop key to continuously send drop item packets");
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            EventUpdate ev = (EventUpdate)e;
            if(ev.getType().equals(EventType.POST))return;
            if(mc.thePlayer.ticksExisted % timing.getValue() == 0){
                if(mc.gameSettings.keyBindDrop.getKeyCode() < 0){
                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "SpamDrop", "Drop key not a valid key"));
                    this.toggle();
                    return;
                }
                if(Keyboard.isKeyDown(mc.gameSettings.keyBindDrop.getKeyCode())){
                    mc.thePlayer.dropOneItem(false);
                }
            }
            if(mc.gameSettings.keyBindAttack.pressed){
                mc.thePlayer.dropOneItem(false);
            }
        }
    }
}
