package uwu.noctura.module.impl.display;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.ui.cape.CapeGUI;

@ModuleInfo(name = "CapeGUI", cat = Category.Display, key = Keyboard.KEY_P, displayName = "Cape GUI")
public class ModuleCapeGUI extends Module {

    @Override
    public void onEnable() {
        mc.displayGuiScreen(null);
        mc.displayGuiScreen(new CapeGUI(new ScaledResolution(mc)));
    }
}
