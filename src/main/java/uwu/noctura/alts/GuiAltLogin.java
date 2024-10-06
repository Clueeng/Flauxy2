package uwu.noctura.alts;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.alts.cookie.CookieAltsUtil;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.StringUtils;

public final class GuiAltLogin extends GuiScreen {
    private PasswordField password;

    private final GuiScreen previousScreen;

    private AltLoginThread thread;

    private GuiTextField username;

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected void actionPerformed(GuiButton button) {
        String clipboardContents;
        String text;
        String[] split;
        switch (button.id) {
            case 1:
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            case 0:
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
                break;
            case 2:
                clipboardContents = StringUtils.getTrimmedClipboardContents();
                split = clipboardContents.split(":");
                if (split[0] != null)
                    this.username.setText(split[0]);
                if (split.length > 1)
                    this.password.setText(split[1]);
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
                break;
            case 3:
                text = StringUtils.generateRandomStringName();
                this.username.setText(text);
                this.password.setText("");
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.username.setText("");
                this.password.setText("");
                this.thread.start();
                break;
            case 4:
                try {
                    File fileF = CookieAltsUtil.getCookieFile();
                    String file = fileF.getAbsolutePath();
                    CookieAltsUtil.loginWithCookie(file, account -> {
                        if (account != null) {
                            this.mc.session.switchSession(account);
                            System.out.println("logged into " + this.mc.session.getUsername() + " from " + file);
                            Noctura.INSTANCE.notificationManager.addToQueue(new Notification(NotificationType.INFO, "Alt Login", "Logged into " + this.mc.session.getUsername()));
                        } else {
                            Noctura.INSTANCE.notificationManager.addToQueue(new Notification(NotificationType.INFO, "Alt Login", "Failed logging into " + fileF.getName()));
                            System.out.println("login fail");
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    public void drawScreen(int x2, int y2, float z2) {
        drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        drawCenteredString(this.mc.fontRendererObj, "Alt Login", this.width / 2, 20, -1);
        drawCenteredString(this.mc.fontRendererObj, (this.thread == null) ? (EnumChatFormatting.GRAY + "Idle...") : this.thread.getStatus(), this.width / 2, 29, -1);
        if (this.username.getText().isEmpty())
            drawString(this.mc.fontRendererObj, "E-mail", this.width / 2 - 96, 66, -7829368);
        if (this.password.getText().isEmpty())
            drawString(this.mc.fontRendererObj, "Password", this.width / 2 - 96, 106, -7829368);
        super.drawScreen(x2, y2, z2);
    }

    public void initGui() {
        int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, var3 + 72 + 12 + 48, "Clipboard"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, var3 + 72 + 12 + 48 + 24, "Random"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, var3 + 72 - 12, "Login (Cookie)"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.password = new PasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t')
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        if (character == '\r')
            actionPerformed(this.buttonList.get(0));
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}
