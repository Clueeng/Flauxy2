package uwu.flauxy;

import com.darkmagician6.eventapi.EventManager;
import lombok.Getter;
import org.lwjgl.opengl.Display;
import uwu.flauxy.alts.AltManager;
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
    public EventManager eventManager;
    public FontManager fontManager;
    public AltManager altManager;
    public void init(){
        // inits shit
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        fontManager = new FontManager();
        altManager = new AltManager();
        Display.setTitle(name + " - " + version);
        moduleManager.getModule(HUD.class).toggle();
        moduleManager.getModule(ArrayList.class).toggle();
        moduleManager.getModule(Noslow.class).toggle();
        moduleManager.getModule(Sprint.class).toggle();
        moduleManager.getModule(Animations.class).toggle();
        EventManager.register(this);
    }

}
