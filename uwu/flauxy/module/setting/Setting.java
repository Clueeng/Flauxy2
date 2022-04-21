package uwu.flauxy.module.setting;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.Module;

import java.util.function.Supplier;

@Getter
@Setter
public class Setting {

    private final String name;
    private Supplier<Boolean> visible;


    public Setting(String name, Module parent, Supplier<Boolean> visible) {
        this.name = name;
        this.visible = visible;
        parent.addSetting(this);
    }

    public Setting(String name, Module parent) {
        this(name, parent, () -> true);
    }

}