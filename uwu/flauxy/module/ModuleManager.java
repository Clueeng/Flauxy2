package uwu.flauxy.module;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

public class ModuleManager {

    public static ArrayList<Module> modules = new ArrayList<Module>();

    public ModuleManager(){
        final Reflections reflections = new Reflections("uwu.flauxy.module.impl");
        final Set<Class<? extends Module>> classes = reflections.getSubTypesOf(Module.class);
        for (Class<?> aClass : classes) {
            try {
                final Module mod = (Module) aClass.newInstance();
                modules.add(mod);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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



}
