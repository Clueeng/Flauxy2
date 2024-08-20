package uwu.flauxy.module.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

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
