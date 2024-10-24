package uwu.noctura.module.impl.player;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.lwjgl.input.Keyboard;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.event.impl.EventSendPacket;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.impl.display.ArrayList;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.ui.dropdown.ClickGUI;
import uwu.noctura.ui.packet.PacketTweaker;
import uwu.noctura.utils.MoveUtils;
import uwu.noctura.utils.PacketUtil;

@ModuleInfo(name = "InventoryMove", displayName = "Inventory Move", key = -1, cat = Category.Player)
public class InventoryMove extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Spoof", "Vulcan");
    public BooleanSetting onlyClickGUI = new BooleanSetting("ClickGUI only", false);
    public BooleanSetting stopOnClick = new BooleanSetting("Stop On Click", false);

    public InventoryMove() {
        addSettings(mode, onlyClickGUI, stopOnClick);
    }

    java.util.ArrayList<Packet> delayed = new java.util.ArrayList<>();

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventSendPacket){
            EventSendPacket ev = (EventSendPacket) e;
            if(ev.getPacket() instanceof C0EPacketClickWindow && mode.is("Vulcan")){
                ev.setCancelled(true);
                delayed.add(ev.getPacket());
            }
            if(mc.currentScreen != null){
                if(ev.getPacket() instanceof C0BPacketEntityAction && mode.is("Vulcan")){
                    C0BPacketEntityAction.Action action = ((C0BPacketEntityAction) ev.getPacket()).getAction();
                    if(action.equals(C0BPacketEntityAction.Action.START_SPRINTING)){
                        ev.setCancelled(true);
                        PacketUtil.sendSilentPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    }
                }
            }
            if(ev.getPacket() instanceof C0BPacketEntityAction){
                C0BPacketEntityAction.Action action = ((C0BPacketEntityAction) ev.getPacket()).getAction();
                if(mode.is("Spoof")){
                    if(action.equals(C0BPacketEntityAction.Action.OPEN_INVENTORY)){
                        ev.setCancelled(true);
                    }
                }
            }
        }

        if(e instanceof EventMotion) {
            EventMotion ev = (EventMotion) e;
            if(!(mc.currentScreen instanceof ClickGUI || mc.currentScreen instanceof uwu.noctura.ui.astolfo.ClickGUI || mc.currentScreen instanceof uwu.noctura.ui.noctura.ClickGUI)){
                if(onlyClickGUI.isEnabled()){
                    return;
                }
            }

            while(!delayed.isEmpty() && mode.is("Vulcan") && ev.isPre()){
                mc.thePlayer.setSprinting(false);
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                MoveUtils.stopMoving();
                MoveUtils.cancelMoveInputs();
                PacketUtil.sendSilentPacket(    delayed.get(0));
                delayed.clear();
            }
            if(mode.is("Vulcan") && mc.currentScreen != null){
                MoveUtils.enableMoveInputs();
            }

            if(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat || mc.currentScreen == null || mc.currentScreen instanceof PacketTweaker) return;
            if(mode.is("Vulcan")){
                if(mc.thePlayer.onGround){
                    mc.thePlayer.motionZ *= 0.75;
                    mc.thePlayer.motionX *= 0.75;
                }else{
                    mc.thePlayer.motionZ *= 0.885;
                    mc.thePlayer.motionX *= 0.885;
                }
            }
            keyset(mc.gameSettings.keyBindForward);
            keyset(mc.gameSettings.keyBindLeft);
            keyset(mc.gameSettings.keyBindRight);
            keyset(mc.gameSettings.keyBindBack);
            keyset(mc.gameSettings.keyBindJump);
            mc.gameSettings.keyBindSneak.pressed = false;
        }
    }

    private void keyset(KeyBinding key){
        key.pressed = Keyboard.isKeyDown(key.getKeyCode());
    }

}
