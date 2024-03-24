package uwu.flauxy.commands.impl;


import net.minecraft.client.Minecraft;
import uwu.flauxy.Flauxy;
import uwu.flauxy.commands.Command;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleManager;
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

        //Wrapper.instance.log("0: " + args[0] + "1: " + args[1] + "2: " + args[2]  );
        if(args[1].equalsIgnoreCase("list")) {

            File folder = new File("Flauxy" + "/Configs");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    Wrapper.instance.log(listOfFiles[i].getName());
                }
            }
        }
        if(true == true){

            if(args[1].equalsIgnoreCase("save")) {
                Wrapper.instance.log("Saved the config " + args[2] +  ".");
                try {
                    Flauxy.INSTANCE.getConfigManager().save(args[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if(args[1].equalsIgnoreCase("load")) {
                Wrapper.instance.log("Config " + args[2] +  " has been loaded.");
                try {
                    Flauxy.INSTANCE.getConfigManager().load(args[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(args[1].equalsIgnoreCase("delete")) {
                Wrapper.instance.log("Config " + args[2] +  " has been removed.");
                try {
                    Flauxy.INSTANCE.getConfigManager().delete(args[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }



    }
}
