package uwu.noctura.module.impl.player;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;


@ModuleInfo(name = "FaceChanger", displayName = "Face Changer", cat = Category.Player, key = -1)
public class FaceChanger extends Module
{
    public BooleanSetting face = new BooleanSetting("Change Face",true);
    public ModeSetting modes = new ModeSetting("Face", "Self","Self", "South", "North", "East", "West", "Up", "Down").setCanShow(n -> face.isEnabled());
    public BooleanSetting mousePos = new BooleanSetting("Change Mouse",true);

    public NumberSetting mouseX = new NumberSetting("Mouse X", 0, 0, 2, 0.05f).setCanShow(m -> mousePos.isEnabled());
    public NumberSetting mouseY = new NumberSetting("Mouse Y", 0, 0, 2, 0.05f).setCanShow(m -> mousePos.isEnabled());
    public NumberSetting mouseZ = new NumberSetting("Mouse Z", 0, 0, 2, 0.05f).setCanShow(m -> mousePos.isEnabled());

    public FaceChanger(){
        addSettings(face, modes, mousePos, mouseX, mouseY, mouseZ);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventSendPacket){
            EventSendPacket ev = (EventSendPacket) e;
            if(ev.getPacket() instanceof C08PacketPlayerBlockPlacement){
                C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) ev.getPacket();
                int dir = 0;
                switch (modes.getMode()){
                    case "Down":{
                        dir = 0;
                        break;
                    }
                    case "Up":{
                        dir = 1;
                        break;
                    }
                    case "North":{
                        dir = 2;
                        break;
                    }
                    case "South":{
                        dir = 3;
                        break;
                    }
                    case "West":{
                        dir = 4;
                        break;
                    }
                    case "East":{
                        dir = 5;
                        break;
                    }
                    case "Self":{
                        dir = 255;
                        break;
                    }
                }
                if(face.isEnabled()){
                    packet.setPlacedBlockDirection(dir);
                }
                if(mousePos.isEnabled()){
                    packet.setFacing((float) mouseX.getValue(), (float) mouseY.getValue(), (float) mouseZ.getValue());
                }
            }
        }
    }
}
