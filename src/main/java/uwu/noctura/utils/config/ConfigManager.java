package uwu.noctura.utils.config;

import uwu.noctura.Noctura;
import uwu.noctura.utils.MinecraftInstance;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ConfigManager implements MinecraftInstance {

    private final Path configDirectory = Paths.get(mc.mcDataDir.getAbsolutePath(), "configs");
    private final LinkedHashMap<Config, File> configs = new LinkedHashMap<>(); // what does that mean
    private Config currentConfig;

    public void init(){
        configs.clear();
        try{
            Files.list(configDirectory).map(Path::toFile).forEach(config -> configs.put(new Config(config.getName().replace(".json", "")), config));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean load(Config config) {
        if (config != null)
            if (config.load()) {
                currentConfig = config;
                return true;
            }
        return false;
    }

    public boolean add(Config config) {
        configs.put(config, Paths.get(Noctura.INSTANCE.getConfigsDirectory().toFile().getAbsolutePath(), config.getConfigName()).toFile());
        return config.save();
    }

    public boolean remove(Config config) {
        configs.remove(config);
        return config.delete();
    }

    public boolean save(Config config) {
        if (config != null)
            return config.save();
        return false;
    }

    public boolean load(String name) {
        return load(getConfigByName(name));
    }

    public boolean save(String name) {
        return save(getConfigByName(name));
    }

    public Config getConfigByName(String name) {
        return configs.keySet().stream().filter(config -> config.getConfigName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Config> getConfigs(){
        return new ArrayList<>(this.configs.keySet());
    }
}
