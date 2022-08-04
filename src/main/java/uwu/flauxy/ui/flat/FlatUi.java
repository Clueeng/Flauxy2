package uwu.flauxy.ui.flat;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import uwu.flauxy.Flauxy;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.impl.display.ClickGUI;
import uwu.flauxy.module.setting.Setting;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.DiscordUtil;
import uwu.flauxy.utils.NumberUtil;
import uwu.flauxy.utils.render.RenderUtil;
import uwu.flauxy.utils.timer.Timer;

import static uwu.flauxy.utils.font.FontManager.getFont;


public class FlatUi extends GuiScreen {

    public boolean close = false;
    public boolean closed;
    public boolean renderDone;
    public float dragvalueX, dragvalueY;
    public static float role, roleNow;
    public static float valueRoleNow, valueRole;
    public float guiX = 200, guiY = 50;
    public float width = 500, height = 310;
    public float hy = guiY + 40;
    public float lastValue;
    public float lastOutro;
    public float percent;
    public float percent2;
    public float lastPercent2;
    public float outro;
    public int tag;
    public int moduleX = 0;

    public Timer tagtimer = new Timer();
    public Timer timer = new Timer();

    public Category Category = uwu.flauxy.module.Category.Combat;
    public Module currentModule;

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        percent = 1.33f;
        percent2 = 1.33f;
        lastPercent2 = 1f;
        lastValue = 1f;
        outro = 1;
        lastOutro = 1;
        timer.reset();
        renderDone = false;
        tag = NumberUtil.generateRandom(1000, 9999);
    }
    float valuey = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)  {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sResolution = new ScaledResolution(mc);
        ScaledResolution sr = new ScaledResolution(mc);


        float outro = smoothAnimation(this.outro, lastOutro);
        if (mc.currentScreen == null) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(outro, outro, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        }

        percent = smoothAnimation(this.percent, lastValue);
        percent2 = smoothAnimation(this.percent2, lastPercent2);

        if (percent > 0.98) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(percent, percent, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        } else {
            if (percent2 <= 1) {
                GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
                GlStateManager.scale(percent2, percent2, 0);
                GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
            }
        }

        if (percent <= 1.5 && close) {
            percent = smoothAnimation(this.percent, 2);
            percent2 = smoothAnimation(this.percent2, 2);
        }

        if (percent >= 1.4 && close) {
            percent = 1.5f;
            closed = true;
            mc.currentScreen = null;
        }

        RenderUtil.drawGradientRect(0, 0, sResolution.getScaledWidth(), sResolution.getScaledHeight(), new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue(), 100).getRGB(), new Color(0, 0, 0, 30).getRGB());

        if (isHovered(guiX, guiY, guiX + width, guiY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (dragvalueX == 0 && dragvalueY == 0) {
                dragvalueX = mouseX - guiX;
                dragvalueY = mouseY - guiY;
            } else {
                guiX = mouseX - dragvalueX;
                guiY = mouseY - dragvalueY;
            }
        } else if (dragvalueX != 0 || dragvalueY != 0) {
            dragvalueX = 0;
            dragvalueY = 0;
        }

        RenderUtil.drawRoundRect(guiX, guiY, guiX + width, guiY + height, new Color(0, 0, 0, 140).getRGB());

        if (currentModule == null) {
            RenderUtil.drawRoundRect(guiX + 7, guiY + 17 + 258, guiX + 90, guiY + 17 + 284, new Color(0, 0, 0, 100).getRGB());

            drawHead((AbstractClientPlayer) mc.thePlayer, (int) (guiX + 10), (int) (guiY + 17 + 284 - 22.5), 20, 20);
            Flauxy.INSTANCE.getFontManager().getFont("auxy 15").drawString(renderDone ? "" : DiscordUtil.getDiscordName(), (float) (guiX + 90 - 53.5 - 3.2), guiY + 17 + 258 + 4 , -1);

            Flauxy.INSTANCE.getFontManager().getFont("auxy 15").drawString("#" + DiscordUtil.getDiscordHash(), (float) (guiX + 90 - 53.5 - 3.2), guiY + 17 + 258 + 12, -1);
        }

        Flauxy.INSTANCE.getFontManager().getFont("Japan 40").drawString("F" + EnumChatFormatting.WHITE + "LAUXY", guiX + 20, guiY + 17 , new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue()).getRGB());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, 2 * ((int) (sr.getScaledHeight_double() - (guiY + height))) + 40, (int) (sr.getScaledWidth_double() * 2), (int) ((height) * 2) - 160);
        if (currentModule == null || currentModule.getSettings().isEmpty()) {
            float cateY = guiY + 65;
            for (Category m : Category.values()) {
                if (m == Category) {
                    getFont().drawString(m.name(), guiX + 20, cateY, new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue()).getRGB());
                    if (isHovered(guiX, guiY, guiX + width, guiY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                        hy = cateY;
                    } else {
                        if (hy != cateY) {
                            hy += (cateY - hy) / 20;
                        }
                    }
                } else {
                    getFont().drawString(m.name(), guiX + 20, cateY, -1);
                }
                cateY += 25;
            }
        }

        if (isHovered(guiX + 7, guiY + 17 + 258, guiX + 90, guiY + 17 + 284, mouseX, mouseY) && Mouse.isButtonDown(0) && !renderDone) {
            renderDone = true;
            final StringSelection stringSelection = new StringSelection(DiscordUtil.getDiscordName() + "#" + DiscordUtil.getDiscordHash());
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }

        if (tagtimer.hasTimeElapsed(3000, true) && renderDone) {
            renderDone = false;
        }

        if (renderDone) {
            RenderUtil.drawCheckMark( (float) (guiX + 90 - 53.5 - 3.2 + 10), (float) (guiY + 17 + 258 + 1.2), 5, -1);
        }


        if (currentModule != null && !currentModule.getSettings().isEmpty()) {
            if (moduleX > -80) {
                moduleX -= 5;
            }
        } else {
            if (moduleX < 0) {
                moduleX += 5;
            }
        }

        if (currentModule != null && !currentModule.getSettings().isEmpty()) {
            RenderUtil.drawRoundRect(guiX + 430 + moduleX, guiY + 60, guiX + width, guiY + height - 20, new Color(0, 0, 0, 90).getRGB());
            RenderUtil.drawRoundRect(guiX + 430 + moduleX, guiY + 60, guiX + width, guiY + 85, new Color(0, 0, 0, 50).getRGB());
            getFont().drawString(currentModule.getName(), guiX + 435 + moduleX + 80 - 79, guiY + 65 + 3, -1);
            if (isHovered(guiX + 435 + moduleX, guiY + 65, guiX + 435 + moduleX + 16, guiY + 65 + 16, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                currentModule = null;
                timer.reset();
            }


            int dWheel = Mouse.getDWheel();
            if (isHovered(guiX + 430 + (int) moduleX, guiY + 60, guiX + width, guiY + height - 20, mouseX, mouseY)) {
                if (dWheel < 0 && Math.abs(valueRole) + 170 < (currentModule.getSettings().size() * 25)) {
                    valueRole -= 32;
                }
                if (dWheel > 0 && valueRole < 0) {
                    valueRole += 32;
                }
            }

            if (valueRoleNow != valueRole) {
                valueRoleNow += (valueRole - valueRoleNow) / 20;
                valueRoleNow = (int) valueRoleNow;
            }

            valuey = guiY + 100 + valueRoleNow;

            if (currentModule == null) {
                return;
            }

            for (Setting v : currentModule.settings) { // setts flaily

                if (isHidden(v))
                    continue;

                if (v instanceof BooleanSetting) {
                    if (valuey + 4 > guiY + 100) {
                        if (((Boolean) ((BooleanSetting) v).isEnabled())) {
                            getFont().drawString(v.name, guiX + 445 + moduleX, valuey + 4, -1);
                            RenderUtil.drawCircle(guiX + 445 + moduleX + 115, valuey + 10, 3.5F,  new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue()).getRGB());
                        } else {
                            getFont().drawString(v.name, guiX + 445 + moduleX, valuey + 4, new Color(73, 72, 76).getRGB());
                            RenderUtil.drawCircle(guiX + 445 + moduleX + 115, valuey + 10, 3.5F, new Color(19, 19, 19).getRGB());
                        }
                        if (isHovered(guiX + 350, valuey + 2, guiX + width - 10, valuey + 12, mouseX, mouseY) && Mouse.isButtonDown(0) && timer.hasTimeElapsed(300, false)) {
                            ((BooleanSetting) v).setEnabled(!(Boolean) ((BooleanSetting) v).isEnabled());
                            timer.reset();
                        }
                    }

                    if (v.animationNow != v.animation) {
                        v.animationNow += (v.animation - v.animationNow) / 20;
                    }
                    valuey += 25;
                }

                if (v instanceof NumberSetting) {
                    if (valuey + 4 > guiY + 100) {
                        float present = (float) (((guiX + width - 11) - (guiX + 450 + moduleX)) * (((Number) ((NumberSetting) v).getValue()).floatValue() - ((NumberSetting) v).getMinimum()) / (((NumberSetting) v).getMaximum() - ((NumberSetting) v).getMinimum()));
                        getFont().drawString(v.name, guiX + 445 + moduleX, valuey + 5, new Color(73, 72, 76).getRGB());
                        getFont().drawCenteredString("" + ((NumberSetting) v).getValue(), (int) (guiX + width - 20), (int) (valuey + 5), new Color(255, 255, 255).getRGB());
                        RenderUtil.drawRect(guiX + 450 + moduleX, valuey + 20, guiX + width - 11, valuey + 21.5f, new Color(0, 0, 0, 84).getRGB());
                        float finalValuey = valuey;
                        RenderUtil.drawRect(guiX + 450 + moduleX, finalValuey + 20, guiX + 450 + moduleX + present, finalValuey + 21.5f, new Color((int) ClickGUI.red.getValue(), (int) ClickGUI.green.getValue(), (int) ClickGUI.blue.getValue()).getRGB());

                        if (isHovered(guiX + 450 + moduleX, valuey + 18, guiX + width - 11, valuey + 23.5f, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                            float render2 = (float) ((NumberSetting) v).getMinimum();
                            double max = ((NumberSetting) v).getMaximum();
                            double inc = 0.1;
                            double valAbs = (double) mouseX - ((double) (guiX + 450 + moduleX));
                            double perc = valAbs / (((guiX + width - 11) - (guiX + 450 + moduleX)));
                            perc = Math.min(Math.max(0.0D, perc), 1.0D);
                            double valRel = (max - render2) * perc;
                            double val = render2 + valRel;
                            val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                            ((NumberSetting) v).setValue(Double.valueOf(val));
                        }
                    }
                    valuey += 25;
                }

                if (v instanceof ModeSetting) {
                    ModeSetting modeValue = (ModeSetting) v;

                    if (valuey + 4 > guiY + 100 & valuey < (guiY + height)) {
                        getFont().drawString(v.name + " : " + modeValue.getMode(), (float) (guiX + 455 + moduleX - 9.5), valuey + 10, new Color(230, 230, 230).getRGB());
                        if (isHovered(guiX + 445 + moduleX, valuey + 2, guiX + width - 5, valuey + 22, mouseX, mouseY) && Mouse.isButtonDown(0) && timer.hasTimeElapsed(300, false)) {
                            ((ModeSetting) v).cycle(Mouse.getEventDWheel());
                            timer.reset();
                        }
                    }
                    valuey += 25;
                }
            }
        }

        float moduleY = guiY + 70 + roleNow;
        for (Module m : Flauxy.INSTANCE.getModuleManager().modules) {
            if (m.getCategory() != Category)
                continue;

            if (isHovered(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                if (timer.hasTimeElapsed(300, false) && moduleY + 40 > (guiY + 70) && moduleY < (guiY + height)) {
                    m.setToggled(!m.isToggled());
                    timer.reset();
                }
            } else if (isHovered(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, mouseX, mouseY) && Mouse.isButtonDown(1)) {
                if (timer.hasTimeElapsed(300, false)) {
                    if (currentModule != m) {
                        valueRole = 0;
                        currentModule = m;
                    } else if (currentModule == m) {
                        currentModule = null;
                    }
                    timer.reset();
                }
            }

            if (isHovered(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, mouseX, mouseY)) {
                if (m.isToggled()) {
                    RenderUtil.drawRoundRect(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, new Color(0, 0, 0, 130).getRGB());
                } else {
                    RenderUtil.drawRoundRect(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, new Color(0, 0, 0, 110).getRGB());
                }
            } else {
                if (m.isToggled()) {
                    RenderUtil.drawRoundRect(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, new Color(0, 0, 0, 130).getRGB());
                } else {
                    RenderUtil.drawRoundRect(guiX + 100 + moduleX, moduleY - 10, guiX + 425 + moduleX, moduleY + 25, new Color(0, 0, 0, 110).getRGB());
                }
            }

            if (!m.getSettings().isEmpty()) {
                getFont().drawString(".", guiX + 416 + moduleX, moduleY - 5, -1);
                getFont().drawString(".", guiX + 416 + moduleX, moduleY - 1, -1);
                getFont().drawString(".", guiX + 416 + moduleX, moduleY + 4, -1);
            }

            if (m.isToggled()) {
                getFont().drawString(m.getName(), guiX + 140 + moduleX - 35, moduleY + 5 - 2, new Color(220, 220, 220).getRGB());
                m.animation = 100;
            } else {
                getFont().drawString(m.getName(), guiX + 140 + moduleX - 35, moduleY + 5 - 2, new Color(108, 109, 113).getRGB());
                m.animation = 0;
            }

            if (m.animationNow != m.animation) {
                m.animationNow += (m.animation - m.animationNow) / 20;
            }

            moduleY += 40;
        }
        int currentDWheel = Mouse.getDWheel();

        if (isHovered(guiX + 100 + moduleX, guiY + 60, guiX + 425 + moduleX, guiY + height, mouseX, mouseY)) {
            if (currentDWheel < 0 && Math.abs(role) + 220 < (Flauxy.INSTANCE.getModuleManager().getModules(Category).length * 40)) {
                role -= 32;
            }
            if (currentDWheel > 0 && role < 0) {
                role += 32;
            }
        }

        if (roleNow != role) {
            roleNow += (role - roleNow) / 20;
            roleNow = (int) roleNow;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (isHovered(guiX + 100 + moduleX, guiY + 60, guiX + 425 + moduleX, guiY + height, mouseX, mouseY)) {
            if (currentDWheel < 0 && Math.abs(role) + 220 < (Flauxy.INSTANCE.getModuleManager().getModules(Category).length * 40)) {
                role -= 16;
            }
            if (currentDWheel > 0 && role < 0) {
                role += 16;
            }
        }

        if (roleNow != role) {
            roleNow += (role - roleNow) / 20;
            roleNow = (int) roleNow;
        }
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float categoryY = guiY + 65;
        for (Category m : Category.values()) {

            if (isHovered(guiX, categoryY - 8, guiX + 50, categoryY + getFont().getHeight("") + 8, mouseX, mouseY)) {
                if (Category != m) {
                    role = 0;
                }

                Category = m;
                for (Module module : Flauxy.INSTANCE.getModuleManager().modules) {
                    module.animation = 0;module.animationNow = 0;

                }
            }
            categoryY += 25;
        }
        if(currentModule != null){
            if(currentModule.settings != null){
                for (Setting v : currentModule.settings) { // setts flaily

                    if (isHidden(v))
                        continue;

                    if (v instanceof BooleanSetting) {
                        if (valuey + 4 > guiY + 100) {
                            if (isHovered(guiX + width - 30, valuey + 2, guiX + width - 10, valuey + 12, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                //((BooleanSetting) v).setEnabled(!(Boolean) ((BooleanSetting) v).isEnabled());
                            }
                        }
                    }

                    if (v instanceof NumberSetting) {
                        if (valuey + 4 > guiY + 100) {
                            float present = (float) (((guiX + width - 11) - (guiX + 450 + moduleX)) * (((Number) ((NumberSetting) v).getValue()).floatValue() - ((NumberSetting) v).getMinimum()) / (((NumberSetting) v).getMaximum() - ((NumberSetting) v).getMinimum()));
                            float finalValuey = valuey;
                            if (isHovered(guiX + 450 + moduleX, valuey + 18, guiX + width - 11, valuey + 23.5f, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                float render2 = (float) ((NumberSetting) v).getMinimum();
                                double max = ((NumberSetting) v).getMaximum();
                                double inc = 0.1;
                                double valAbs = (double) mouseX - ((double) (guiX + 450 + moduleX));
                                double perc = valAbs / (((guiX + width - 11) - (guiX + 450 + moduleX)));
                                perc = Math.min(Math.max(0.0D, perc), 1.0D);
                                double valRel = (max - render2) * perc;
                                double val = render2 + valRel;
                                val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);

                                ((NumberSetting) v).setValue(Double.valueOf(val));
                            }
                        }
                    }

                    if (v instanceof ModeSetting) {
                        ModeSetting modeValue = (ModeSetting) v;

                        if (valuey + 4 > guiY + 100 & valuey < (guiY + height)) {
                            if (isHovered(guiX + 445 + moduleX, valuey + 2, guiX + width - 5, valuey + 22, mouseX, mouseY) && Mouse.isButtonDown(0) && timer.hasTimeElapsed(300, false)) {
                                ((ModeSetting) v).cycle(Mouse.getEventDWheel());
                                timer.reset();
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if(!closed && keyCode == Keyboard.KEY_ESCAPE){
            close = true;
            mc.mouseHelper.grabMouseCursor();
            mc.inGameHasFocus = true;
            return;
        }
        
        if(close) {
            this.mc.displayGuiScreen(null);
        }

        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onGuiClosed(){}


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

    public float smoothAnimation(double current, double last){
        return (float) (current + (last - current) / (Minecraft.getDebugFPS() / 10));
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }


    public static void drawHead(final AbstractClientPlayer target, final int x, final int y, final int width, final int height) {
        final ResourceLocation skin = target.getLocationSkin();
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1, 1, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }


}
