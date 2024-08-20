package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.gen.FlatGeneratorInfo;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.impl.exploit.NoPitchLimit;
import uwu.flauxy.module.impl.other.util.Folder;
import uwu.flauxy.utils.Wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class CommandSetPitch extends Command {

    @Override
    public String getName() {
        return "setpitch";
    }

    @Override
    public String getSyntax() {
        return ".setpitch <value>";
    }

    @Override
    public String getDescription() {
        return "Sets your pitch";
    }


    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if (args.length < 2) {
            Wrapper.instance.log("Incorrect syntax");
            return;
        }
        try {
            String pitchStr = args[1];
            float pitch = Float.parseFloat(pitchStr);
            NoPitchLimit nopitch = Flauxy.INSTANCE.getModuleManager().getModule(NoPitchLimit.class);
            if((pitch > 90 || pitch < -90) && !nopitch.isToggled()){
                Wrapper.instance.log("Enable no pitch limit");
                return;
            }else{
                mc.thePlayer.rotationPitch = pitch;
            }
            Wrapper.instance.log("Set pitch to " + pitch);
        } catch (NumberFormatException e) {
            Wrapper.instance.log("Invalid pitch value");
        }
    }
}
