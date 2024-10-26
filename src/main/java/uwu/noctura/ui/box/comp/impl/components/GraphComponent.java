package uwu.noctura.ui.box.comp.impl.components;


import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.module.setting.Setting;
import uwu.noctura.module.setting.impl.GraphSetting;
import uwu.noctura.module.setting.impl.NumberSetting;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.ui.box.comp.ModuleFrame;
import uwu.noctura.ui.box.comp.impl.Component;
import uwu.noctura.ui.box.comp.impl.ModuleSettingFrame;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.font.FontManager;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static uwu.noctura.utils.MoveUtils.mc;
import static uwu.noctura.utils.render.RenderUtil.drawGradientRect;
import static uwu.noctura.utils.render.RenderUtil.drawGradientSideways;

public class GraphComponent extends Component {
    public ModuleSettingFrame owner;
    public boolean dragX, dragY;
    public NumberSetting hue;

    public GraphComponent(int x, int y, ModuleSettingFrame owner, Setting setting, NumberSetting hue) {
        super(x, y, owner, setting);
        this.owner = owner;
        this.hue = hue;
        compWidth = 130;
        compHeight = 130;
    }
    public GraphComponent(int x, int y, ModuleSettingFrame owner, Setting setting) {
        super(x, y, owner, setting);
        this.owner = owner;
        compWidth = 130;
        compHeight = 130;
    }

    float lerpToY = 0, posY = 0;
    float lerpToX = 0, posX = 0;

    @Override
    public void initGui() {
        dragX = dragY = false;
    }

