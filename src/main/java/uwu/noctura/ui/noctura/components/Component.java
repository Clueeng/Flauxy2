package uwu.noctura.ui.noctura.components;


import lombok.Setter;
import uwu.noctura.module.setting.Setting;

public abstract class Component
{
    private final ModuleFrame owner;
    private final Setting setting;
    @Setter
    protected float x, y;


    public Component(int x, int y, ModuleFrame owner, Setting setting)  {
        this.owner = owner;
        this.setting = setting;
        this.x = x;
        this.y = y;
    }

    public abstract void initGui();
    public abstract void drawScreen(int mouseX, int mouseY);
    public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton); // Return a boolean to know if we cancel the drag
    public abstract void onGuiClosed(int mouseX, int mouseY, int mouseButton);
    public abstract void keyTyped(char typedChar, int keyCode);

    public abstract int getOffset(); // Return offset

    public Setting getSetting() {
        return setting;
    }
}
