package uwu.noctura.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;

@ModuleInfo(name = "Phase", displayName = "Phase", key = Keyboard.KEY_H, cat = Category.Movement)
public class Phase extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Vclip", "Redesky");
    NumberSetting clipAmount = new NumberSetting("Blocks", -3, -5, 5, 1).setCanShow(m -> mode.is("Vclip"));

    public Phase(){
        addSettings(mode, clipAmount);
    }

    @Override
    public void onEvent(Event e){
        if(e instanceof EventMotion){
            switch(mode.getMode()){
                case "Vclip":{
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + clipAmount.getValue(), mc.thePlayer.posZ);
                    Wrapper.instance.log("Clipped to the position " + (mc.thePlayer.posY + clipAmount.getValue()));
                    this.toggle();
                    break;
                }
                case "Normal":{
                    if(mc.thePlayer.moveForward < 0) return;
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (mc.thePlayer.motionX * 6), mc.thePlayer.posY, mc.thePlayer.posZ + (mc.thePlayer.motionZ * 6), true));
                    mc.thePlayer.setPosition(mc.thePlayer.posX + (mc.thePlayer.motionX * 6), mc.thePlayer.posY, mc.thePlayer.posZ + (mc.thePlayer.motionZ * 6));
                    if(mc.thePlayer.ticksExisted % 50 == 0) this.toggle();
                    break;
                }
                case "Redesky":{
                    if(mc.thePlayer.moveForward < 0) return;
                    mc.thePlayer.setSneaking(mc.thePlayer.ticksExisted % 2 == 0);
                    if(mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42f;

                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (mc.thePlayer.motionX * 6) + 0.4 + 1, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ + (mc.thePlayer.motionZ * 6) + 1, true));
                    mc.thePlayer.setPosition(mc.thePlayer.posX + (mc.thePlayer.motionX * 6) + 0.4 + 1, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ + (mc.thePlayer.motionZ * 6) + 1);
                    if(mc.thePlayer.ticksExisted % 50 == 0) this.toggle();
                    break;
                }
            }
        }
    }

}
