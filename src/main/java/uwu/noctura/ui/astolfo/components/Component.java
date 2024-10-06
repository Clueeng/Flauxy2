package uwu.noctura.ui.astolfo.components;


import uwu.noctura.module.setting.Setting;

public abstract class Component
{
    private final ModuleFrame owner;
    private final Setting setting;
    protected int x, y;


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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Setting getSetting() {
        return setting;
    }
}
