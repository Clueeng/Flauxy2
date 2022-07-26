package uwu.flauxy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import uwu.flauxy.Flauxy;

public interface Methods {

    Minecraft mc = Minecraft.getMinecraft();

    default Timer getTimer() {
        return mc.timer;
    }

    default boolean isMoving() {
        return mc.thePlayer.moveStrafing != 0 || mc.thePlayer.moveForward != 0;
    }

    default boolean isEnabled(String name) {
        return Flauxy.INSTANCE.getModuleManager().getModule(name).isToggled();
    }
}
