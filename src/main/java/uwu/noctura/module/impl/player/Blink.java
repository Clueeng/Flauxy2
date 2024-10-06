package uwu.noctura.module.impl.player;

import net.minecraft.network.Packet;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventUpdate;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.PacketUtil;

import java.util.LinkedList;

@ModuleInfo(name = "Blink", displayName = "Blink", cat = Category.Player, key = -1)
public class Blink extends Module {

    private final BooleanSetting pulse = new BooleanSetting("Pulse", true);
    private NumberSetting pulseDelay = new NumberSetting("Pulse Delay Ticks", 40, 1, 100, 1).setCanShow(m -> pulse.getValue());
    private LinkedList<Packet> packetsLinked = new LinkedList<>();
    int ticksSpan = 0;

    public Blink(){
        addSettings(pulse, pulseDelay);
    }

    public void onEvent(Event e){
        if(e instanceof EventUpdate){
            ticksSpan++;
        }
        PacketUtil.blink(packetsLinked, e, ticksSpan, pulse.getValue() ? (int) pulseDelay.getValue() : Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void onDisable(){
        packetsLinked.clear();
    }

}
