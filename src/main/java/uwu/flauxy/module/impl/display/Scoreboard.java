package uwu.flauxy.module.impl.display;

import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;

@ModuleInfo(name = "Scoreboard", displayName = "Scoreboard", key = -1, cat = Category.Display)
public class Scoreboard extends Module {

    public float scoreBoardWidth = 120, scoreboardHeight = 120;

    public BooleanSetting showScore = new BooleanSetting("Show Score",true);

    public Scoreboard(){
        setHudMoveable(true);
        setMoveX(0);
        setMoveY(0);
        addSettings(showScore);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRender2D){
            //setMoveY(0);
            //setMoveW(100);
            //setMoveH(scoreboardHeight);
        }
    }
}
