package uwu.noctura.commands.impl;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import uwu.noctura.commands.Command;
import uwu.noctura.utils.Wrapper;

public class CommandGhost extends Command {
    @Override
    public String getName() {
        return "ghost";
    }

    @Override
    public String getSyntax() {
        return ".ghost";
    }

    @Override
    public String getDescription() {
        return "Toggles the ghost (safe) mode";
    }

    private boolean safeMode;
    @Getter
    private long toggledMs;
    public boolean getSafeMode(){
        return safeMode;
    }
    public void toggleSafeMode(){
        safeMode = !safeMode;
    }
    public void setGhostmode(boolean g){
        safeMode = g;
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        toggleSafeMode();
        toggledMs = System.currentTimeMillis();
        if(getSafeMode()){
            Wrapper.instance.log(EnumChatFormatting.WHITE + "ghost mode" + EnumChatFormatting.GREEN + " enabled");
        }else{
            Wrapper.instance.log(EnumChatFormatting.WHITE + "ghost mode" + EnumChatFormatting.RED + " disabled");
        }
    }
}
