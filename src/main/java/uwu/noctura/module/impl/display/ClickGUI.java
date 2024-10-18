package uwu.noctura.module.impl.display;

import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.ui.discord.Discord;
import uwu.noctura.ui.flat.FlatUi;

@ModuleInfo(name = "ClickGUI", displayName = "ClickGui", key = Keyboard.KEY_RSHIFT, cat = Category.Display)
public class ClickGUI extends Module {

    uwu.noctura.ui.dropdown.ClickGUI clickgui;
    uwu.noctura.ui.noctura.ClickGUI clickguiNoctura;
    uwu.noctura.ui.astolfo.ClickGUI clickguiAs;
    Discord discordUi;
    FlatUi flatUi;
    public ModeSetting mode = new ModeSetting("Mode", "Noctura", "Dropdown", "Astolfo", "Noctura");
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
                mc.displayGuiScreen(clickguiAs == null ? clickguiAs = new uwu.noctura.ui.astolfo.ClickGUI() : clickguiAs);
                break;
            }
            case "Dropdown": {
                mc.displayGuiScreen(clickgui == null ? clickgui = new uwu.noctura.ui.dropdown.ClickGUI() : clickgui);
                break;
            }
            case "Noctura": {
                mc.displayGuiScreen(clickguiNoctura == null ? clickguiNoctura = new uwu.noctura.ui.noctura.ClickGUI() : clickguiNoctura);
                //mc.displayGuiScreen(new uwu.noctura.ui.noctura.ClickGUI());
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
