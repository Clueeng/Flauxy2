package uwu.flauxy.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.setting.Setting;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Supplier;
public class ModeValue extends Setting {

    private int index = 0;

    protected String[] modes;
    protected String currMode;

    public ModeValue(String name, Module parent, String[] modes, String currMode, Supplier<Boolean> visible) {
        super(name, parent, visible);
        this.modes = modes;
        this.currMode = currMode;
    }

    public ModeValue(String name, Module parent, String[] modes, String currMode) {
        this(name, parent, modes, currMode, () -> true);
    }

    public ModeValue(String name, Module parent, String[] modes) {
        this(name, parent, modes, Arrays.asList(modes).get(0));
    }

    public ModeValue(String name, Module parent, String[] modes, Supplier<Boolean> visible) {
        this(name, parent, modes, Arrays.asList(modes).get(0), visible);
    }

    public final String[] getModes() {
        return modes;
    }
    public final String getCurrMode() {
        return currMode;
    }
    public final void setCurrMode(String currMode) {
        this.currMode = currMode;
    }
    public final void loopNext() {
        if (index + 1 <= modes.length - 1) {
            index++;
            setCurrMode(getModes()[index]);
        } else {
            setCurrMode(getModes()[0]);
            index = 0;
        }
    }
    public final void loopPrev() {
        if (index - 1 != -1) {
            index--;
            setCurrMode(getModes()[index]);
        } else {
            setCurrMode(modes[modes.length - 1]);
            index = modes.length - 1;
        }
    }

    public boolean is(String mode) {
        return getCurrMode().equalsIgnoreCase(mode);
    }

}