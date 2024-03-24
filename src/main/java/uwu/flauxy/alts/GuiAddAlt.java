package uwu.flauxy.alts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Proxy;
import java.util.Objects;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import uwu.flauxy.Flauxy;
import uwu.flauxy.utils.config.ConfigUtil;
import uwu.flauxy.utils.config.Folder;


public class GuiAddAlt
extends GuiScreen {
    private final GuiAltManager manager;
    private PasswordField password;
    private String status = (Object)((Object)EnumChatFormatting.GRAY) + "Idle...";
    private GuiTextField username;
    private AltLoginThread thread;

    public GuiAddAlt(GuiAltManager manager) {
        this.manager = manager;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                AddAltThread login = new AddAltThread(this.username.getText(), this.password.getText());
                login.start();
                /// Gonna add it to a file
                String name = this.username.getText();
                String pass = this.password.getText();
                if(!Objects.isNull(pass)){
                    String altFolder = String.valueOf(new File(String.valueOf(Flauxy.INSTANCE.clientDirectory)));
                    File dataFile = new File(altFolder,"alts.txt");
                    try {
                        PrintWriter pw = new PrintWriter(dataFile);
                        pw.println(name + ":" + pass);
                        pw.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.manager);
            }
        }
    }

    @Override
    public void drawScreen(int i2, int j2, float f2) {
        this.drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "Add Alt", width / 2, 20, -1);
        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Username / E-Mail", width / 2 - 96, 66, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Password", width / 2 - 96, 106, -7829368);
        }
        this.drawCenteredString(this.fontRendererObj, this.status, width / 2, 30, -1);
        super.drawScreen(i2, j2, f2);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 92 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 116 + 12, "Back"));
        this.username = new GuiTextField(this.eventButton, this.mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.password = new PasswordField(this.mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        this.username.textboxKeyTyped(par1, par2);
        this.password.textboxKeyTyped(par1, par2);
        if (par1 == '\t' && (this.username.isFocused() || this.password.isFocused())) {
            this.username.setFocused(!this.username.isFocused());
            this.password.setFocused(!this.password.isFocused());
        }
        if (par1 == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        try {
            super.mouseClicked(par1, par2, par3);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(par1, par2, par3);
        this.password.mouseClicked(par1, par2, par3);
    }

    static void access$0(GuiAddAlt guiAddAlt, String status) {
        guiAddAlt.status = status;
    }

    private class AddAltThread
    extends Thread {
        private final String password;
        private String status;
        private final String username;
        private Minecraft mc = Minecraft.getMinecraft();

        public AddAltThread(String username, String password) {
            super("Alt Login Thread");
            this.username = username;
            this.password = password;
            this.status = (Object)((Object)EnumChatFormatting.GRAY) + "Waiting...";
        }

        private Session createSession(String username, String password) {
            try {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult result = null;
                result = authenticator.loginWithCredentials(username, password);
                return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getStatus() {
            return this.status;
        }

        @Override
        public void run() {
            if (this.password.equals("")) {
                this.mc.session = new Session(this.username, "", "", "mojang");
                this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + this.username + " - offline name)";
                return;
            }
            this.status = (Object)((Object)EnumChatFormatting.YELLOW) + "Logging in...";
            Session auth = this.createSession(this.username, this.password);
            if (auth == null) {
                this.status = (Object)((Object)EnumChatFormatting.RED) + "Login failed!";
            } else {
                AltManager altManager = Flauxy.INSTANCE.altManager;
                AltManager.lastAlt = new Alt(this.username, this.password);
                this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + auth.getUsername() + ")";
                this.mc.session = auth;
            }
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}

