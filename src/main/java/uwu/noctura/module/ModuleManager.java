package uwu.noctura.module;

import org.reflections.Reflections;
import uwu.noctura.module.impl.other.util.Folder;
import uwu.noctura.utils.config.KeyLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class ModuleManager {

    public static ArrayList<Module> modules = new ArrayList<Module>();
    private File dataFile;

    public ModuleManager(){
        final Reflections reflections = new Reflections("uwu.noctura.module.impl");
        final Set<Class<? extends Module>> classes = reflections.getSubTypesOf(Module.class);
        for (Class<?> aClass : classes) {
            try {
                final Module mod = (Module) aClass.newInstance();
                modules.add(mod);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {KeyLoader.load(this);} catch (IOException e) {e.printStackTrace();}
    }

    public Module getModule(String name) {
        return modules.stream().filter(module -> module.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public <T extends Module> T getModule(Class<T> tClass) {
        return (T) modules.stream().filter(mod -> mod.getClass().equals(tClass)).findFirst().orElse(null);
    }

    public Module[] getModules(Category category) {
        return modules.stream().filter(module -> module.getCategory() == category).toArray(Module[]::new);
    }
    public Module[] getHudModules(){
        return modules.stream().filter(module -> module.hudMoveable).toArray(Module[]::new);
    }

    private File dir;
    public void saveHudPosition(){
        this.dir = new File(String.valueOf(Folder.dir)); // Assuming Folder.dir is a valid directory path
        if (!this.dir.exists()) {
            this.dir.mkdir();
        }

        String fileName = "hud";
        this.dataFile = new File(this.dir, fileName + ".txt");
        if (dataFile == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for(Module hudMod : getHudModules()){
                int x = (int)hudMod.getMoveX();
                int y = (int)hudMod.getMoveY();
                writer.write(hudMod.getName() + "," + x + "," + y + "\n");
                System.out.println("[SAVING] Saved " + hudMod.getName() + " at " + x + " " + y );
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int[] load(String moduleName) {
        this.dir = new File(String.valueOf(Folder.dir));
        String fileName = "hud";
        this.dataFile = new File(this.dir, fileName + ".txt");

        if (!this.dataFile.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(this.dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 3) {
                    String currentModuleName = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    if (currentModuleName.equalsIgnoreCase(moduleName)) {
                        return new int[]{x, y};
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if the module is not found
    }



}
