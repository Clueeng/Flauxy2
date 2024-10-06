package uwu.noctura.utils.config;

import net.minecraft.client.Minecraft;
import uwu.noctura.Noctura;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleManager;


import java.io.*;
import java.util.List;

public class KeyLoader {

    public static void load(ModuleManager modules) throws IOException {
        File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Noctura.INSTANCE.getName(),"Keybinds.set");
        if (new File(Minecraft.getMinecraft().mcDataDir, Noctura.INSTANCE.getName()).mkdir() || !file.exists()) {
            file.createNewFile();
            return;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.lines().forEach(s -> {
                try {
                    modules.getModule(s.split(":")[0]).setKey(Integer.valueOf(s.split(":")[1]));
                }
                catch (Exception e) {}
            });
        }
    }

    public static void save(List<Module> moduleList) {
        File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Noctura.INSTANCE.getName(),"Keybinds.set");
        if (new File(Minecraft.getMinecraft().mcDataDir, Noctura.INSTANCE.getName()).mkdir() || !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
            return;
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Module module : moduleList) {
                bufferedWriter.write(module.getName() + ":" + module.getKey() + "\n");
                bufferedWriter.flush();
            }
            bufferedWriter.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
