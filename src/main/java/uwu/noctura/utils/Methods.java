package uwu.noctura.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import uwu.noctura.Noctura;

public interface Methods { // we need to finish this beforef
    Minecraft mc = Minecraft.getMinecraft();

    default Timer getTimer() {
        return mc.timer;
    }

    default boolean isMoving() {
        return mc.thePlayer.moveStrafing != 0 || mc.thePlayer.moveForward != 0;
    }

    default boolean isEnabled(String name) {
        return Noctura.INSTANCE.getModuleManager().getModule(name).isToggled();
    }
}
