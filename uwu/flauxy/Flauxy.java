package uwu.flauxy;

import lombok.Getter;
import org.lwjgl.opengl.Display;
import uwu.flauxy.alts.AltManager;
import uwu.flauxy.event.Event;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.display.ArrayList;
import uwu.flauxy.module.impl.display.HUD;
import uwu.flauxy.module.impl.player.Noslow;
import uwu.flauxy.module.impl.player.Sprint;
import uwu.flauxy.module.impl.visuals.Animations;
import uwu.flauxy.utils.font.FontManager;

@Getter
public enum Flauxy {
    INSTANCE;

    public String name = "Flauxy", version = "1.0";
    public ModuleManager moduleManager;
    public FontManager fontManager;
    public AltManager altManager;
    public void init(){
        // inits shit
        moduleManager = new ModuleManager();
        fontManager = new FontManager();
        altManager = new AltManager();
        Display.setTitle(name + " - " + version);
        moduleManager.getModule(HUD.class).toggle();
        moduleManager.getModule(ArrayList.class).toggle();
        moduleManager.getModule(Noslow.class).toggle();
        moduleManager.getModule(Sprint.class).toggle();
        moduleManager.getModule(Animations.class).toggle();
        AutoBind.setKeyBinds(AutoBind.dev.Flaily);
    }

    public static void onEvent(Event e){
        for(Module m : ModuleManager.modules){
            if(m.isToggled()) m.onEvent(e);
        }
    }

    public static void onEventIgnore(Event e){
        for(Module m : ModuleManager.modules){
            m.onEventIgnore(e);
        }
    }


}