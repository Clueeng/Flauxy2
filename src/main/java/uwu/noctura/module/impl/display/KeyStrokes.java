package uwu.noctura.module.impl.display;

import org.lwjgl.input.Mouse;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.display.keystrokes.Keystroke;
import uwu.noctura.module.impl.ghost.AutoClicker;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "KeyStrokes", displayName = "Key Strokes", key = -1, cat = Category.Display)
public class KeyStrokes extends Module {

    java.util.ArrayList<Keystroke> keystrokes = new java.util.ArrayList<>();
    public NumberSetting size = new NumberSetting("Size", 1.25,0.75,2,0.0125);
    public NumberSetting hue = new NumberSetting("HUE",0,0,360,1);
    public GraphSetting satBright = new GraphSetting("Saturation", 100,100,0,100,0,100,1,1, hue);
    java.util.ArrayList<Long> cpsTimeLeft = new java.util.ArrayList<>();
    java.util.ArrayList<Long> cpsTimeRight = new java.util.ArrayList<>();
    int cpsLeft, cpsRight;
    boolean leftHeld, leftClicked, rightHeld, rightClicked;

    public KeyStrokes(){
        keystrokes.add(new Keystroke(mc.gameSettings.keyBindForward,0,0,0));
        keystrokes.add(new Keystroke(mc.gameSettings.keyBindLeft,0,-16,16));
        keystrokes.add(new Keystroke(mc.gameSettings.keyBindBack,0,0,16));
        keystrokes.add(new Keystroke(mc.gameSettings.keyBindRight,0,16,16));
        keystrokes.add(new Keystroke(mc.gameSettings.keyBindAttack,0,-16,32)); // 24 w
        keystrokes.add(new Keystroke(mc.gameSettings.keyBindUseItem,0,8,32)); // 24 w
        setHudMoveable(true);
        hue.setColorDisplay(true);
        satBright.setColorDisplay(true);
        addSettings(size, hue, satBright);
        moveX = 100;
        moveY = 100;
    }

    @Override
    public void onEvent(Event e) {
        AutoClicker ac = Noctura.INSTANCE.getModuleManager().getModule(AutoClicker.class);
        if(e instanceof EventRender2D){
            int color = Noctura.INSTANCE.moduleManager.getModule(ArrayList.class).stringColor;
            int index = 0;
            for(Keystroke k : keystrokes){
                k.setSize((float) size.getValue());
                k.updatePressed();
                if(k.keyBinding.equals(mc.gameSettings.keyBindUseItem) || k.keyBinding.equals(mc.gameSettings.keyBindAttack)){
                    k.renderClicks(moveX, moveY, cpsLeft, cpsRight, getColorFromSettings(hue, satBright).getRGB());
                }else{
                    k.render(moveX,moveY, getColorFromSettings(hue, satBright).getRGB());
                }
            }

            // left
            leftHeld = Mouse.isButtonDown(0);
            if((leftHeld && !leftClicked) || ac.isClickingLeft){
                cpsTimeLeft.add(System.currentTimeMillis());
                leftClicked = true;
            }
            if(!leftHeld){
                leftClicked = false;
            }
            // Right
            rightHeld = Mouse.isButtonDown(1);
            if(rightHeld && !rightClicked || ac.isClickingRight){
                cpsTimeRight.add(System.currentTimeMillis());
                rightClicked = true;
            }
            if(!rightHeld){
                rightClicked = false;
            }
            cpsLeft = cpsTimeLeft.size();
            cpsRight = cpsTimeRight.size();
            cpsTimeLeft.removeIf(l -> Math.abs(l - System.currentTimeMillis()) > 1000);
            cpsTimeRight.removeIf(l -> Math.abs(l - System.currentTimeMillis()) > 1000);
        }
        if(e instanceof EventUpdate){


            for(Keystroke k : keystrokes){
                if(k.pressed()){
                    k.whiten(255);
                }else{
                    k.whiten(0);
                }
            }
        }
    }
}