    public Color getColorFromSettings(NumberSetting hue, GraphSetting saturationValue) {
        return Color.getHSBColor((float) hue.getValue() / 360f, (float) saturationValue.getX() / 100f, (float) saturationValue.getY() / 100f);
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

    public void copyColor() {
        Noctura.INSTANCE.copiedColor = getColorFromSettings(hue, (GraphSetting) this.getSetting());
        Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Color Setting", "Copied color, press V to paste it elsewhere"));
    }

    public void pasteColor() {
        Color copied = Noctura.INSTANCE.getCopiedColor();
        if (copied == null) {
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Color Setting", "Copy a color first before pasting one"));
        } else {
            setColorFromSettings(copied, hue, (GraphSetting) this.getSetting());
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Color Setting", "Pasted color"));
        }
    }

    private void handleMouseDragging(int mouseX, int mouseY, GraphSetting graph) {
        float diffX = (float) Math.min(compWidth, Math.max(0, mouseX - this.x));
        float diffY = (float) Math.min(getOffset() - 20, Math.max(0, mouseY - (this.y + 20)));

        if (dragX) {
            float newX = roundToPlace((diffX / compWidth) * (graph.getMaxX() - graph.getMinX()) + graph.getMinX(), 2);
            if (newX <= graph.getMaxX()) graph.setX(newX);
        }

        if (dragY) {
            float newY = roundToPlace(((getOffset() - 20 - diffY) / (getOffset() - 20)) * (graph.getMaxY() - graph.getMinY()) + graph.getMinY(), 2);
            if (newY <= graph.getMaxY()) graph.setY(newY);
        }
    }

    private float roundToPlace(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state) {
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
        return compHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        int adjustedY = (int) (y - 5); // Adjusted y position by 5 pixels up
        TTFFontRenderer title = FontManager.getFont("Poppins", 19);

        if (!Mouse.isButtonDown(0)) {
            dragX = dragY = false;
        }
        if (hue != null && RenderUtil.hover(x, adjustedY + 20, mouseX, mouseY, compWidth, getOffset() - 20)) {
            boolean heldCtrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
            boolean heldCopy = Keyboard.isKeyDown(Keyboard.KEY_C) && heldCtrl;
            boolean heldPaste = Keyboard.isKeyDown(Keyboard.KEY_V) && heldCtrl;

            if (heldCopy && (Noctura.INSTANCE.copiedColor.getRGB() != getColorFromSettings(hue, (GraphSetting) this.getSetting()).getRGB())) copyColor();
            if (heldPaste && (Noctura.INSTANCE.copiedColor.getRGB() != getColorFromSettings(hue, (GraphSetting) this.getSetting()).getRGB())) pasteColor();
        }

        GraphSetting graph = (GraphSetting) getSetting();
        float renderWidthX = (float) (compWidth * (graph.getX() - graph.getMinX()) / (graph.getMaxX() - graph.getMinX()));
        float renderHeightY = (float) ((getOffset() - 20) * (graph.getY() - graph.getMinY()) / (graph.getMaxY() - graph.getMinY()));


        boolean isColor = graph.isColorDisplay();

        if (isColor && hue != null) {
            //GL11.glEnable(GL11.GL_ALPHA_TEST);
            Gui.drawRect(x, y + 13, x + compWidth, y + getOffset() - 6, Color.getHSBColor((float) hue.getValue() / 360f,1,1).getRGB());

            // 2. sat overlay

            drawGradientSideways(x, y + 13, x + compWidth, y + getOffset() - 6, new Color(255, 255, 255, 255).getRGB(), new Color(255, 255, 255, 0).getRGB());

            // 3. bright overlay
            drawGradientRect(x, y, x + compWidth, y + getOffset() - 6,
                    new Color(0, 0, 0, 255).getRGB(),
                    new Color(0, 0, 0, 0).getRGB()
            );
            //GL11.glDisable(GL11.GL_ALPHA_TEST);
            drawColorCursor(renderWidthX, renderHeightY, adjustedY-2);
        }else{
            drawGraphBackground(adjustedY);
            drawGraphForeground(graph, renderWidthX, renderHeightY, adjustedY);
        }

        handleMouseDragging(mouseX, mouseY, graph);

        title.drawStringWithShadow(
                String.format("%s: [%.2f, %.2f]", getSetting().name.toLowerCase(), graph.getX(), graph.getY()),
                (int) (x + 0), (int) (adjustedY + 6), -1
        );
        GlStateManager.resetColor();
    }

    private void drawGraphBackground(int adjustedY) {
        Gui.drawRect(x, adjustedY + 20, x + compWidth, adjustedY + getOffset() - 10 + 5 + 8, new Color(125, 125, 125).getRGB());
    }

    private void drawColorCursor(float rx, float ry, int adjY){

        lerpToX = x + (int) rx;
        lerpToY = adjY + 20 + (int) (getOffset() - 20 - ry);
        posX = (float) MathHelper.lerp(0.1, posX, lerpToX);
        posY = (float) MathHelper.lerp(0.1, posY, lerpToY);

        RenderUtil.drawCircle(posX, posY, 2, Color.white.getRGB());
        RenderUtil.drawCircle(posX, posY, 1.5f, Color.black.getRGB());
    }

    private void drawGraphForeground(GraphSetting graph, float renderWidthX, float renderHeightY, int adjustedY) {
        lerpToX = x + (int) renderWidthX;
        lerpToY = adjustedY + 20 + (int) (getOffset() - 20 - renderHeightY);
        posX = (float) MathHelper.lerp(0.1, posX, lerpToX);
        posY = (float) MathHelper.lerp(0.1, posY, lerpToY);

        RenderUtil.drawCircle(posX, posY, 2, Color.white.getRGB());
        RenderUtil.drawCircle(posX, posY, 1.5f, Color.black.getRGB());

        Gui.drawRect(x, adjustedY + 18, x + compWidth, adjustedY + 20, new Color(22, 22, 22).getRGB());
        Gui.drawRect(x, adjustedY + getOffset() + 3, x + compWidth, adjustedY + getOffset() + 5, new Color(22, 22, 22).getRGB());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int adjustedY = (int) (y - 5);
        if (RenderUtil.hover(x, adjustedY + 20, mouseX, mouseY, compWidth, getOffset() - 20) && mouseButton == 0) {
            dragX = dragY = true;
            return true;
        }
        return false;
    }
}
