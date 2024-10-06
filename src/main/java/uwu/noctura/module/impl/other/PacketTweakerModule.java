package uwu.noctura.module.impl.other;

import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.ui.packet.PacketTweaker;

@ModuleInfo(name = "PacketTweaker", displayName = "Packet Tweaker", cat = Category.Other, key = -1)
public class PacketTweakerModule extends Module {
    PacketTweaker instance;
    @Override
    public void onEnable() {
        this.mc.displayGuiScreen(instance == null ? instance = new PacketTweaker() : instance);
        this.toggle();
    }
}
