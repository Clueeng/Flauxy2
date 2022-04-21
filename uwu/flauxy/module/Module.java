package uwu.flauxy.module;

import com.darkmagician6.eventapi.EventManager;
import lombok.Getter;
import lombok.Setter;
import uwu.flauxy.module.setting.Setting;

import java.util.ArrayList;


public class Module {
    @Getter@Setter
    protected String name, displayName;
    @Getter@Setter
    protected int key;
    @Getter@Setter
    protected Category category;
    @Getter@Setter
    protected boolean toggled;

    @Getter
    @Setter
    private ArrayList<Setting> settings = new ArrayList<>();
    public Module(){
        final ModuleInfo featureInfo = getClass().getAnnotation(ModuleInfo.class);

        this.name = featureInfo.name();
        this.displayName = featureInfo.displayName();
        this.key = featureInfo.key();
        this.category = featureInfo.cat();
    }

    public void onEnable() {
        EventManager.register(this);
    }

    public void onDisable() {
        EventManager.unregister(this);
    }

    public void toggle(){
        toggled = !toggled;
        if(toggled) onEnable();
        else onDisable();
    }

    public final void addSetting(Setting setting) {
        settings.add(setting);
    }


}
