package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.commands.CommandManager;
import uwu.flauxy.utils.Wrapper;

public class CommandHelp extends Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getSyntax() {
        return ".help";
    }

    @Override
    public String getDescription() {
        return "Sends all commands";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        Wrapper.instance.log(String.format("Available commands (%d)", CommandManager.getCommands().size()));
        for(Command c : CommandManager.getCommands()){
            Wrapper.instance.logHover(c.getName() + ": " + EnumChatFormatting.GRAY + c.getDescription(), c.getSyntax());
        }
    }
}
