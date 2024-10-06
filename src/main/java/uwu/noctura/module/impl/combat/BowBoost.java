package uwu.noctura.module.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventReceivePacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

@ModuleInfo(name = "BowBoost", displayName = "Bow Boost", cat = Category.Combat, key = Keyboard.KEY_NONE)
public class BowBoost extends Module {
    public NumberSetting x = new NumberSetting("H", 1, 1, 20, 0.25);
    public NumberSetting y = new NumberSetting("V", 1, 1, 20, 0.25);

    public BowBoost(){
        addSettings(x, y);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventReceivePacket){
            EventReceivePacket event = (EventReceivePacket) e;

            if(event.getPacket() instanceof S12PacketEntityVelocity){
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                if(packet.getEntityID() == mc.thePlayer.getEntityId()){
                    packet.setMotionX((int) ((packet.getMotionX() / 100) * (x.getValue() * 100)) ); // between 1 and 20?
                    packet.setMotionY((int) ((packet.getMotionY() / 100) * (y.getValue()) * 100) );
                    packet.setMotionZ((int) ((packet.getMotionZ() / 100) * (x.getValue()) * 100) );
                }
            }
        }
    }
}
