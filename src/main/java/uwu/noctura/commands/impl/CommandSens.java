package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.commands.Command;
import uwu.noctura.utils.Wrapper;

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
