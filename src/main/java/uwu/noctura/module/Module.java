package uwu.noctura.module;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.utils.Methods;
import uwu.noctura.utils.WorldUtil;

import java.awt.*;
import java.util.ArrayList;


public class Module implements Methods {

    @Getter @Setter
    private boolean settingsShown = true;

    @Getter @Setter
    public boolean hudMoveable = false;
    @Getter @Setter
    public float moveX, moveY, moveW, moveH;

    @Getter @Setter
    protected String name, displayName;

    @Getter @Setter
    protected int key;

    @Getter @Setter
    protected Category category;

    @Getter @Setter
    protected boolean toggled;

    @Getter
    protected Minecraft mc = Minecraft.getMinecraft();

    @Getter
    public ArrayList<Setting> settings = new ArrayList<>();

    public float xSlide = 0f, ySlide = 0f, alpha = 1f;
    public float animation = 0;
    public float animationNow = 0;

    public Module(){
        final ModuleInfo featureInfo = getClass().getAnnotation(ModuleInfo.class);

        this.name = featureInfo.name();
        this.displayName = featureInfo.displayName();
        this.key = featureInfo.key();
        this.category = featureInfo.cat();
    }
    public Module(String name, String displayName, int key, Category cat){
        this.name = name;
        this.displayName = displayName;
        this.key = key;
        this.category = cat;
    }

    public void onEnable() {
    }

    public void onDisable() {

    }

    public void toggle(){
        toggled = !toggled;
        if(Noctura.INSTANCE.isGhost() && (this.category != Category.Display && this.category != Category.Visuals && this.category != Category.Ghost)) return;
        if(toggled) onEnable();
        else onDisable();
    }
    public void onEvent(Event e){
        if(WorldUtil.shouldNotRun()) return;
    }
    public void onEventIgnore(Event e){}

    public void addSettings(final Setting... settings) {
        for (final Setting setting : settings) {
            this.getSettings().add(setting);
        }
    }

    public Color getColorFromSettings(NumberSetting hue, GraphSetting saturationValue){
        return Color.getHSBColor((float)hue.getValue() / 360f,(float)saturationValue.getX() / 100f,(float)saturationValue.getY() / 100f);
    }

    public void addSetting(final Setting setting){
        this.getSettings().add(setting);
    }


}
