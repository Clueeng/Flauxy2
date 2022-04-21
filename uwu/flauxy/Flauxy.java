package uwu.flauxy;

import com.darkmagician6.eventapi.EventManager;
import lombok.Getter;
import org.lwjgl.opengl.Display;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.impl.visuals.HUD;
import uwu.flauxy.utils.font.FontManager;

@Getter
public class Flauxy {

    public String name = "Flauxy", version = "1.0";
    public static Flauxy instance = new Flauxy();
    public ModuleManager mgr;
    public EventManager em;
    public FontManager fmgr;
    public void init(){
        // inits shit
        em = new EventManager();
        mgr = new ModuleManager();
        fmgr = new FontManager();
        Display.setTitle(name + " - " + version);
        mgr.getModule(HUD.class).toggle();
        EventManager.register(this);
    }

}
