package uwu.flauxy.module.impl.other;

import uwu.flauxy.commands.impl.CommandSpammer;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Spammer", displayName = "Spammer", cat = Category.Other, key = -1)
public class Spammer extends Module {

    public NumberSetting ticks = new NumberSetting("Tick delay",1,0,50,1);

    public Spammer(){
        addSettings(ticks);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
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
