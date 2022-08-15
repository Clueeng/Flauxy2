package uwu.flauxy.ui.astolfo;

import javafx.scene.transform.Scale;
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
        // Creating category instance foreach listed category
        for(Category category : Category.values()) {
            categories.add(new CategoryFrame(category, category.id > 7 ? 10 : 10 + (++index * (125 + 10)), category.id > 7 ? height + 280 : 10));
        }
        for(Config config : Flauxy.INSTANCE.getNonShittyConfigManager().getConfigs()){
            //non shitty code run here :D
        }
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
