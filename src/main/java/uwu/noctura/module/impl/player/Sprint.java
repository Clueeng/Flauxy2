package uwu.noctura.module.impl.player;

import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventMotion;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;

@ModuleInfo(name = "Sprint", displayName = "Sprint", key = Keyboard.KEY_B, cat = Category.Player)
public class Sprint extends Module {


    @Override
    public void onEvent(Event ev){
        if(ev instanceof EventMotion){
            mc.thePlayer.setSprinting(mc.thePlayer.isMoving() && mc.thePlayer.moveForward > 0 && !mc.thePlayer.isCollidedHorizontally && !((mc.thePlayer.isBlocking() && mc.thePlayer.isEating()) && !Noctura.INSTANCE.moduleManager.getModule(Noslow.class).isToggled()));
        }
    }

}
