package uwu.flauxy.module.impl.combat;

import net.minecraft.entity.Entity;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventMotion;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.impl.movement.Fly;
import uwu.flauxy.module.impl.movement.Speed;
import uwu.flauxy.utils.MoveUtils;

@ModuleInfo(name = "Targetstrafe", displayName = "Target Strafe", key = -1, cat = Category.Combat)
public class TargetStrafe extends Module {
    public float yaw = 0.0f;
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){
            Killaura killaura = Flauxy.INSTANCE.getModuleManager().getModule(Killaura.class);
            if(!killaura.isToggled() || killaura.currentTarget == null){
                yaw = Float.NaN;
            }
            Speed speed = Flauxy.INSTANCE.getModuleManager().getModule(Speed.class);
            Fly fly = Flauxy.INSTANCE.getModuleManager().getModule(Fly.class);
            Entity target = killaura.currentTarget;
            if(target == null || !killaura.isToggled()){
                return;
            }
            if(!fly.isToggled() && !speed.isToggled()){
                return;
            }
            yaw = killaura.getRotations(target)[0]; // rate my code
            // baseYaw + (float) (90 * this.direction * (distanceProperty.get() / distance))
            float distance = 2.0f;
            float direction = getStrafe() == 0 ? 1 : getStrafe();
            float deltaX = (float) Math.abs(mc.thePlayer.posX - target.posX);
            float deltaZ = (float) Math.abs(mc.thePlayer.posZ - target.posZ);
            float targetDistance = (float) Math.hypot(deltaX,deltaZ);
            yaw += (90 * direction * (distance / targetDistance));
            MoveUtils.setSpeed(MoveUtils.getSpeed(),yaw,1,1);
        }
    }
    public int getStrafe() {
        int strafe = 0;
        if (mc.gameSettings.keyBindLeft.pressed) strafe++;
        if (mc.gameSettings.keyBindRight.pressed) strafe--;
        return strafe;
    }
    // big skid

}
