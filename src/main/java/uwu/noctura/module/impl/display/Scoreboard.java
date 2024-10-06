package uwu.noctura.module.impl.display;

import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;

@ModuleInfo(name = "Scoreboard", displayName = "Scoreboard", key = -1, cat = Category.Display)
public class Scoreboard extends Module {

    public float scoreBoardWidth = 120, scoreboardHeight = 120;

    public BooleanSetting showScore = new BooleanSetting("Show Score",true);
    public BooleanSetting blur = new BooleanSetting("Blur",true);

    public Scoreboard(){
        setHudMoveable(true);
        setMoveX(0);
        setMoveY(0);
        addSettings(showScore, blur);
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
