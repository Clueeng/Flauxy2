package uwu.noctura.ui.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import uwu.noctura.Noctura;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.ui.star.StarParticle;
import uwu.noctura.utils.MathHelper;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;
import uwu.noctura.utils.font.TTFFontRenderer;
import uwu.noctura.utils.render.RenderUtil;
import uwu.noctura.utils.render.shader.StencilUtil;
import uwu.noctura.utils.render.shader.blur.GaussianBlur;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static uwu.noctura.ui.star.StarParticle.drawLinesToNearestParticles;
import static uwu.noctura.ui.star.StarParticle.getNearestParticles;
import static uwu.noctura.utils.render.RenderUtil.generateStars;

public class PacketTweaker extends GuiScreen {

    public static PacketTweaker instance;
    private GuiTextField packetNameField;
    private GuiTextField[] argumentFields;
    private GuiButton sendButton;
    String packetStatus = "";
    private long sentTimestamp = 0L;

    private int currentOpacity = 0;

    String[] cachedFieldArgs;
    String cachedPacketName;
    public static String cachedMs;
    public String cachedState;


    private GuiButton toggleButton;
    private GuiTextField msField;
    public static boolean isRunning = false;

    @Override
    public void initGui() {
        instance = instance == null ? new PacketTweaker() : instance;
        this.packetNameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 40, 200, 20);
        this.packetNameField.setMaxStringLength(50);
        this.packetNameField.setFocused(true);
        this.packetNameField.setText("Packet Name");

        this.argumentFields = new GuiTextField[7];
        for (int i = 0; i < argumentFields.length; i++) {
            argumentFields[i] = new GuiTextField(i + 1, this.fontRendererObj, this.width / 2 - 100, 70 + (i * 30), 200, 20);
            argumentFields[i].setMaxStringLength(25000);
            argumentFields[i].setText("");
        }
        if(cachedPacketName != null){
            packetNameField.setText(cachedPacketName);
        }
        if(cachedMs != null){
            msField.setText(cachedMs);
        }
        //if(cachedState != null){
        //    toggleButton.displayString = cachedState;
        //}

        if (cachedFieldArgs != null) {
            for (int i = 0; i < Math.min(argumentFields.length, cachedFieldArgs.length); i++) {
                argumentFields[i].setText(cachedFieldArgs[i]);
            }
        }

        this.sendButton = new GuiButton(1, this.width / 2 - 100, 300, 200, 20, "Send Packet");
        this.buttonList.add(this.sendButton);


