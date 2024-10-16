package uwu.noctura.utils.config;


import uwu.noctura.Noctura;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleManager;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;

import java.io.*;
import java.util.ArrayList;

public class ConfigUtil {

      /*
    imported from Auxware
     */


    private File dir;

    private File dataFile;

    public ArrayList<File> getAllConfigs(){
        File folder = new File("Noctura" + "/Configs");
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> finalList = null;

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                finalList.add(listOfFiles[i]);
            }
        }
        return finalList;
    }

    public void save(String name) {
        KeyLoader.save(ModuleManager.modules);
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists())
            this.dir.mkdir();
        this.dataFile = new File(this.dir, name + ".txt");
        if (!this.dataFile.exists())
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        ArrayList<String> toSave = new ArrayList<>();
        for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
            toSave.add("Module:" + m.getName() + ":" + m.isToggled() + ":" + m.getKey() + ":" + '\001');
            for (Setting s : m.getSettings()) {
                if(s instanceof GraphSetting){
                    GraphSetting set = (GraphSetting) s;
                    toSave.add("Graph:" + m.getName() + ":" + set.name + ":" + set.getX() + ":" + set.getY());
                }
                if (s instanceof NumberSetting) {
                    NumberSetting set = (NumberSetting)s;
                    toSave.add("Number:" + m.getName() + ":" + set.name + ":" + set.getValue());
                    continue;
                }
                if (s instanceof BooleanSetting) {
                    BooleanSetting set = (BooleanSetting)s;
                    toSave.add("Boolean:" + m.getName() + ":" + set.name + ":" + set.enabled);
                    continue;
                }
                if (s instanceof ModeSetting) {
                    ModeSetting set = (ModeSetting)s;
                    toSave.add("Mode:" + m.getName() + ":" + set.name + ":" + set.getMode());
                    continue;
                }
            }
        }
        try {
            PrintWriter pw = new PrintWriter(this.dataFile);
            for (String str : toSave)
                pw.println(str);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void delete(String name) {
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists())
            this.dir.mkdir();
        this.dataFile = new File(this.dir, name + ".txt");
        try {
            this.dataFile.delete();
        } catch (Exception exception) {}
    }

    public String getCurrentloadedConfig = "None";

    public void load(String name) {
        this.getCurrentloadedConfig = name;
        this.dir = new File(String.valueOf(Folder.dir));
        if (!this.dir.exists())
            this.dir.mkdir();
        this.dataFile = new File(this.dir, name + ".txt");
        for(Module m : Noctura.INSTANCE.getModuleManager().modules) {
            if(m.isToggled()  ) {
                m.toggle();
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (String s : lines) {
                String[] args = s.split(":");
                if (s.toLowerCase().startsWith("module:"))
                    for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
                        if (m.getName().equalsIgnoreCase(args[1]) ) {
                            boolean shouldEnable = Boolean.parseBoolean(args[2]);
                            if (shouldEnable && !m.isToggled() )
                                m.setToggled(true);
                            if (args.length > 4);
                        }
                    }
                if (s.toLowerCase().startsWith("graph:")) {
                    // Split the string into components
                    String[] parts = s.split(":");

                    if (parts.length == 5) {  // Ensure the correct number of arguments
                        String moduleName = parts[1];
                        String settingName = parts[2];
                        double valueX = Double.parseDouble(parts[3]);
                        double valueY = Double.parseDouble(parts[4]);

                        // Loop through the modules to find the correct one
                        for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
                            if (m.getName().equalsIgnoreCase(moduleName)) {
                                // Loop through the settings to find the correct GraphSetting
                                for (Setting setting : m.getSettings()) {
                                    if (setting instanceof GraphSetting && setting.name.equalsIgnoreCase(settingName)) {
                                        GraphSetting graphSetting = (GraphSetting) setting;
                                        graphSetting.setX((float) valueX);  // Set the X value
                                        graphSetting.setY((float) valueY);  // Set the Y value
                                    }
                                }
                            }
                        }
                    }
                }
                if (s.toLowerCase().startsWith("number:"))
                    for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
                        if (m.getName().equalsIgnoreCase(args[1]) )
                            for (Setting setting : m.getSettings()) {
                                if (!(setting instanceof NumberSetting))
                                    continue;
                                if (setting.name.equalsIgnoreCase(args[2])) {
                                    NumberSetting setting1 = (NumberSetting)setting;
                                    setting1.setValue(Double.parseDouble(args[3]));
                                }
                            }
                    }
                if (s.toLowerCase().startsWith("boolean:"))
                    for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
                        if (m.getName().equalsIgnoreCase(args[1]) )
                            for (Setting setting : m.getSettings()) {
                                if (!(setting instanceof BooleanSetting))
                                    continue;
                                if (setting.name.equalsIgnoreCase(args[2])) {
                                    BooleanSetting setting1 = (BooleanSetting)setting;
                                    setting1.setEnabled(Boolean.parseBoolean(args[3]));
                                }
                            }
                    }
                if (s.toLowerCase().startsWith("mode:"))
                    for (Module m : Noctura.INSTANCE.getModuleManager().modules) {
                        if (m.getName().equalsIgnoreCase(args[1]) )
                            for (Setting setting : m.getSettings()) {
                                if (!(setting instanceof ModeSetting))
                                    continue;
                                for (String str : ((ModeSetting)setting).modes) {
                                    if (setting.name.equalsIgnoreCase(args[2]) && args[3]
                                            .equalsIgnoreCase(str)) {
                                        ModeSetting setting1 = (ModeSetting)setting;
                                        setting1.setSelected(args[3]);
                                    }
                                }
                            }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
