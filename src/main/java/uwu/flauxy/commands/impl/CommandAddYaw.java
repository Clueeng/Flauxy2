package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;

import java.util.HashMap;

public class CommandAddYaw extends Command {
    @Override
    public String getName() {
        return "addyaw";
    }

    @Override
    public String getSyntax() {
        return ".addyaw <num>";
    }

    @Override
    public String getDescription() {
        return "Adds your yaw";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {

        // 0 is the command itself
        if(args.length < 1 || args.length > 2){
            Wrapper.instance.log("Incorrect syntax");
            return;
        }
        String yawStr = args[1];
        float yaw = Float.parseFloat(yawStr);
        Wrapper.instance.log("Added " + yaw + " to yaw. New value: " + (mc.thePlayer.rotationYaw + yaw));
        mc.thePlayer.rotationYaw += yaw;
    }
}
