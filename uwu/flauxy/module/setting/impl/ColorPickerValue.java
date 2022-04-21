package uwu.flauxy.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.setting.Setting;

import java.awt.*;
import java.util.function.Supplier;

@Getter
@Setter
public class ColorPickerValue extends Setting {

    private String value;
    public Color ColorPickerC;
    public ColorPickerValue(String name, Module parent, Color color) {

        super(name, parent);
        this.ColorPickerC = color;
    }
    public ColorPickerValue(String name, Module parent, Color color, Supplier<Boolean> visible) {
        super(name, parent, visible);
        this.ColorPickerC = color;
    }
}