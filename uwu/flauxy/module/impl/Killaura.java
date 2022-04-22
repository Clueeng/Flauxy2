package uwu.flauxy.module.impl;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.event.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.timer.Timer;

@ModuleInfo(name = "Killaura", displayName = "Killaura", key = Keyboard.KEY_R, cat = Category.Combat)
public class Killaura extends Module {

    NumberSetting cps = new NumberSetting("CPS", 1, 1, 1, 1);
    NumberSetting reach = new NumberSetting("CPS", 1, 1, 1, 1);

    ModeSetting rotations = new ModeSetting("Rotations", "Instant", "Instant", "Verus");

    BooleanSetting players = new BooleanSetting("Players", true);
    BooleanSetting mobs = new BooleanSetting("Mobs", true);
    BooleanSetting animals = new BooleanSetting("Animals", true);
    BooleanSetting shop = new BooleanSetting("Shopkeepers", false);
    Timer timer = new Timer();

    @EventTarget
    public void onMotion(EventMotion event){

    }

    public void yaw(float yaw, EventMotion e){
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
        e.setYaw(yaw);
    }
    public void pitch(float pitch, EventMotion e){
        mc.thePlayer.rotationPitchHead = pitch;
        e.setPitch(pitch);
    }

    public boolean isValid(Entity e){
        boolean finalValid = false;
        if(e instanceof EntityPlayer && players.getValue()){
            if((e.getName().equals("UPGRADES") || e.getName().equals("SHOP"))){
                finalValid = !shop.getValue();
            }else{
                finalValid = true;
            }
        }
        if(e instanceof EntityMob && mobs.getValue()){
            finalValid = true;
        }
        if(e instanceof EntityAnimal && animals.getValue()){
            finalValid = true;
        }
        return finalValid;
    }
    public float[] getRotations(Entity e) {
        double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX,
                deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        return new float[] {yaw, pitch};
    }
}
