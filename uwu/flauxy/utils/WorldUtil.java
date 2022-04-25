package uwu.flauxy.utils;

import net.minecraft.client.Minecraft;

public class WorldUtil {

    public static boolean shouldNotRun(){
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.ticksExisted <= 5;
    }

}
