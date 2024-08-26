package uwu.flauxy.module.impl.display;

import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventUI;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.ui.discord.Discord;
import uwu.flauxy.ui.flat.FlatUi;

@ModuleInfo(name = "ClickGUI", displayName = "ClickGui", key = Keyboard.KEY_RSHIFT, cat = Category.Display)
public class ClickGUI extends Module {

    uwu.flauxy.ui.dropdown.ClickGUI clickgui;
    uwu.flauxy.ui.astolfo.ClickGUI clickguiAs;
    Discord discordUi;
    FlatUi flatUi;
    public ModeSetting mode = new ModeSetting("Mode", "Dropdown", "Dropdown", "Flat", "Astolfo");
    public static NumberSetting red = new NumberSetting("R", 180, 1, 250, 1);
    public static NumberSetting green = new NumberSetting("G", 10, 1, 250, 1);
    public static NumberSetting blue = new NumberSetting("B", 120, 1, 250, 1);
    public ClickGUI() {
        addSettings(mode, red, green, blue);
    }

    @Override
    public void onEnable() {
        switch (mode.getMode()) {
            case "Astolfo":{

                mc.displayGuiScreen(clickguiAs == null ? clickguiAs = new uwu.flauxy.ui.astolfo.ClickGUI() : clickguiAs);
                break;
            }
            case "Discord":{

                mc.displayGuiScreen(new Discord());
                break;
            }
            case "Flat": {
                mc.displayGuiScreen(flatUi == null ? flatUi = new FlatUi() : flatUi);
                toggle();
                break;
            }
            case "Dropdown": {
                mc.displayGuiScreen(clickgui == null ? clickgui = new uwu.flauxy.ui.dropdown.ClickGUI() : clickgui);
                break;
            }
        }
        super.onEnable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || mc.currentScreen == null) this.toggle();
        }
    }
}
