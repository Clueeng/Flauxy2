package uwu.noctura.ui.box.comp.impl;

import lombok.Setter;
import uwu.noctura.module.setting.Setting;

public abstract class Component
{
    private final ModuleSettingFrame owner;
    private final Setting setting;
    @Setter
    protected float x, y;
    protected int compWidth, compHeight;


    public Component(int x, int y, ModuleSettingFrame owner, Setting setting)  {
        this.owner = owner;
        this.setting = setting;
        this.x = x;
        this.y = y;
    }

    public abstract void initGui();
    public abstract void drawScreen(int mouseX, int mouseY);
    public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton); // Return a boolean to know if we cancel the drag
    public abstract boolean mouseReleased(int mouseX, int mouseY, int state);
    public abstract void onGuiClosed(int mouseX, int mouseY, int mouseButton);
    public abstract void keyTyped(char typedChar, int keyCode);

    public abstract int getOffset(); // Return offset

    public Setting getSetting() {
        return setting;
    }
}
