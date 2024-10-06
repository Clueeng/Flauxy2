package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import uwu.noctura.commands.Command;
import uwu.noctura.module.impl.falses.KeySpammer;
import uwu.noctura.utils.Wrapper;

public class CommandSetSpamkey extends Command {

    @Override
    public String getName() {
        return "spamkey";
    }

    @Override
    public String getSyntax() {
        return ".spamkey key";
    }

    @Override
    public String getDescription() {
        return "Sets spam key for key spammer";
    }


    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if (args.length < 1) {
            Wrapper.instance.log("Error: No key specified. Usage: " + getSyntax());
            return;
        }

        String keyName = args[1].toUpperCase();

        int keyCode = Keyboard.getKeyIndex(keyName);

        if (keyCode == Keyboard.KEY_NONE) {
            Wrapper.instance.log("Error: Invalid key specified.");
            return;
        }

        KeySpammer.spamKeycode = keyCode;
        Wrapper.instance.log("Spam key set to: " + keyName);
    }
}
