package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;

public class CommandSay extends Command {
    @Override
    public String getName() {
        return "say";
    }

    @Override
    public String getSyntax() {
        return "say";
    }

    @Override
    public String getDescription() {
        return "says shiut";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        Wrapper.instance.log("say");
    }
}
