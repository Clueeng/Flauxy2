package uwu.noctura.ui.astolfo.components.impl;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.module.Category;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.ui.astolfo.components.Component;
import uwu.noctura.ui.astolfo.components.ModuleFrame;
import uwu.noctura.ui.astolfo.ColorHelper;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static uwu.noctura.utils.font.FontManager.getFont;
import static uwu.noctura.utils.render.RenderUtil.drawGradientRect;
import static uwu.noctura.utils.render.RenderUtil.drawGradientSideways;

public class GraphComponent extends Component implements ColorHelper {
    private ModuleFrame owner;
    private boolean dragX, dragY;
    NumberSetting hue;

    public GraphComponent(int x, int y, ModuleFrame owner, Setting setting) {
        super(x, y, owner, setting);
        this.owner = owner;
    }
    public GraphComponent(int x, int y, ModuleFrame owner, Setting setting, NumberSetting hue) {
        super(x, y, owner, setting);
        this.owner = owner;
        this.hue = hue;
    }

    float lerpToY = 0, posY = 0;
    float lerpToX = 0, posX = 0;

    @Override
    public void initGui() {
        dragX = dragY = false;
    }

    public Color getColorFromSettings(NumberSetting hue, GraphSetting saturationValue){
        return Color.getHSBColor((float)hue.getValue() / 360f,(float)saturationValue.getX() / 100f,(float)saturationValue.getY() / 100f);
    }
    public void setColorFromSettings(Color c, NumberSetting hue, GraphSetting saturationValue) {
        float[] hsbValues = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float hueValue = hsbValues[0] * 360f;
        float saturationValuePercentage = hsbValues[1] * 100f;
        float brightnessValuePercentage = hsbValues[2] * 100f;
        hue.setValue(hueValue);
        saturationValue.setX(saturationValuePercentage);
        saturationValue.setY(brightnessValuePercentage);
    }

    public void copyColor(){
        Noctura.INSTANCE.copiedColor = getColorFromSettings(hue, (GraphSetting) this.getSetting());
        Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Color Setting", "Copied color, press V to paste it elsewhere"));
    }

    public void pasteColor(){
        Color copied = Noctura.INSTANCE.getCopiedColor();
        if(copied == null){
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Color Setting", "Copy a color first before pasting one"));
        }else{
            float[] hsbValues = Color.RGBtoHSB(copied.getRed(), copied.getGreen(), copied.getBlue(), null);
            float hueValue = hsbValues[0] * 360f;
            float saturationValuePercentage = hsbValues[1] * 100f;
            float brightnessValuePercentage = hsbValues[2] * 100f;
            hue.setValue(hueValue);
            ((GraphSetting) this.getSetting()).setX(saturationValuePercentage);
            ((GraphSetting) this.getSetting()).setY(brightnessValuePercentage);
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Color Setting", "Pasted color"));
        }
    }
    boolean stateCopy, statePaste;
    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (!Mouse.isButtonDown(0)) {
            dragX = dragY = false;
        }

        if(hue != null && RenderUtil.hover(x, y + 20, mouseX, mouseY, defaultWidth, getOffset() - 20)){
            boolean heldCtrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
            boolean heldCopy = Keyboard.isKeyDown(Keyboard.KEY_C) && heldCtrl;
            boolean heldPaste = Keyboard.isKeyDown(Keyboard.KEY_V) && heldCtrl;

            if(heldCopy && !stateCopy){
                copyColor();
            }
            if(heldPaste && !statePaste) {
                pasteColor();
            }
            stateCopy = heldCopy;
            statePaste = heldPaste;
        }

        GraphSetting graph = (GraphSetting) getSetting();
        boolean isColor = graph.isColorDisplay();
        float minX = graph.getMinX();
        float maxX = graph.getMaxX();
        float minY = graph.getMinY();
        float maxY = graph.getMaxY();

        float incrementX = graph.getIncrementX();
        float incrementY = graph.getIncrementY();

        // Constrain the mouse coordinates within the graph's boundaries
        float diffX = (float) Math.min(defaultWidth, Math.max(0, mouseX - (this.x)));
        float diffY = (float) Math.min(getOffset() - 20, Math.max(0, mouseY - (this.y + 20)));

        float renderWidthX = (float) (defaultWidth * (graph.getX() - minX) / (maxX - minX));
        float renderHeightY = (float) ((getOffset() - 20) * (graph.getY() - minY) / (maxY - minY));

        // Draw background and graph area
        Gui.drawRect(x, y + 20, x + defaultWidth, y + getOffset() - 10 + 5 + 8, new Color(125, 125, 125).getRGB());

