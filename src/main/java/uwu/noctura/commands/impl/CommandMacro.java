package uwu.noctura.commands.impl;

import net.minecraft.client.Minecraft;
import uwu.noctura.commands.Command;
import uwu.noctura.module.impl.other.util.Folder;
import uwu.noctura.utils.Wrapper;

import java.io.*;
import java.util.Arrays;

public class CommandMacro extends Command {

    public static String[] macros = new String[8];
    private File dir;
    private File dataFile;

    public CommandMacro() {
        load();
    }

    @Override
    public String getName() {
        return "macro";
    }

    @Override
    public String getSyntax() {
        return ".macro set <x> <command>";
    }

    @Override
    public String getDescription() {
        return "Sets a macro to a command.";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        Wrapper.instance.log("Debug: " + Arrays.toString(macros));
        if (args.length < 4 || !args[1].equalsIgnoreCase("set")) {  // Adjusted indices
            Wrapper.instance.log("Usage: " + getSyntax());
            return;
        }

        try {
            // Parse the index for the macro slot
            int macroIndex = Integer.parseInt(args[2]);  // Adjusted index

            if (macroIndex < 1 || macroIndex > 8) {
                Wrapper.instance.log("Macro index must be between 1 and 8.");
                return;
            }

            // Combine the rest of the arguments as the command
            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 3; i < args.length; i++) {  // Adjusted index
                if (i > 3) {
                    commandBuilder.append(" ");
                }
                commandBuilder.append(args[i]);
            }
            String commandToSet = commandBuilder.toString();

            // Set the macro
            macros[macroIndex - 1] = commandToSet;
            Wrapper.instance.log("Set macro " + macroIndex + " to: " + commandToSet);

            // Save the macros to a file
            save();

        } catch (NumberFormatException e) {
            Wrapper.instance.log("Invalid macro index: " + args[2]);  // Adjusted index
        }
    }

    public void load() {
        this.dir = new File(String.valueOf(Folder.dir)); // Assuming Folder.dir is a valid directory path
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }
        String fileName = "macros";
        this.dataFile = new File(this.dir, fileName + ".txt");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(this.dataFile))) {
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null && index < macros.length) {
                macros[index++] = line.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (dataFile == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.dataFile))) {
            for (String macro : macros) {
                writer.write((macro != null ? macro : "") + "\n");
            }
            Wrapper.instance.log("Macros saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Wrapper.instance.log("Failed to save macros: " + e.getMessage());
        }
    }

    // Getter for the macros array
    public String getMacro(int index) {
        if (index >= 0 && index < macros.length) {
            return macros[index];
        }
        return null;
    }
}
