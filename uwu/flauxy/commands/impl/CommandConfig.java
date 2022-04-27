package uwu.flauxy.commands.impl;


import net.minecraft.client.Minecraft;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.utils.Wrapper;

import java.io.File;

public class CommandConfig extends Command {


    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getSyntax() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Config thing";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {

        if(args[0].equalsIgnoreCase("save")) {
            Wrapper.instance.log("Saved the config " + args[1] +  ".");
            try {
                Flauxy.INSTANCE.getConfigManager().save(args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if(args[0].equalsIgnoreCase("load")) {
            Wrapper.instance.log("Config " + args[1] +  " has been loaded.");
            try {
                Flauxy.INSTANCE.getConfigManager().load(args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(args[0].equalsIgnoreCase("delete")) {
            Wrapper.instance.log("Config " + args[1] +  " has been removed.");
            try {
                Flauxy.INSTANCE.getConfigManager().delete(args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(args[0].equalsIgnoreCase("list")) {

            File folder = new File("ClownWare" + "/Configs");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    Wrapper.instance.log(listOfFiles[i].getName());
                }
            }
        }

    }
}
