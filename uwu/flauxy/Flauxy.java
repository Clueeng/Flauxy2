package uwu.flauxy;

import com.darkmagician6.eventapi.EventManager;
import lombok.Getter;
import org.lwjgl.opengl.Display;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.display.HUD;
import uwu.flauxy.utils.font.FontManager;

@Getter
public enum Flauxy {
    INSTANCE;

    public String name = "Flauxy", version = "1.0";
    public ModuleManager moduleManager;
    public EventManager eventManager;
    public FontManager fontManager;
    public void init(){
        // inits shit
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        fontManager = new FontManager();
        Display.setTitle(name + " - " + version);
        moduleManager.getModule(HUD.class).toggle();
        EventManager.register(this);
    }

}
