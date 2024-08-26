package uwu.flauxy.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.setting.Setting;

import java.util.function.Supplier;

public class NumberSetting extends Setting<Double> {

    @Getter
    public double value;

    @Getter @Setter
    public double minimum;

    @Getter @Setter
    public double maximum;

    @Getter @Setter
    public double increment;

    @Getter @Setter
    public boolean colorDisplay;

    public NumberSetting(String name, double value, Supplier<Boolean> dependency, double minimum, double maximum, double increment) {
        super(name, value, dependency);
        this.name = name;
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public NumberSetting(String name, double value, double minimum, double maximum, double increment) {
        this(name, value, () -> true, minimum, maximum, increment);
        this.name = name;
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }


    public void setValue(double value) {
        double precision = 1.0 / this.increment;
        this.value = Math.round(Math.max(this.minimum, Math.min(this.maximum, value)) * precision) / precision;
    }

}
