package uwu.noctura.ui.dropdown;

import net.minecraft.client.gui.GuiScreen;
import uwu.noctura.module.Category;
import uwu.noctura.ui.dropdown.components.CategoryFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {
    private final List<CategoryFrame> categories;

    public ClickGUI()
    {
        this.categories = new ArrayList<>();

        int index = -1;
        // Creating category instance foreach listed category
        for(Category category : Category.values())
        {
            if(category.id >= 7){
                if(category.id == 7) index = -1;
                categories.add(new CategoryFrame(category, 10 + (++index * (125 + 10)), height + 280));
            }else{
                categories.add(new CategoryFrame(category, 10 + (++index * (125 + 10)), 10));
            }

        }
    }

    @Override
    public void initGui()
    {
        categories.forEach(CategoryFrame::initGui);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        categories.forEach(frameCategory -> frameCategory.drawScreen(mouseX, mouseY));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        categories.forEach(frameCategory -> frameCategory.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        categories.forEach(frameCategory -> frameCategory.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
