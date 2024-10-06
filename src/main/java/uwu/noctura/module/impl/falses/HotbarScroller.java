package uwu.noctura.module.impl.falses;

import uwu.noctura.event.Event;
import uwu.noctura.event.EventType;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.timer.Timer;

import java.util.Random;

@ModuleInfo(name = "HotbarScroller", displayName = "Hotbar Scroller", cat = Category.False, key = -1)
public class HotbarScroller extends Module {

    int slot = 0;
    private Timer timer = new Timer();
    NumberSetting ms = new NumberSetting("Delay",10,5,1000,5);
    BooleanSetting randomSlot = new BooleanSetting("Random slot",true);
    BooleanSetting rightToLeft = new BooleanSetting("Right To Left",true).setCanShow(s -> !randomSlot.isEnabled());
    NumberSetting randomDelay = new NumberSetting("Delay Randomness",0,0,100,5);
    BooleanSetting specificSlot = new BooleanSetting("Specific slot",true);
    NumberSetting specificSlotIndex = new NumberSetting("Slot",1,1,9,1).setCanShow(s -> specificSlot.isEnabled());
    public HotbarScroller(){
        addSettings(ms, randomDelay, randomSlot, rightToLeft, specificSlot, specificSlotIndex);
    }

    @Override
    public void onEnable() {
        slot = 0;
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            EventUpdate ev = (EventUpdate)e;
            if(ev.getType().equals(EventType.POST))return;
            Random r = new Random();
            int delay = (int) (ms.getValue() + (r.nextDouble() * randomDelay.getValue()));
            if(specificSlot.isEnabled()){
                if(timer.hasTimeElapsed(delay, true)){
                    mc.thePlayer.inventory.currentItem = (int) specificSlotIndex.getValue()-1;
                }
            }else{
                if(timer.hasTimeElapsed(delay, true)){
                    if(randomSlot.getValue()){
                        Random other = new Random();
                        double value = other.nextDouble() * 8;
                        slot = (int) Math.floor(value);
                    }else{
                        if(rightToLeft.isEnabled()){
                            slot -= 1;
                            if(slot < 0){
                                slot = 8;
                            }
                        }else{
                            slot += 1;
                            if(slot > 8){
                                slot = 0;
                            }
                        }
                    }
                    mc.thePlayer.inventory.currentItem = slot;
                }
            }
        }
    }
}
