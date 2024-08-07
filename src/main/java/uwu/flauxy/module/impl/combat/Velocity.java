package uwu.flauxy.module.impl.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.ModuleManager;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.WorldUtil;
import uwu.flauxy.utils.Wrapper;

@ModuleInfo(name = "Velocity", displayName = "Velocity", key = -1, cat = Category.Combat)
public class Velocity extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Redesky");
    public NumberSetting x = new NumberSetting("X", 0, 0, 100, 1).setCanShow((m) -> mode.is("Cancel"));
    public NumberSetting y = new NumberSetting("Y", 0, 0, 100, 1).setCanShow((m) -> mode.is("Cancel"));
    public NumberSetting strength = new NumberSetting("DragClick amount", 5, 0, 20, 1).setCanShow(m -> mode.is("Redesky"));
    private boolean receivedVelocity;


    public Velocity(){
        addSettings(mode, x, y);
    }

    public void onEvent(Event ev){
        switch(mode.getMode()){
            case "Redesky":{

                Redesky(ev);
                break;
            }
        }
        if(ev instanceof EventUpdate){
            String addMode = "";
            addMode = mode.getMode();

            this.setDisplayName("Velocity " + EnumChatFormatting.WHITE + addMode);
        }
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

            }
        }
    }

    public boolean shouldVelo(EventReceivePacket event){
        return event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)event.getPacket()).getEntityID() == mc.thePlayer.getEntityId();
    }

    private void Redesky(Event event) {
        if(event instanceof EventReceivePacket) {
            EventReceivePacket e = (EventReceivePacket) event;
            if(e.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    receivedVelocity = true;

                    e.setCancelled(true);
                    mc.thePlayer.motionY = packet.getMotionY() / 8000.0D;
                    //MoveUtils.motionMult(0.2);
                    for(int i = 0; i < 10; i++) {
                        WorldUtil.attackFakePlayer();
                    }

                }
            }
        } else if(event instanceof EventUpdate) {
            if(mc.thePlayer.hurtTime == 10 && receivedVelocity) {
                for(int i = 0; i < (int) strength.getValue(); i++) {
                    //PacketUtils.releaseUseItem(false);
                    WorldUtil.attackFakePlayer();
                    mc.thePlayer.motionX= 0.6D;
                    mc.thePlayer.motionZ *= 0.6D;
                }
            }

            if(mc.thePlayer.hurtTime == 0) {
                receivedVelocity = false;
            }
        } else if(event instanceof EventMove) {
            EventMove e = (EventMove) event;

        }
    }

}