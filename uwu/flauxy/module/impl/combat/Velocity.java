package uwu.flauxy.module.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import uwu.flauxy.event.EventReceivePacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;

@ModuleInfo(name = "Velocity", displayName = "Velocity", key = -1, cat = Category.Combat)
public class Velocity extends Module {

    public NumberSetting x = new NumberSetting("X", 0, 0, 100, 1);
    public NumberSetting y = new NumberSetting("Y", 0, 0, 100, 1);


    public Velocity(){
        addSettings(x, y);
    }

    @EventTarget
    public void onReceive(EventReceivePacket event){
        if(event.getPacket() instanceof S12PacketEntityVelocity){
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if(x.getValue() > 0 || y.getValue() > 0){
                /*

							packet.setMotionX((int) ((packet.getMotionX() / 100) * getHV()));
							packet.setMotionY((int) ((packet.getMotionY() / 100) * getVV()));
							packet.setMotionZ((int) ((packet.getMotionZ() / 100) * getHV()));
                 */
                packet.setMotionX((int) ((packet.getMotionX() / 100) * x.getValue()) );
                packet.setMotionY((int) ((packet.getMotionY() / 100) * y.getValue()) );
                packet.setMotionZ((int) ((packet.getMotionZ() / 100) * x.getValue()) );
            }else{
                event.setCancelled(true);
            }
        }
    }

}
