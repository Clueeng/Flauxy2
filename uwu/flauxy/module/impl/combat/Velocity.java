package uwu.flauxy.module.impl.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Velocity", displayName = "Velocity", key = -1, cat = Category.Combat)
public class Velocity extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Redesky");
    public NumberSetting x = new NumberSetting("X", 0, 0, 100, 1).setCanShow((m) -> mode.is("Cancel"));
    public NumberSetting y = new NumberSetting("Y", 0, 0, 100, 1).setCanShow((m) -> mode.is("Cancel"));


    public Velocity(){
        addSettings(mode, x, y);
    }

    public void onEvent(Event ev){
        if(ev instanceof EventReceivePacket){
            EventReceivePacket event = (EventReceivePacket) ev;
            switch(mode.getMode()){
                case "Cancel":{
                    if(event.getPacket() instanceof S12PacketEntityVelocity){
                        S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                        if(x.getValue() > 0 || y.getValue() > 0){
                            packet.setMotionX((int) ((packet.getMotionX() / 100) * x.getValue()) );
                            packet.setMotionY((int) ((packet.getMotionY() / 100) * y.getValue()) );
                            packet.setMotionZ((int) ((packet.getMotionZ() / 100) * x.getValue()) );
                        }else{
                            event.setCancelled(true);
                        }
                    }
                    break;
                }
                case "Redesky":{
                    if(shouldVelo(event)){
                        S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                        packet.setMotionX((int) ((packet.getMotionX() / 100) * 10) );
                        packet.setMotionY((int) ((packet.getMotionY() / 100) * 100) );
                        packet.setMotionZ((int) ((packet.getMotionZ() / 100) * 10) );
                        PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 0, mc.thePlayer.posX, false));

                        Wrapper.instance.log("Received Knockback");
                    }
                    break;
                }
            }
        }
    }

    public boolean shouldVelo(EventReceivePacket event){
        return event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)event.getPacket()).getEntityID() == mc.thePlayer.getEntityId();
    }

}