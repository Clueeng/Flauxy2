package uwu.noctura.module.setting.impl;


import lombok.Getter;
import uwu.noctura.module.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting<String> {

    public int index;

    @Getter
    public List<String> modes;

    @Getter
    private String selected;

    public ModeSetting(String name, String defaultMode, Supplier<Boolean> dependency, String... modes) {
        super(name, defaultMode, dependency);
        this.name = name;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
        this.selected = this.modes.get(this.index);
    }

    public ModeSetting(String name, String defaultMode, String... modes) {
        this(name, defaultMode, () -> true, modes);
        this.name = name;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
        this.selected = this.modes.get(this.index);
    }

    public void cycle(int mb) {
        if(mb == 0){

            if (this.index < this.modes.size() - 1) {
                ++this.index;
                this.selected = this.modes.get(this.index);
            } else {
                this.index = 0;
            }
            this.selected = this.modes.get(0);
        }
        if(mb == 1){

            if (this.index > 0) {
                --this.index;
                this.selected = this.modes.get(this.index);
            } else {
                this.index = this.modes.size()-1;
            }
            this.selected = this.modes.get(0);
        }
    }

    public boolean is(final String mode) {
        return this.index == this.modes.indexOf(mode);
    }

    public void setSelected(final String selected) {
        this.selected = selected;
        this.index = this.modes.indexOf(selected);
    }

    public String getMode() {
        return this.modes.get(this.index);
    }


}