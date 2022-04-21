package uwu.flauxy.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.setting.Setting;

import java.awt.*;
import java.util.function.Supplier;
public class NumberValue extends Setting {

    protected double min;
    protected double max;
    protected double current;
    protected double increment;

    public NumberValue(String name, Module parent, double min, double max, double current, double increment, Supplier<Boolean> visible) {
        super(name, parent, visible);
        this.min = min;
        this.max = max;
        this.current = current;
        this.increment = increment;
    }

    public NumberValue(String name, Module parent, double min, double max, double current, double increment) {
        this(name, parent, min, max, current, Math.round(current * increment) / increment, () -> true);
    }


    public final double getMin() {
        return min;
    }
    public final double getMax() {
        return max;
    }
    public final int getInt() {
        return (int)current;
    }
    public final double getDouble() {
        return current;
    }
    public final long getLong() {
        return (long) current;
    }
    public final float getFloat() {
        return (float) current;
    }
    public final void setCurrent(double current) {
        this.current = current;
    }
    public final double getIncrement() {
        return increment;
    }


}