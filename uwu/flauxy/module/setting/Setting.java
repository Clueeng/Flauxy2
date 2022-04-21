package uwu.flauxy.module.setting;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Setting<T> {

    private Supplier<Boolean> dependency;
    private List<ValueChangeListener<T>> valueChangeListeners;
    private T value;

    @Getter
    public String name;

    @Getter
    public String parentMode;

    @Getter @Setter
    public String description;

    @Getter
    public Setting parent;


    public Setting(String label, T value, Supplier<Boolean> dependency) {
        this.valueChangeListeners = new ArrayList<ValueChangeListener<T>>();
        this.name = label;
        this.value = value;
        this.dependency = dependency;
    }

    public Setting parent(Setting set) {
        this.parent = set;
        return this;
    }

    public Setting mode(String mode) {
        this.parentMode = mode;
        return this;
    }

    public boolean isAvailable() {
        return this.dependency.get();
    }




}
