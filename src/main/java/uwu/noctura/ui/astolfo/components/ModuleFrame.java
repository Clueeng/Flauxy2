package uwu.noctura.ui.astolfo.components;


import net.minecraft.client.gui.GuiScreen;
import uwu.noctura.module.Module;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.BooleanSetting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.ModeSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.ui.astolfo.ColorHelper;
import uwu.noctura.ui.astolfo.components.impl.BooleanComponent;
import uwu.noctura.ui.astolfo.components.impl.GraphComponent;
import uwu.noctura.ui.astolfo.components.impl.ModeComponent;
import uwu.noctura.ui.astolfo.components.impl.NumberComponent;
import uwu.noctura.utils.animtations.Animate;
import uwu.noctura.utils.animtations.Easing;
import uwu.noctura.utils.render.ColorUtils;
import uwu.noctura.utils.render.RenderUtil;

import java.util.ArrayList;

import static uwu.noctura.utils.font.FontManager.getFont;


public class ModuleFrame implements ColorHelper {
    private final Module module;
    private final ArrayList<Component> components;

    private final CategoryFrame owner;

    private final Animate moduleAnimation;

    private int x, y;
    private int offset;

    private boolean opened;

    public ModuleFrame(Module module, CategoryFrame owner, int x, int y)
    {
        this.module = module;
        this.components = new ArrayList<>();
        this.owner = owner;
        this.moduleAnimation = new Animate();
        moduleAnimation.setMin(0).setMax(255).setReversed(!module.isToggled()).setEase(Easing.LINEAR);
        this.opened = false;

        this.x = x;
        this.y = y;

        if(!module.getSettings().isEmpty())
        {
            for (Setting setting : module.getSettings()) {
                if(setting instanceof BooleanSetting)
                {
                    this.components.add(new BooleanComponent(0, 0, this, setting));
                }
                if(setting instanceof ModeSetting)
                {
                    this.components.add(new ModeComponent(0, 0, this, setting));
                }
                if(setting instanceof NumberSetting)
                {
                    this.components.add(new NumberComponent(0, 0, this, setting));
                }
                if(setting instanceof GraphSetting){
                    GraphSetting graph = (GraphSetting)setting;
                    if(graph.hue != null){
                        this.components.add(new GraphComponent(0,0,this, setting, graph.hue));
                    }else{
                        this.components.add(new GraphComponent(0,0,this, setting));
                    }
                }
            }

        }
    }

    public void drawScreen(int mouseX, int mouseY) {
        moduleAnimation.setReversed(!module.isToggled());
        moduleAnimation.setSpeed(1000).update();


        if (RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && hoveredColor) {
            GuiScreen.drawRect(x, y, x + defaultWidth, y + moduleHeight, darkerMainColor);

        }





        if(module.isToggled() || (moduleAnimation.isReversed() && moduleAnimation.getValue() != 0)) {
            RenderUtil.drawRoundedRect2(x,y, x + defaultWidth, y + moduleHeight, 0, ColorUtils.setAlpha(module.getCategory().getCategoryColor(), 255).getRGB());

        }

        // (x + (defaultWidth / 2) - (getFont().getWidth(getSetting().name.toLowerCase()))
        getFont().drawString(module.getName().toLowerCase(), (float) (x + defaultWidth - (getFont().getWidth(module.getName().toLowerCase()))), (float) (y + (moduleHeight / 2F - (getFont().getHeight("A") / 2F)) - 3.8 + 2.5), stringColor);


        int offset = 0;

        if(opened) {
            for (Component component : this.components) {
                if(!component.getSetting().getCanShow().test(this.module)) continue;
                component.setX(x);
                component.setY(y + moduleHeight + offset - 7);

                component.drawScreen(mouseX, mouseY);

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset + 90);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(RenderUtil.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && RenderUtil.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()) && !(mouseY < owner.getY() + 16))
        {
            switch(mouseButton)
            {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    opened = !opened;
                    break;
                case 2:
                    //TODO: Bind
                    break;
            }
            return true;
        }

        if(RenderUtil.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()) && opened) {
            for (Component component : this.components) {
                if(!isHidden(component.getSetting()) && component.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;



                if(component.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;
            }
        }

        return false;
    }

    public Module getParent(){
        return module;
    }

    public int getOffset() {
        offset = 0;
        if(opened) {
            for (Component component : this.components) { // using for loop because continue isn't supported on foreach
               if (isHidden(component.getSetting()))
                   continue;

                offset += component.getOffset() + 1;
            }
        }

        this.setOffset(moduleHeight + offset);
        return offset;
    }

    public boolean isHidden(Setting c) {
        if (c instanceof BooleanSetting &&
                ((BooleanSetting) c).parent != null)
            if (((BooleanSetting) c).parent instanceof BooleanSetting) {
                if (!((BooleanSetting) ((BooleanSetting) c).parent).isEnabled())
                    return true;
            } else if (((BooleanSetting) c).parent instanceof ModeSetting &&
                    !((ModeSetting) ((BooleanSetting) c).parent).getMode().equals(((BooleanSetting) c).parentMode)) {
                return true;
            }
        if (c instanceof ModeSetting &&
                ((ModeSetting) c).parent != null &&
                ((ModeSetting) c).parent instanceof ModeSetting &&
                !((ModeSetting) ((ModeSetting) c).parent).getMode().equals(((ModeSetting) c).parentMode)) {
            return true;
        }
        if (c instanceof NumberSetting &&
                ((NumberSetting) c).parent != null) {
            Setting set = ((NumberSetting) c).parent;
            if (set.parent != null &&
                    set.parent instanceof ModeSetting &&
                    !((ModeSetting) set.parent).getMode().equals(set.parentMode))
                return true;
            if (set instanceof ModeSetting &&
                    !((ModeSetting) set).getMode().equals(((NumberSetting) c).parentMode))
                return true;
        }
        return false;
    }


    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
