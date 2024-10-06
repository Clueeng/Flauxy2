package uwu.noctura.module.setting;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import uwu.noctura.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Accessors(chain = true)
public class Setting<T> {

    private Supplier<Boolean> dependency;
    private List<ValueChangeListener<T>> valueChangeListeners;
    private T value;
    public float animation = 0;
    public float animationNow = 0;

    @Getter
    public String name;

    @Getter
    public String parentMode;

    @Getter @Setter
    public String description;

    @Getter
    public Setting parent;

    @Getter @Setter
    public boolean shown = true;

    @Getter
    private Predicate<Module> canShow = (module) -> true;

    public <E> E setCanShow(Predicate<Module> predicate) {
        this.canShow = predicate;
        return (E) this;
    }

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
