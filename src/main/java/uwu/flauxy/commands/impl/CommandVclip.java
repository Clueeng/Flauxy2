package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;

public class CommandVclip extends Command {
    @Override
    public String getName() {
        return "vclip";
    }

    @Override
    public String getSyntax() {
        return ".vclip <value>";
    }

    @Override
    public String getDescription() {
        return "Sets your position";
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
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + sens, mc.thePlayer.posZ);
            }else{
                Wrapper.instance.log("Use a number you dumb dumb");
            }

        }
    }
}
