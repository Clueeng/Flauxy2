package uwu.noctura.module.impl.combat;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

@ModuleInfo(name = "KeepSprint", displayName = "Keep Sprint", key = -1, cat = Category.Combat)
public class KeepSprint extends Module {

    @Override
    public void onEvent(Event e) {
    }
}