        if (isColor && hue != null) {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            Gui.drawRect(x, y + 20, x + defaultWidth, y + getOffset() - 10 + 5 + 8, Color.getHSBColor((float) hue.getValue() / 360f,1,1).getRGB());

            // 2. sat overlay

            drawGradientSideways(x, y + 20, x + defaultWidth, y + getOffset() - 10 + 5 + 8, new Color(255, 255, 255, 255).getRGB(), new Color(255, 255, 255, 0).getRGB());

            // 3. bright overlay
            drawGradientRect(x, y + 20, x + defaultWidth, y + getOffset() - 10 + 5 + 8,
                    new Color(0, 0, 0, 255).getRGB(),
                    new Color(0, 0, 0, 0).getRGB()
            );
            GL11.glDisable(GL11.GL_ALPHA_TEST);

        } else {
            Gui.drawRect(x + 1, y + 21, x + defaultWidth - 1, y + getOffset() - 10 + 5 + 8 - 1, new Color(55, 55, 55).getRGB());
            for (float i = minX; i <= maxX; i += incrementX) {
                float gridX = x + (i - minX) / (maxX - minX) * defaultWidth;
                Gui.drawRect((int) gridX, y + 20, (int) gridX + 1, y + getOffset() - 10 + 5 + 8, new Color(200, 200, 200, 35).getRGB());
            }

            // Draw grid lines for Y (Brightness) axis
            for (float i = minY; i <= maxY; i += incrementY) {
                float relativePosition = (i - minY) / (maxY - minY);  // Relative position in the range [0, 1]
                int gridY = y + 20 + Math.round(relativePosition * (getOffset() - 20)); // Use Math.round for pixel-perfect alignment
                Gui.drawRect(x, gridY, x + defaultWidth, gridY + 1, new Color(200, 200, 200, 35).getRGB());
            }

        }

        lerpToX = x + (int) renderWidthX;
        lerpToY = y + 20 + (int) (getOffset() - 20 - renderHeightY);
        posX = (float) MathHelper.lerp(0.1, posX, lerpToX);
        posY = (float) MathHelper.lerp(0.1, posY, lerpToY);

        // Draw the selector circle in the current SB position
        RenderUtil.drawCircle(posX, posY, 2, owner.getParent().getCategory().equals(Category.Ghost) ? new Color(255,0,0).getRGB()        : owner.getParent().getCategory().getCategoryColor().getRGB());

        // Draw borders
        Gui.drawRect(x, y + 18, x + defaultWidth, y + 20, new Color(22, 22, 22).getRGB());
        Gui.drawRect(x, y + getOffset() + 3, x + defaultWidth, y + getOffset() + 5, new Color(22, 22, 22).getRGB());

        if (dragX) {
            float newX = roundToPlace((diffX / defaultWidth) * (maxX - minX) + minX, 2);
            if (newX <= maxX) {
                graph.setX(newX);  // This will set Saturation
            }
        }

        if (dragY) {
            float newY = roundToPlace(((getOffset() - 20 - diffY) / (getOffset() - 20)) * (maxY - minY) + minY, 2);
            if (newY <= maxY) {
                graph.setY(newY);  // This will set Brightness
            }
        }
        getFont().drawString(
                getSetting().name.toLowerCase() + ": [" + roundToPlace((float) graph.getX(), 2) + ", " + roundToPlace((float) graph.getY(), 2) + "]",
                (float) (x + 5),
                (float) (y + 8),
                stringColor
        );
        // Draw setting label with current Saturation and Brightness values
        if(!isColor){


            // Draw X and Y axis labels (Saturation and Brightness)
            getFont().drawString("S: " + String.valueOf((int)graph.getMinX()), x + 2, y + getOffset() - 8, new Color(255, 255, 255, 120).getRGB());
            getFont().drawString("S: " + String.valueOf((int)graph.getMaxX()), x + defaultWidth - getFont().getWidth(String.valueOf((int)graph.getMaxX())), y + getOffset() - 8, new Color(255, 255, 255, 120).getRGB());
            getFont().drawString("B: " + String.valueOf((int)graph.getMaxY()), x + 1, y + 21, new Color(255, 255, 255, 120).getRGB());

        }
        GlStateManager.resetColor();
    }

    private float roundToPlace(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private float snapToStep(float value, float valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * Math.round(value / valueStep);
        return value;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.hover(x, y + 20, mouseX, mouseY, defaultWidth, getOffset() - 20) && mouseButton == 0) {
            dragX = dragY = true;
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public int getOffset() {
        return this.getSetting().getCanShow().test(null) ? 100 : 0;
    }
}
