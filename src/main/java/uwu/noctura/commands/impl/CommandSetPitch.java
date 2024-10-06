package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.Noctura;
import uwu.noctura.commands.Command;
import uwu.noctura.module.impl.exploit.NoPitchLimit;
import uwu.noctura.utils.Wrapper;

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
            NoPitchLimit nopitch = Noctura.INSTANCE.getModuleManager().getModule(NoPitchLimit.class);
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
