package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.commands.Command;
import uwu.noctura.utils.Wrapper;

public class CommandSay extends Command {
    @Override
    public String getName() {
        return "say";
    }

    @Override
    public String getSyntax() {
        return ".say <msg>";
    }

    @Override
    public String getDescription() {
        return "says something in chat";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        Wrapper.instance.log("say");
    }
}
