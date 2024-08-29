package uwu.flauxy.commands.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.impl.falses.KeySpammer;
import uwu.flauxy.module.impl.other.util.Folder;
import uwu.flauxy.utils.Wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

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
        // Check if an argument was provided
        if (args.length < 1) {
            Wrapper.instance.log("Error: No key specified. Usage: " + getSyntax());
            return;
        }

        // Get the key name from the arguments
        String keyName = args[1].toUpperCase();  // Convert to uppercase for consistent key matching

        // Get the key code from the LWJGL Keyboard class
        int keyCode = Keyboard.getKeyIndex(keyName);

        // Check if the key code is valid
        if (keyCode == Keyboard.KEY_NONE) {
            Wrapper.instance.log("Error: Invalid key specified.");
            return;
        }

        // Set the key code to the KeySpammer class
        KeySpammer.spamKeycode = keyCode;
        Wrapper.instance.log("Spam key set to: " + keyName);
    }
}