        this.msField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, height - 80, 200, 20);
        this.msField.setMaxStringLength(24);
        this.msField.setText("1000");
        this.msField.setVisible(true);

        this.toggleButton = new GuiButton(3, this.width / 2 - 100, height - 120, 200, 20, "Start");
        toggleButton.displayString = isRunning ? "Stop" : "Start";
        this.buttonList.add(this.toggleButton);

        generateStars(140, stars, width, height);
    }

    private List<StarParticle> stars = new ArrayList<>();



    @Override
    public void updateScreen() {
        if(packetNameField.isFocused()){
            packetNameField.updateCursorCounter();
        }
        if(msField.isFocused()){
            this.msField.drawTextBox();
            msField.updateCursorCounter();
        }
        for (GuiTextField field : argumentFields) {
            if (field.isFocused()) {
                field.updateCursorCounter();
            }
        }

    }

    String[] fieldsOld = new String[7];

    public Map<String, String> packetSuggestion(){
        Map<String, String> packetSuggestions = new HashMap<>();
        packetSuggestions.put("C03", "C03PacketPlayer");
        packetSuggestions.put("C17", "C17PacketCustomPayload");
        packetSuggestions.put("C0E", "C0EPacketClickWindow");
        packetSuggestions.put("C02", "C02PacketUseEntity");
        packetSuggestions.put("C04", "C04PacketPlayerPosition");
        packetSuggestions.put("C16", "C16PacketClientStatus");
        packetSuggestions.put("C05", "C05PacketPlayerLook");
        packetSuggestions.put("C06", "C06PacketPlayerPosLook");
        packetSuggestions.put("C08", "C08PacketPlayerBlockPlacement");
        packetSuggestions.put("C00", "C00PacketKeepAlive");
        packetSuggestions.put("C0F", "C0FPacketConfirmTransaction");
        packetSuggestions.put("C0C", "C0CPacketInput");
        packetSuggestions.put("C07", "C07PacketPlayerDigging");
        packetSuggestions.put("C13", "C13PacketPlayerAbilities");
        packetSuggestions.put("C0B", "C0BPacketEntityAction");
        packetSuggestions.put("C18", "C18PacketSpectate");
        packetSuggestions.put("C09", "C09PacketHeldItemChange");

        return packetSuggestions;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        TTFFontRenderer font = Noctura.INSTANCE.getFontManager().getFont("Good 21");
        TTFFontRenderer smallerFont = Noctura.INSTANCE.getFontManager().getFont("Good 18");
        currentOpacity = currentOpacity < 3 ? 0 : (int) MathHelper.lerp(0.01, currentOpacity, 0);

        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(0
                ,0,width, height);

        Gui.drawRect(1, 1, 1, 1, new Color(0, 0, 0, 10).getRGB());
        GaussianBlur.renderBlur(5f);
        Gui.drawRect(1, 1, 1, 1, new Color(0, 0, 0, 10).getRGB());

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        StencilUtil.uninitStencilBuffer();

        int mid = 240;
        int x2 = mid;
        int rightX = width - mid;
        int y2 = 4;
        int bottom = height - 4;

        RenderUtil.drawUnfilledRectangle(x2-1, y2-1, rightX+1, bottom+1, 4, new Color(255, 255, 255).getRGB());

        RenderUtil.prepareBlur(x2, y2, rightX, bottom);
        GaussianBlur.renderBlur(23f);
        RenderUtil.finishBlur();

        this.drawDefaultBackground();

        for (StarParticle star : stars) {
            star.update(width, height, mouseX, mouseY);
            star.render(mouseX, mouseY, stars);
        }
        List<StarParticle> nearestParticles = getNearestParticles(mouseX, mouseY, stars, 3);
        drawLinesToNearestParticles(mouseX, mouseY, nearestParticles);
        this.packetNameField.drawTextBox();

        this.msField.drawTextBox();
        for (GuiTextField field : argumentFields) {
            field.drawTextBox(field.ok);
        }

        // hints
        String[] hints = new String[7];
        String search = "";

        String field = packetNameField.getText();
        String suggestion = packetSuggestion().get(field);

        if (suggestion != null) {
            search = "Do you mean " + suggestion + "?";
            autoComplete(packetNameField, suggestion);
        }



        //mc.fontRendererObj.drawString(search, (int) (width / 2f - (mc.fontRendererObj.getStringWidth(search) / 2f)), 12, -1);
        if(!search.isEmpty()){
            String[] words = search.split(" ");
            String lastWord = words[words.length - 1];
            lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");
            if(!field.equals(lastWord)){
                font.drawStringWithShadow(search, (int) (width / 2f - (font.getWidth(search) / 2f)), 12, -1);
                smallerFont.drawStringWithShadow("Press TAB for autocomplete", (int) (width / 2f - (smallerFont.getWidth("Press TAB for autocomplete") / 2f)), 24, -1);
            }else{
                font.drawStringWithShadow("Editing Packet...", (int) (width / 2f - (font.getWidth("Editing Packet...") / 2f)), 12, -1);
            }
        }

        if(field.equals("C0BPacketEntityAction")){
            hints[0] = "C0BAction (?)";
        }
        if(field.equals("C05PacketPlayerLook")){
            hints[0] = "Yaw (D)";
            hints[1] = "Pitch (D)";
            hints[2] = "Ground (B)";
        }
        if(field.equals("C02PacketUseEntity")){
            hints[0] = "Entity (?)";
            hints[1] = "Action (?)";
        }
        if(field.equals("C17PacketCustomPayload")){
            hints[0] = "Channel (String)";
            hints[1] = "PacketBuffer (?)";
        }
        /*
        // int windowId, int slotId, int usedButton, int mode, ItemStack clickedItem, short actionNumber
        // C0EPacketClickWindow
         */
        if(field.equals("C0EPacketClickWindow")){
            hints[0] = "Window ID (I)";
            hints[1] = "Slot ID (I)";
            hints[2] = "Button ID (I)";
            hints[3] = "Mode (I)";
            hints[4] = "Action Number (S)";
        }
        if(field.equals("C0DPacketCloseWindow")){
            hints[0] = "Window ID (I)";
        }

        if(field.equals("C0FPacketConfirmTransaction")){
            // int windowId, short uid, boolean accepted
            hints[0] = "Window ID (I)";
            hints[1] = "UID (Short)";
            hints[2] = "Accepted (B)";
        }
        if(field.equals("C18PacketSpectate")){
            hints[0] = "Player Name (S)";
        }
        if(field.equals("C09PacketHeldItemChange")){
            hints[0] = "Slot ID (I)";
        }
        if(field.equals("C00PacketKeepAlive")){
            hints[0] = "Key (I)";
        }
        if(field.equals("C03PacketPlayer")){
            hints[0] = "Ground (B)";
        }
        if(field.equals("C04PacketPlayerPosition")){
            hints[0] = "PosX (D)";
            hints[1] = "PosY (D)";
            hints[2] = "PosZ (D)";
            hints[3] = "Ground (B)";
        }
        if(field.equals("C05PacketPlayerPosition")){
            hints[0] = "Yaw (D)";
            hints[1] = "Pitch (D)";
            hints[2] = "Ground (B)";
        }
        if(field.equals("C06PacketPlayerPosLook")){
            hints[0] = "PosX (D)";
            hints[1] = "PosY (D)";
            hints[2] = "PosZ (D)";
            hints[3] = "Yaw (D)";
            hints[4] = "Pitch (D)";
            hints[5] = "Ground (B)";
        }
        if(field.equals("C07PacketPlayerDigging")){
            hints[0] = "PosX (D)";
            hints[1] = "PosY (D)";
            hints[2] = "PosZ (D)";
            hints[3] = "Face (?)";
            hints[4] = "C07Action (?)";
        }
        if(field.equals("C0CPacketInput")){
            hints[0] = "Strafe Speed (F)";
            hints[1] = "Forward Speed (F)";
            hints[2] = "Jumping (B)";
            hints[3] = "Sneaking (B)";
        }
        if(field.equals("C08PacketPlayerBlockPlacement")){
            hints[0] = "BlockPos X (D)";
            hints[1] = "BlockPos Y (D)";
            hints[2] = "BlockPos Z (D)";
            hints[3] = "Face (?)";
        }
        if(field.equals("C16PacketClientStatus")){
            //hints[0] = "BlockPos X (D)";
            //hints[1] = "BlockPos Y (D)";
            //hints[2] = "BlockPos Z (D)";
            hints[0] = "Status In (?)";
        }
        /*

                    boolean creative = Boolean.parseBoolean(args.get(2));
                    boolean isFlying = Boolean.parseBoolean(args.get(3));
                    boolean disableDamage = Boolean.parseBoolean(args.get(4));
                    boolean allowFly = Boolean.parseBoolean(args.get(5));
                    boolean allowEdit = Boolean.parseBoolean(args.get(6));
         */
        if(field.equals("C13") || field.equals("C13PacketPlayerAbilities")){
            hints[0] = "Creative (B)";
            hints[1] = "Is Flying (B)";
            hints[2] = "Disable Dmg (B)";
            hints[3] = "Allow Fly (B)";
            hints[4] = "Allow Edit (B)";
        }

        for(int i = 0; i < hints.length; i++){
            String hint = hints[i];
            GuiTextField text = argumentFields[i];
            if(hint == null || text == null) continue;
            if (hint.contains("(B)")) {
                String currentText = text.getText();
                if (currentText == null || currentText.isEmpty()) continue;
                if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                        currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                    currentText = currentText.substring(2);
                }
                text.ok = !(currentText.equals("true") || currentText.equals("false"));
            }
            if (hint.contains("(I)") || hint.contains("(Short)") || hint.contains("(S)")) {
                String currentText = text.getText();
                if (currentText == null || currentText.isEmpty()) continue;
                if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                        currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                    currentText = currentText.substring(2);
                }
                try {
                    Integer.parseInt(currentText);
                    text.ok = false;
                } catch (NumberFormatException e) {
                    text.ok = true;
                }
            }
            if (hint.contains("(D)")) {
                String currentText = text.getText();
                if (currentText == null || currentText.isEmpty()) continue;
                if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                        currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                    currentText = currentText.substring(2);
                }
                try {
                    Double.parseDouble(currentText);
                    text.ok = false;
                } catch (NumberFormatException e) {
                    text.ok = true;
                }
            }
            if(hint.contains("(?)")){

                if (hint.contains("C07Action")) {
                    String currentText = text.getText();
                    if (currentText == null || currentText.isEmpty()) continue;

                    // Check if the text already has a formatting prefix and remove it.
                    if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                            currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                        currentText = currentText.substring(2); // Remove existing formatting
                    }

                    // Check if the text matches valid C07Action values.
                    /*

        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM;
                     */
                    if (!currentText.equalsIgnoreCase("START_DESTROY_BLOCK") &&
                            !currentText.equalsIgnoreCase("ABORT_DESTROY_BLOCK") &&
                            !currentText.equalsIgnoreCase("STOP_DESTROY_BLOCK") &&
                            !currentText.equalsIgnoreCase("DROP_ALL_ITEMS") &&
                            !currentText.equalsIgnoreCase("RELEASE_USE_ITEM") &&
                            !currentText.equalsIgnoreCase("DROP_ITEM")) {
                        text.ok = true;
                    } else {
                        text.ok = false;
                    }
                }

                if (hint.contains("Status In")) {
                    String currentText = text.getText();
                    if (currentText == null || currentText.isEmpty()) continue;

                    if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                            currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                        currentText = currentText.substring(2);
                    }

                    // Check if the text matches valid Status In values.
                    if (!currentText.equalsIgnoreCase("PERFORM_RESPAWN") &&
                            !currentText.equalsIgnoreCase("REQUEST_STATS") &&
                            !currentText.equalsIgnoreCase("OPEN_INVENTORY_ACHIEVEMENT")) {
                        text.ok = true;
                    } else {
                        text.ok = false;
                    }
                }

                if (hint.contains("C02PacketUseEntity")) {
                    String currentText = text.getText();
                    if (currentText == null || currentText.isEmpty()) continue;

                    if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                            currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                        currentText = currentText.substring(2);
                    }

                    // Check for valid Entity or Action values.
                    if (hint.contains("Entity") && !currentText.equalsIgnoreCase("%p%")) {
                        // Validate entity names or special placeholder.
                        text.setText(EnumChatFormatting.RED + currentText);
                    } else if (hint.contains("Action") &&
                            !currentText.equalsIgnoreCase("INTERACT") &&
                            !currentText.equalsIgnoreCase("ATTACK") &&
                            !currentText.equalsIgnoreCase("INTERACT_AT")) {
                        text.ok = true;
                    } else {
                        text.ok = false;
                    }
                }

                if (hint.contains("C0BAction")) {
                    String currentText = text.getText();
                    if (currentText == null || currentText.isEmpty()) continue;

                    if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                            currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                        currentText = currentText.substring(2);
                    }

                    // Check if the text matches valid C0BAction values.
                    if (!currentText.equalsIgnoreCase("START_SNEAKING") &&
                            !currentText.equalsIgnoreCase("OPEN_INVENTORY")) {
                        text.ok = true;
                    } else {
                        text.ok = false;
                    }
                }

                if (hint.contains("Face")) {
                    String currentText = text.getText();
                    if (currentText == null || currentText.isEmpty()) continue;

                    if (currentText.startsWith(EnumChatFormatting.RED.toString()) ||
                            currentText.startsWith(EnumChatFormatting.WHITE.toString())) {
                        currentText = currentText.substring(2);
                    }

                    // Check if the text matches valid Face directions.
                    if (!currentText.equalsIgnoreCase("DOWN") &&
                            !currentText.equalsIgnoreCase("UP") &&
                            !currentText.equalsIgnoreCase("EAST") &&
                            !currentText.equalsIgnoreCase("WEST") &&
                            !currentText.equalsIgnoreCase("NORTH") &&
                            !currentText.equalsIgnoreCase("SOUTH") &&
                            !currentText.equalsIgnoreCase("SELF")) {
                        text.ok = true;
                    } else {
                        text.ok = false;
                    }
                }
            }
        }

        int i = 0;
        for(String s : hints){
            if(s == null || s.isEmpty()){
                continue;
            }
            //mc.fontRendererObj.drawString(s, width / 2 - 200, 70 + (i * 30) + 6, -1);
            smallerFont.drawStringWithShadow(s, width / 2f - 200, 70 + (i * 30) + 6, -1);
            int x = width / 2 - 200;
            int y= 70 + (i * 30) + 6;
            if (s.contains("?")) {
                if (mouseX >= x && mouseX <= x + (mc.fontRendererObj.getStringWidth(s)) && mouseY >= y && mouseY <= y + 24) {
                    if (s.toLowerCase().contains("C07Action".toLowerCase())) {
                        smallerFont.drawString("(START_DESTROY_BLOCK, DROP_ITEM etc...)", x + 310, y, -1);
                    }
                    /*

                     */
                    if(s.toLowerCase().contains("PacketBuffer".toLowerCase())){
                        smallerFont.drawString("PacketBuffer built from any java object", x + 310, y, -1);
                        smallerFont.drawString("Specify object by typing b: for boolean", x + 310, y + 12, -1);
                        smallerFont.drawString("or s: for short before the custom parameter", x + 310, y + 24, -1);
                        smallerFont.drawString("if no prefix is found, or is invalid, the argument", x + 310, y + 36, -1);
                        smallerFont.drawString("will be interpreted as a String object", x + 310, y + 48, -1);
                    }
                    if (s.toLowerCase().contains("Status In".toLowerCase())) {
                        smallerFont.drawString("(PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT)", x + 310, y, -1);
                    }
                    if(field.equals("C02PacketUseEntity")){
                        if(s.toLowerCase().contains("Entity".toLowerCase())){
                            smallerFont.drawString("Any Player Name or %p% for self", x + 310, y, -1);
                        }
                        if(s.toLowerCase().contains("Action".toLowerCase())){
                            smallerFont.drawString("(INTERACT, ATTACK, INTERACT_AT)", x + 310, y, -1);
                        }
                    }
                    if (s.toLowerCase().contains("C0BAction".toLowerCase())) {
                        smallerFont.drawString("(START_SNEAKING, OPEN_INVENTORY etc...)", x + 310, y, -1);
                    }
                    if (s.toLowerCase().contains("Face".toLowerCase())) {
                        smallerFont.drawString("(DOWN, UP, EAST, WEST, NORTH, SOUTH, SELF)", x + 310, y, -1);
                    }
                }
            }
            i++;
        }

        //smallerFont.drawString(String.valueOf(sentTimestamp), (int) ((width/2f) - (smallerFont.getWidth(String.valueOf(sentTimestamp))/2f)), height - 12, -1);

        // the overlay
        currentOpacity = net.minecraft.util.MathHelper.clamp_int(currentOpacity, 0, 255);
        RenderUtil.drawGradientRect(0, height - 128, width, height, new Color(feedbackColor.getRed(), feedbackColor.getGreen(), feedbackColor.getBlue(), currentOpacity).getRGB(), new Color(0, 0, 0, 0).getRGB());

        RenderUtil.drawGradientRect(0, height / 1.8f, width, height + 100,
                new Color(60, 68, 170, 100).getRGB(),
                new Color(0, 0, 0, 0).getRGB());

        smallerFont.drawString(packetStatus, (int) ((width/2f) - (smallerFont.getWidth(packetStatus)/2f)), height - 24, new Color(255, 255, 255, 200).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    Color feedbackColor = new Color(255, 0, 0);

    public void autoComplete(GuiTextField field, String toComplete){
        if(Keyboard.isKeyDown(Keyboard.KEY_TAB) && field.isFocused()){
            field.setText(toComplete);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // cache
        cachedFieldArgs = new String[argumentFields.length];
        cachedPacketName = packetNameField.getText() == null ? "" : packetNameField.getText();
        cachedMs = msField.getText() == null ? "" : msField.getText();
        cachedState = toggleButton.displayString == null ? "Stop" : toggleButton.displayString;
        for(int i = 0; i < argumentFields.length; i++){
            cachedFieldArgs[i] = argumentFields[i].getText();
        }

        if(keyCode == Keyboard.KEY_ESCAPE){
            this.mc.displayGuiScreen(null);
        }
        if (keyCode == Keyboard.KEY_TAB) {
            int numFields = argumentFields.length;
            for (int i = 0; i < numFields; i++) {
                GuiTextField field = argumentFields[i];
                if (field.isFocused()) {
                    field.setFocused(false);
                    boolean back = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
                    int nextIndex = back ? (i - 1 + numFields) % numFields : (i + 1) % numFields;
                    argumentFields[nextIndex].setFocused(true);
                    break;
                }
            }
        }
        if(packetNameField.isFocused()){
            packetNameField.textboxKeyTyped(typedChar, keyCode);
        }
        if(msField.isFocused()){
            msField.textboxKeyTyped(typedChar, keyCode);
        }
        for(GuiTextField field : argumentFields){
            if(field.isFocused()){
                field.textboxKeyTyped(typedChar, keyCode);
                fieldsOld[Arrays.asList(argumentFields).indexOf(field)] = field.getText();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        msField.setFocused(false);
        packetNameField.setFocused(false);

        if(mouseX >= packetNameField.xPosition && mouseX <= packetNameField.xPosition + packetNameField.getWidth() && mouseY >= packetNameField.yPosition && mouseY <= packetNameField.yPosition + 24){
            for (GuiTextField argumentField : argumentFields) {
                argumentField.setFocused(false);
            }
            packetNameField.setFocused(true);
            if(packetNameField.isFocused()){
                packetNameField.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        if(mouseX >= msField.xPosition && mouseX <= msField.xPosition + msField.getWidth() && mouseY >= msField.yPosition && mouseY <= msField.yPosition + 24){
            for (GuiTextField argumentField : argumentFields) {
                argumentField.setFocused(false);
            }
            msField.setFocused(true);
            if(msField.isFocused()){
                msField.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        for(GuiTextField field : argumentFields){
            if(mouseX >= field.xPosition && mouseX <= field.xPosition + field.getWidth() && mouseY >= field.yPosition && mouseY <= field.yPosition + 24){
                for (GuiTextField argumentField : argumentFields) {
                    argumentField.setFocused(false);
                }
                field.setFocused(true);
                packetNameField.setFocused(false);
                if(field.isFocused()){
                    field.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            currentOpacity = 255;
            sendPacket();
        }
        if(button.id == 3){
            sendPacket();
            String msText = msField.getText();
            try {
                int milliseconds = Integer.parseInt(msText);
                isRunning = !isRunning;
                if (isRunning) {
                    toggleButton.displayString = "Stop";
                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Packet Tweaker", "Started sending packet at interval"));
                } else {
                    toggleButton.displayString = "Start";
                    Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Packet Tweaker", "Stopped sending packet at interval"));
                }
                System.out.println(milliseconds + " is " + isRunning);
            } catch (NumberFormatException e) {
                System.out.println("Invalid milliseconds value: " + msText);
                Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Packet Tweaker", "Invalid Parameter: " + msText));
            }
        }

    }

    public void sendPacket(){
        String packetName = packetNameField.getText();
        //String[] args = new String[argumentFields.length + 1];
        ArrayList<String> args = new ArrayList<>();
        args.add(".packet");
        args.add(packetName);
        //args[0] = ".packet";
        //args[1] = packetName;

        packetStatus = "Sent " + packetName + ": (";
        cachedFieldArgs = new String[argumentFields.length];
        cachedPacketName = packetName;

        for (int i = 0; i < argumentFields.length; i++) {
            // Get the text from the argument field and remove color formatting.
            String text = argumentFields[i].getText();
            if (text != null) {
                // Remove any color formatting codes.
                text = text.replaceAll("§[0-9a-fk-or]", "");

                args.add(text);
                cachedFieldArgs[i] = text;

                if (!text.isEmpty()) {
                    String formatted = text
                            .replace("%x%", String.format("%.2f", mc.thePlayer.posX))
                            .replace("%y%", String.format("%.2f", mc.thePlayer.posY))
                            .replace("%z%", String.format("%.2f", mc.thePlayer.posZ))
                            .replace("%yaw%", String.format("%.2f", mc.thePlayer.rotationYaw))
                            .replace("%pitch%", String.format("%.2f", mc.thePlayer.rotationPitch));

                    packetStatus += (i == 0 ? "" : ", ") + formatted;
                }
            }
        }
        packetStatus += ")";
        sentTimestamp = System.currentTimeMillis();

        args.replaceAll(s -> s.replace("%x%", String.valueOf(mc.thePlayer.posX)));
        args.replaceAll(s -> s.replace("%y%", String.valueOf(mc.thePlayer.posY)));
        args.replaceAll(s -> s.replace("%z%", String.valueOf(mc.thePlayer.posZ)));


        //Wrapper.instance.log(String.valueOf(args));

        try {
            //Wrapper.instance.log(String.valueOf(args));
            sendPacket(args);
        } catch (Exception e) {
            Wrapper.instance.log("Error sending packet: " + e.getMessage());
        }
    }

    public static Packet cachedPacket;

    private void sendPacket(ArrayList<String> args) {
        try {
            String packetName = args.get(1);
            switch (packetName){
                case "C17":{
                    packetName = "C17PacketCustomPayload";
                    break;
                }
                case "C03":{
                    packetName = "C03PacketPlayer";
                    break;
                }
                case "C04":{
                    packetName = "C04PacketPlayerPosition";
                    break;
                }
                case "C05":{
                    packetName = "C05PacketPlayerLook";
                    break;
                }
                case "C06":{
                    packetName = "C06PacketPlayerPosLook";
                    break;
                }
                case "C08":{
                    packetName = "C08PacketPlayerBlockPlacement";
                    break;
                }
                case "C00":{
                    packetName = "C00PacketKeepAlive";
                    break;
                }
                case "C0F":{
                    packetName = "C0FPacketConfirmTransaction";
                    break;
                }
                case "C0C":{
                    packetName = "C0CPacketInput";
                    break;
                }
                case "C07":{
                    packetName = "C07PacketPlayerDigging";
                    break;
                }
                case "C13":{
                    packetName = "C13PacketPlayerAbilities";
                    break;
                }
                case "C0B":{
                    packetName = "C0BPacketEntityAction";
                    break;
                }
                case "C18":{
                    packetName = "C18PacketSpectate";
                    break;
                }
                case "C09":{
                    packetName = "C09PacketHeldItemChange";
                    break;
                }
                case "C16":{
                    packetName = "C16PacketClientStatus";
                    break;
                }
                case "C02":{
                    packetName = "C02PacketUseEntity";
                    break;
                }
                case "C0D":{
                    packetName = "C0DPacketCloseWindow";
                    break;
                }
                case "C0E":{
                    packetName = "C0EPacketClickWindow";
                    break;
                }
            }
            for (int i = 0; i < args.size(); i++) {
                String s = args.get(i).replaceAll("§[0-9a-fk-or]", "");
                args.set(i, s);
            }
            feedbackColor = new Color(128, 216, 108);
            switch (packetName) {
                case "C17PacketCustomPayload":{
                    /*
                    PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                    packetbuffer.writeByte(this.localCommandBlock.func_145751_f());
                    this.localCommandBlock.func_145757_a(packetbuffer);
                    packetbuffer.writeString(this.commandTextField.getText());
                    packetbuffer.writeBoolean(this.localCommandBlock.shouldTrackOutput());
                    this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", packetbuffer));
                     */
                    String channel = args.get(2);
                    String packetBufferString = args.get(3);
                    PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());

                    if (packetBufferString.length() > 2) {
                        String prefix = packetBufferString.substring(0, 2);
                        String value = packetBufferString.substring(2);
                        switch (prefix) {
                            case "b:":
                                boolean boolValue = Boolean.parseBoolean(value);
                                packetBuffer.writeBoolean(boolValue);
                                break;
                            case "s:":
                                try {
                                    short shortValue = Short.parseShort(value);
                                    packetBuffer.writeShort(shortValue);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "i:":
                                try {
                                    int intValue = Integer.parseInt(value);
                                    packetBuffer.writeInt(intValue);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                packetBuffer.writeString(prefix + value);
                                break;
                        }

                        cachedPacket = new C17PacketCustomPayload(channel, packetBuffer);
                        //Wrapper.instance.log(((C17PacketCustomPayload)cachedPacket).getBufferData().readStringFromBuffer(99));
                        PacketUtil.sendPacket(cachedPacket);
                    } else {
                        System.out.println("Invalid packetBufferString format: " + packetBufferString);
                    }

                    cachedPacket = new C17PacketCustomPayload(channel, packetBuffer);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C0DPacketCloseWindow":{
                    int windowId = Integer.parseInt(args.get(2));
                    cachedPacket = new C0DPacketCloseWindow(windowId);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C0EPacketClickWindow":{
                    int windowId = Integer.parseInt(args.get(2));
                    int slotId = Integer.parseInt(args.get(3));
                    int button = Integer.parseInt(args.get(4));
                    int mode = Integer.parseInt(args.get(5));
                    short action = Short.parseShort(args.get(6));


                    if(mc.thePlayer.inventory.getCurrentItem() == null){
                        Wrapper.instance.log("Hold an item");
                        return;
                    }
                    ItemStack item = mc.thePlayer.inventory.getCurrentItem();
                    cachedPacket = new C0EPacketClickWindow(windowId, slotId, button, mode, item, action);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C02PacketUseEntity":{
                    String entityArgument = args.get(2);
                    if(entityArgument.equals("%p%")){
                        entityArgument = mc.thePlayer.getName();
                    }
                    Entity toBeHit = mc.theWorld.getPlayerEntityByName(entityArgument);
                    C02PacketUseEntity.Action action = C02PacketUseEntity.Action.valueOf(args.get(3));
                    cachedPacket = new C02PacketUseEntity(toBeHit, action);
                    PacketUtil.sendPacket(cachedPacket);

                    break;
                }
                case "C09PacketHeldItemChange":{
                    int slotId = Integer.parseInt(args.get(2));
                    cachedPacket = new C09PacketHeldItemChange(slotId);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C16PacketClientStatus":{
                    C16PacketClientStatus.EnumState state = C16PacketClientStatus.EnumState.valueOf(args.get(2));
                    cachedPacket = new C16PacketClientStatus(state);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C0FPacketConfirmTransaction":{
                    /*

            hints[0] = "Window ID (I)";
            hints[1] = "UID (Short)";
            hints[2] = "Accepted (B)";
                     */
                    int windowId = Integer.parseInt(args.get(2));
                    short uid = Short.parseShort(args.get(3));
                    boolean accepted = Boolean.parseBoolean(args.get(4));
                    cachedPacket = new C0FPacketConfirmTransaction(windowId, uid, accepted);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C00PacketKeepAlive":{
                    int key = Integer.valueOf(args.get(2));
                    cachedPacket = new C00PacketKeepAlive(key);
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C18PacketSpectate":{
                    String playerName = args.get(2);
                    EntityPlayer spoofed = mc.theWorld.getPlayerEntityByName(playerName);
                    if(spoofed != null){
                        UUID uid = spoofed.getUniqueID();
                        cachedPacket = new C18PacketSpectate(uid);
                    }else{
                        Wrapper.instance.log("User not found, using your uuid");
                        cachedPacket = new C18PacketSpectate(mc.thePlayer.getUniqueID());
                    }
                    PacketUtil.sendPacket(cachedPacket);
                    break;
                }
                case "C0BPacketEntityAction":{
                    /*
                    BlockPos pos = new BlockPos(Double.parseDouble(args.get(2)), Double.parseDouble(args.get(3)), Double.parseDouble(args.get(4)));
                    EnumFacing facing = EnumFacing.valueOf(args.get(5));
                    C07PacketPlayerDigging.Action action = C07PacketPlayerDigging.Action.valueOf(args.get(6));
                    cachedPacket = new C07PacketPlayerDigging(action, pos, facing);
                    PacketUtil.sendSilentPacket(cachedPacket);
                     */
                    C0BPacketEntityAction.Action action = C0BPacketEntityAction.Action.valueOf(args.get(2));
                    cachedPacket = new C0BPacketEntityAction(mc.thePlayer, action);

                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C13PacketPlayerAbilities":{
                    PlayerCapabilities cap = new PlayerCapabilities();
                    //cap.isFlying;
                    //cap.isCreativeMode;
                    //cap.disableDamage;
                    //cap.allowFlying;
                    //cap.allowEdit;
                    // start at 2
                    boolean creative = Boolean.parseBoolean(args.get(2));
                    boolean isFlying = Boolean.parseBoolean(args.get(3));
                    boolean disableDamage = Boolean.parseBoolean(args.get(4));
                    boolean allowFly = Boolean.parseBoolean(args.get(5));
                    boolean allowEdit = Boolean.parseBoolean(args.get(6));

                    cap.allowEdit = allowEdit;
                    cap.allowFlying = allowFly;
                    cap.disableDamage = disableDamage;
                    cap.isCreativeMode = creative;
                    cap.isFlying = isFlying;
                    cachedPacket = new C13PacketPlayerAbilities(cap);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C03PacketPlayer":{
                    boolean ground = Boolean.parseBoolean(args.get(2));
                    cachedPacket = new C03PacketPlayer(ground);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C04PacketPlayerPosition":{
                    double x = Double.parseDouble(args.get(2));
                    double y = Double.parseDouble(args.get(3));
                    double z = Double.parseDouble(args.get(4));
                    boolean ground = Boolean.parseBoolean(args.get(5));
                    cachedPacket = new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C05PacketPlayerLook":{
                    double yaw = Double.parseDouble(args.get(2));
                    double pitch = Double.parseDouble(args.get(3));
                    boolean ground = Boolean.parseBoolean(args.get(4));
                    System.out.println(ground);
                    C03PacketPlayer.C05PacketPlayerLook pack = new C03PacketPlayer.C05PacketPlayerLook((float) yaw, (float) pitch, ground);
                    cachedPacket = pack;
                    PacketUtil.sendSilentPacket(pack);
                    break;
                }
                case "C06PacketPlayerPosLook":{
                    double x = Double.parseDouble(args.get(2));
                    System.out.println("X : " + x);
                    double y = Double.parseDouble(args.get(3));
                    double z = Double.parseDouble(args.get(4));
                    double yaw = Double.parseDouble(args.get(5));
                    double pitch = Double.parseDouble(args.get(6));
                    boolean ground = Boolean.parseBoolean(args.get(7));
                    cachedPacket = new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, (float) yaw, (float) pitch, ground);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C07PacketPlayerDigging": {
                    BlockPos pos = new BlockPos(Double.parseDouble(args.get(2)), Double.parseDouble(args.get(3)), Double.parseDouble(args.get(4)));
                    EnumFacing facing = EnumFacing.valueOf(args.get(5));
                    C07PacketPlayerDigging.Action action = C07PacketPlayerDigging.Action.valueOf(args.get(6));
                    cachedPacket = new C07PacketPlayerDigging(action, pos, facing);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C08PacketPlayerBlockPlacement": {
                    BlockPos pos = new BlockPos(Double.parseDouble(args.get(2)), Double.parseDouble(args.get(3)), Double.parseDouble(args.get(4)));
                    EnumFacing facing = EnumFacing.valueOf(args.get(5));
                    cachedPacket = new C08PacketPlayerBlockPlacement(pos, facing.getIndex(), mc.thePlayer.inventory.getItemStack(), 0, 0, 0);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                case "C0CPacketInput": {
                    float strafeSpeed = Float.parseFloat(args.get(2));
                    float forwardSpeed = Float.parseFloat(args.get(3));
                    boolean jumping = Boolean.parseBoolean(args.get(4));
                    boolean sneaking = Boolean.parseBoolean(args.get(5));
                    cachedPacket = new C0CPacketInput(strafeSpeed, forwardSpeed, jumping, sneaking);
                    PacketUtil.sendSilentPacket(cachedPacket);
                    break;
                }
                default: {
                    Wrapper.instance.log("Unknown packet name: " + packetName);
                    feedbackColor = new Color(207, 90, 90);
                    return;
                }
            }

            //Wrapper.instance.log("Packet sent successfully!");
            //Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Packet Tweaker", "Sent " + packetName, 600));
        } catch (Exception e) {
            Wrapper.instance.log("Error sending packet: " + e.getMessage());
            feedbackColor = new Color(207, 90, 90);
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
