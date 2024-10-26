package uwu.noctura.ui.box.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import uwu.noctura.Noctura;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.ui.box.BoxGUI;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.render.RenderUtil;

import java.util.ArrayList;

public class CategoryWindow {

    Minecraft mc = Minecraft.getMinecraft();

    public Category category;
    public int x, y;
    public int height = 20;
    public int width = 120;
    public BoxGUI parent;

    ArrayList<ModuleFrame> modules = new ArrayList<>();

    public CategoryWindow(Category category, int x, int y, BoxGUI parentGui){
        this.category = category;
        this.x = x;
        this.y = y;
        this.parent = parentGui;


        int modX = 0;
        int modY = y;
        for (Module module : Noctura.INSTANCE.getModuleManager().getModules(category)) {
            ModuleFrame modFrame = new ModuleFrame(module, this, 12 + modX + parentGui.x, y + 0 + modY, parentGui);
            this.modules.add(modFrame);
            modX += modFrame.w + 10;
            System.out.println(modX + " / " + parentGui.guiWidth + " : " + modFrame.module.getName());
            if(modX > parentGui.guiWidth - modFrame.w){
                modX = 0;
                modY += modFrame.h + 4;
            }
        }
    }

    public void drawCategoryFrame(int mouseX, int mouseY){
        ScaledResolution sr = new ScaledResolution(mc);
        // modules logic

        for(ModuleFrame frame : modules){
            frame.drawScreen(mouseX, mouseY);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton){
        for (ModuleFrame moduleFrame : modules) {
            //if(moduleFrame.categoryWindow.category.equals(parent.currentCategory)){
                //moduleFrame.module.toggle();
                //Wrapper.instance.log(moduleFrame.getModule().getName());
            //}
            moduleFrame.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if(RenderUtil.hover(x, y, mouseX, mouseY, width, height)){

            return true;
        }
       return false;
    }

}
