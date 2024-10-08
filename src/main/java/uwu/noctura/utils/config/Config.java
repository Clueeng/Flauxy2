package uwu.noctura.utils.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uwu.noctura.utils.MinecraftInstance;

import java.nio.file.Files;
import java.nio.file.Paths;

@Getter@AllArgsConstructor
public class Config implements MinecraftInstance {
    private final String configName;


    public boolean save() {
        return true;
    }

    public boolean load() {
        return true;
    }

    public boolean delete() {
        try {
            Files.delete(Paths.get(mc.mcDataDir.getAbsolutePath() + "configs" + "/" + configName + ".json"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
