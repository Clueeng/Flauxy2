package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;

public class CommandHelp extends Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getSyntax() {
        return "hl";
    }

    @Override
    public String getDescription() {
        return "Toggles the ghost (safe) mode";
    }

    private boolean safeMode;
    public boolean getSafeMode(){
        return safeMode;
    }
    public void toggleSafeMode(){
        safeMode = !safeMode;
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        toggleSafeMode();
        if(getSafeMode()){
            Wrapper.instance.log(EnumChatFormatting.WHITE + "ghost mode" + EnumChatFormatting.GREEN + " enabled");
        }else{
            Wrapper.instance.log(EnumChatFormatting.WHITE + "ghost mode" + EnumChatFormatting.RED + " disabled");
        }
    }
}
