package uwu.noctura.module.impl.display;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import uwu.noctura.Noctura;
import uwu.noctura.event.Event;
import uwu.noctura.event.impl.EventRender2D;
import uwu.noctura.module.Category;
import uwu.noctura.module.Module;
import uwu.noctura.module.ModuleInfo;
import uwu.noctura.module.setting.impl.NumberSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@ModuleInfo(name = "InventoryDisplay", displayName = "Inventory Display", key = -1, cat = Category.Display)
public class InventoryDisplay extends Module {

    public NumberSetting size = new NumberSetting("Size", 100, 10, 100, 5);

    public InventoryDisplay(){
        setHudMoveable(true);
        setMoveX(100);
        setMoveY(120);
        setMoveW(120);
        setMoveH(120);
    }

    float lerpX, lerpY, lerpW, lerpH;

    public void onEvent(Event e){
        if(e instanceof EventRender2D){
            ArrayList<ItemStack> inventoryItems = new ArrayList<>();
            for(int i = 0; i < mc.thePlayer.inventoryContainer.getInventory().size(); i++){
                ItemStack item = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                inventoryItems.add(item);
            }
            int squareSize = 24;
            float x = getMoveX();
            float y = getMoveY();
            float width = 9 * squareSize;
            float height = 4 * squareSize;
            setMoveH(height);
            setMoveW(width);

            // background
            Gui.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 90).getRGB());


            // the list is reverse order, 1st item in hotbar is last in list

            // drawing items
            int columns = 9;
            float xStart = getMoveX() + 4;
            float yStart = getMoveY();
            for (int i = 0; i < inventoryItems.size(); i++) {
                ItemStack itemStack = inventoryItems.get(i);
                if (itemStack != null) {
                    int row = i / columns;
                    int col = i % columns;

                    int xPos = (int) (xStart + (col * squareSize));
                    int yPos = (int) (yStart + (row * squareSize));
                    renderItem(itemStack, xPos, yPos - squareSize + 4);
                }
            }
        }
    }


    private void renderItem(ItemStack itemStack, int xPos, int yPos) {
        if (itemStack != null) {
            mc.getRenderItem().renderItemIntoGUINoctura(itemStack, xPos, yPos);
            //mc.getRenderItem().renderItemOverlayIntoGUIButBetter(mc.fontRendererObj, itemStack, xPos, yPos);
            GlStateManager.resetColor();
            //Noctura.INSTANCE.getFontManager().getFont("Good 16").drawStringWithShadow(itemStack.stackSize + "", xPos + 7, yPos + 7, -1);
            GlStateManager.resetColor();
        }
    }

}
