package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.commands.Command;
import uwu.noctura.utils.Wrapper;

public class CommandSpammer extends Command {

    public CommandSpammer() {

    }

    @Override
    public String getName() {
        return "spam";
    }

    @Override
    public String getSyntax() {
        return ".spam <command>";
    }

    @Override
    public String getDescription() {
        return "Sets a command for the spammer.";
    }

    public static String spammerCommand = "";

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if (args.length < 2) {
            Wrapper.instance.log("Usage: " + getSyntax());
            return;
        }
        try {
            // Combine everything after ".spam" as the command
            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {  // Start from the first index after ".spam"
                if (i > 1) {
                    commandBuilder.append(" ");
                }
                commandBuilder.append(args[i]);
            }

            spammerCommand = commandBuilder.toString();
            Wrapper.instance.log("Set " + spammerCommand);
        } catch (NumberFormatException e) {
            Wrapper.instance.log("Invalid input: " + args[1]);
        }
    }
}
