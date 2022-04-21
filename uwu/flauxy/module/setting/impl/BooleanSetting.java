package uwu.flauxy.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.setting.Setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {

    @Getter @Setter
    public boolean enabled;

    public BooleanSetting(String name, boolean enabled, Supplier<Boolean> dependancy) {
        super(name, enabled, dependancy);
        this.name = name;
        this.enabled = enabled;
    }

    public BooleanSetting(String name, boolean enabled) {
        this(name, enabled, () -> true);
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }


}
