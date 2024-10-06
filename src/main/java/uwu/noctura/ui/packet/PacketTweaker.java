package uwu.noctura.ui.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import uwu.noctura.Noctura;
import uwu.noctura.notification.Notification;
import uwu.noctura.notification.NotificationType;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PacketTweaker extends GuiScreen {

    public static PacketTweaker instance;
    private GuiTextField packetNameField;
    private GuiTextField[] argumentFields;
    private GuiButton sendButton;
    String packetStatus = "";

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
            argumentFields[i].setMaxStringLength(50);
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
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.packetNameField.drawTextBox();
        this.msField.drawTextBox();
        if(System.currentTimeMillis() % 100 == 0){
            if(packetNameField.isFocused()){
                packetNameField.updateCursorCounter();
            }
            if(msField.isFocused()){
                this.msField.drawTextBox();
            }
        }
        for (GuiTextField field : argumentFields) {
            field.drawTextBox();
            if(System.currentTimeMillis() % 100 == 0){
                if(field.isFocused()){
                    field.updateCursorCounter();
                }
            }
        }

        // hints
        String[] hints = new String[7];
        String search = "";

        String field = packetNameField.getText();
        if(field.contains("C03")){
            search = "Do you mean C03PacketPlayer?";
        }
        if(field.contains("C04")){
            search = "Do you mean C04PacketPlayerPosition?";
        }
        if(field.contains("C05")){
            search = "Do you mean C05PacketPlayerLook?";
        }
        if(field.contains("C06")){
            search = "Do you mean C06PacketPlayerPosLook?";
        }
        if(field.contains("C08")){
            search = "Do you mean C08PacketPlayerBlockPlacement?";
        }
        if(field.contains("C00")){
            search = "Do you mean C00PacketKeepAlive?";
        }
        if(field.contains("C0F")){
            search = "Do you mean C0FPacketConfirmTransaction?";
        }
        if(field.contains("C0C")){
            search = "Do you mean C0CPacketInput?";
        }
        if(field.contains("C07")){
            search = "Do you mean C07PacketPlayerDigging?";
        }
        if(field.contains("C13")){
            search = "Do you mean C13PacketPlayerAbilities?";
        }
        if(field.contains("C0B")){
            search = "Do you mean C0BPacketEntityAction?";
        }
        if(field.contains("C18")){
            search = "Do you mean C18PacketSpectate?";
        }

        mc.fontRendererObj.drawString(search, (int) (width / 2f - (mc.fontRendererObj.getStringWidth(search) / 2f)), 24, -1);

        if(field.equals("C0B") || field.equals("C0BPacketEntityAction")){
            hints[0] = "C0BAction (?)";
        }
        if(field.equals("C18") || field.equals("C18PacketSpectate")){
            hints[0] = "Player Name (S)";
        }
        if(field.equals("C09") || field.equals("C09PacketHeldItemChange")){
            hints[0] = "Slot ID (I)";
        }
        if(field.equals("C00") || field.equals("C00PacketKeepAlive")){
            hints[0] = "Key (I)";
        }
        if(field.equals("C03") || field.equals("C03PacketPlayer")){
            hints[0] = "Ground (B)";
        }
        if(field.equals("C04") || field.equals("C04PacketPlayerPosition")){
            hints[0] = "PosX (D)";
            hints[1] = "PosY (D)";
            hints[2] = "PosZ (D)";
            hints[3] = "Ground (B)";
        }
        if(field.equals("C05") || field.equals("C05PacketPlayerPosition")){
            hints[0] = "Yaw (D)";
            hints[1] = "Pitch (D)";
            hints[2] = "Ground (B)";
        }
        if(field.equals("C06") || field.equals("C06PacketPlayerPosLook")){
            hints[0] = "PosX (D)";
            hints[1] = "PosY (D)";
            hints[2] = "PosZ (D)";
            hints[3] = "Yaw (D)";
            hints[4] = "Pitch (D)";
            hints[5] = "Ground (B)";
        }
        if(field.equals("C07") || field.equals("C07PacketPlayerDigging")){
            hints[0] = "PosX (D)";
            hints[1] = "PosY (D)";
            hints[2] = "PosZ (D)";
            hints[3] = "Face (?)";
            hints[4] = "C07Action (?)";
        }
        if(field.equals("C0C") || field.equals("C0CPacketInput")){
            hints[0] = "Strafe Speed (F)";
            hints[1] = "Forward Speed (F)";
            hints[2] = "Jumping (B)";
            hints[3] = "Sneaking (B)";
        }
        if(field.equals("C08") || field.equals("C08PacketPlayerBlockPlacement")){
            hints[0] = "BlockPos X (D)";
            hints[1] = "BlockPos Y (D)";
            hints[2] = "BlockPos Z (D)";
            hints[3] = "Face (?)";
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

        int i = 0;
        for(String s : hints){
            if(hints == null){
                return;
            }
            mc.fontRendererObj.drawString(s, width / 2 - 200, 70 + (i * 30) + 6, -1);
            int x = width / 2 - 200;
            int y= 70 + (i * 30) + 6;
            if(s != null){
                if(s.contains("?")){
                    if(mouseX >= x && mouseX <= x + (mc.fontRendererObj.getStringWidth(s)) && mouseY >= y && mouseY <= y + 24) {
                        if(s.toLowerCase().contains("C07Action".toLowerCase())){
                            mc.fontRendererObj.drawString("(START_DESTROY_BLOCK, DROP_ITEM etc...)", x + 310, y, -1);
                        }
                        if(s.toLowerCase().contains("C0BAction".toLowerCase())){
                            mc.fontRendererObj.drawString("(START_SNEAKING, OPEN_INVENTORY etc...)", x + 310, y, -1);
                        }
                        if(s.toLowerCase().contains("Face".toLowerCase())){
                            mc.fontRendererObj.drawString("(DOWN, UP, EAST, WEST, NORTH, SOUTH, SELF)", x + 310, y, -1);
                        }
                    }
                }
            }
            i++;
        }

        mc.fontRendererObj.drawString(packetStatus, (int) ((width/2f) - (mc.fontRendererObj.getStringWidth(packetStatus)/2f)), height - 24, -1);



        super.drawScreen(mouseX, mouseY, partialTicks);
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
        }

        if(mouseX >= msField.xPosition && mouseX <= msField.xPosition + msField.getWidth() && mouseY >= msField.yPosition && mouseY <= msField.yPosition + 24){
            for (GuiTextField argumentField : argumentFields) {
                argumentField.setFocused(false);
            }
            msField.setFocused(true);
        }

        for(GuiTextField field : argumentFields){
            if(mouseX >= field.xPosition && mouseX <= field.xPosition + field.getWidth() && mouseY >= field.yPosition && mouseY <= field.yPosition + 24){
                for (GuiTextField argumentField : argumentFields) {
                    argumentField.setFocused(false);
                }
                field.setFocused(true);
                packetNameField.setFocused(false);
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
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

        for(int i = 0; i < argumentFields.length; i++){
            // .packet packetname included
            args.add(argumentFields[i].getText());
            cachedFieldArgs[i] = argumentFields[i].getText();

            if(argumentFields[i].getText() != null){
                if(!argumentFields[i].getText().equals("")){
                    String formatted = argumentFields[i].getText()
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

        args.replaceAll(s -> s.replace("%x%", String.valueOf(mc.thePlayer.posX)));
        args.replaceAll(s -> s.replace("%y%", String.valueOf(mc.thePlayer.posY)));
        args.replaceAll(s -> s.replace("%z%", String.valueOf(mc.thePlayer.posZ)));


        Wrapper.instance.log(String.valueOf(args));

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
            String packetName = args.get(1); // Get the packet name (from args[1])
            //Wrapper.instance.log("Sending packet: " + packetName);


            switch (packetName){
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
            }
            switch (packetName) {
                case "C09PacketHeldItemChange":{
                    int slotId = Integer.parseInt(args.get(2));
                    cachedPacket = new C09PacketHeldItemChange(slotId);
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
                case "C05PacketPlayerPosition":{
                    double yaw = Double.parseDouble(args.get(2));
                    double pitch = Double.parseDouble(args.get(3));
                    boolean ground = Boolean.parseBoolean(args.get(4));
                    C03PacketPlayer.C05PacketPlayerLook pack = new C03PacketPlayer.C05PacketPlayerLook((float) yaw, (float) pitch, ground);
                    cachedPacket = pack;
                    //System.out.println("error parsing : " + args.get(4) + " to " + Boolean.parseBoolean(args.get(4)) + " to " + pack.isOnGround());
                    PacketUtil.sendSilentPacket(pack);
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
                    return;
                }
            }

            //Wrapper.instance.log("Packet sent successfully!");
            Noctura.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Packet Tweaker", "Sent " + packetName, 600));
        } catch (Exception e) {
            Wrapper.instance.log("Error sending packet: " + e.getMessage());
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
