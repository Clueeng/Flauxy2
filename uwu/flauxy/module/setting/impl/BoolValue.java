package uwu.flauxy.module.setting.impl;

import uwu.flauxy.module.Module;
import uwu.flauxy.module.setting.Setting;

import java.util.function.Supplier;
public class BoolValue extends Setting {

    protected boolean toggled;

    public BoolValue(String name, Module parent, boolean toggled, Supplier<Boolean> visible) {
        super(name, parent, visible);
        this.toggled = toggled;
    }



    public BoolValue(String name, Module parent, boolean toggled) {
        this(name, parent, toggled, () -> true);
    }

    public final boolean get() {
        return toggled;
    }
    public final void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
    public final void toggle() {
        toggled = !toggled;
    }

}