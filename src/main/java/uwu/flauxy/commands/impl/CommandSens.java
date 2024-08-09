package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;

import java.util.Arrays;

public class CommandSens extends Command {
    @Override
    public String getName() {
        return "sens";
    }

    @Override
    public String getSyntax() {
        return ".sens <value>";
    }

    @Override
    public String getDescription() {
        return "Sets your sensitivity";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if(args.length < 2){
            Wrapper.instance.log("Wrong syntax: " + getSyntax());
            return;
        }
        if(args.length == 2){
            String argString = args[1];
            float sens = Float.parseFloat(argString);
            if(!Float.isNaN(sens)){
                mc.gameSettings.mouseSensitivity = sens;
            }else{
                Wrapper.instance.log("Use a number you dumb dumb");
            }

        }
    }
}
