package uwu.flauxy.module.impl.display;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.ui.cape.CapeGUI;

@ModuleInfo(name = "CapeGUI", cat = Category.Display, key = Keyboard.KEY_P, displayName = "Cape GUI")
public class ModuleCapeGUI extends Module {

    @Override
    public void onEnable() {
        mc.displayGuiScreen(null);
        mc.displayGuiScreen(new CapeGUI(new ScaledResolution(mc)));
    }
}
