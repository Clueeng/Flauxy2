package uwu.flauxy.ui.astolfo;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import uwu.flauxy.Flauxy;
import uwu.flauxy.module.Category;
import uwu.flauxy.ui.astolfo.components.CategoryFrame;
import uwu.flauxy.utils.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {
    private final List<CategoryFrame> categories;

    public ClickGUI()
    {
        this.categories = new ArrayList<>();

        int index = -1;
        for(Category category : Category.values()) {
            if(!category.equals(Category.Macro)){
                index++;
                categories.add(new CategoryFrame(category, category.id > 6 ? 10 + ((index-6) * (125 + 10)) : 10 + (index * (125 + 10)), category.id > 6 ? height + 280 : 10));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void initGui()// plez dont look discord clickgui cuz it is aids
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
