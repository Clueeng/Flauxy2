package uwu.flauxy.module.impl.ghost;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventReceivePacket;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventRender3D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.notification.Notification;
import uwu.flauxy.notification.NotificationType;
import uwu.flauxy.utils.BedwarsUtils;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.font.FontManager;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

@ModuleInfo(name = "BedwarsOverlay", displayName = "BedwarsOverlay", key = -1, cat = Category.Ghost)
public class BedwarsOverlay extends Module {

    public BedwarsOverlay(){
    }

    @Override
    public void onEnable() {
        reloadEverything();

        playerTraps.put("Trap", false);
        playerTraps.put("Mining Fatigue", false);
        playerTraps.put("Heal Pool", false);
    }
    boolean bedAlive = true;
    private ArrayList<String> infos = new ArrayList<>();
    private World currentWorld;
    ArrayList<String> allPlayers = new ArrayList<>();
    ArrayList<BlockPos> allBeds = new ArrayList<>();

    private ArrayList<String> getTabPlayerList() {
        final NetHandlerPlayClient var4 = mc.thePlayer.sendQueue;
        final List players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (final Object o : players) {
            final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            if(info.getGameProfile().getName().equals("UPGRADES") || info.getGameProfile().getName().equals("SHOP")) break;
            //Wrapper.instance.log(info.getGameProfile().getName() + " a");
            allPlayers.add(info.getGameProfile().getName());
        }
        return allPlayers;
    }

    private void reloadEverything(){
        currentWorld = mc.theWorld;
        allPlayers.clear();
        allPlayers = getTabPlayerList();
        colorMapping.put("#8000", "Green");
        colorMapping.put("#800080", "Pink");
        colorMapping.put("#FFFF00", "Yellow");
        colorMapping.put("#FF", "Blue");
        colorMapping.put("#00FFFF", "Aqua");
        colorMapping.put("#808080", "Gray");
        colorMapping.put("#FFFFFF", "White");
        colorMapping.put("#FF0000", "Red");
        //players.forEach(player -> Wrapper.instance.log(player + "") );

    }

    HashMap<String, String> colorMapping = new HashMap<>();
    HashMap<String, String> playersArmor = new HashMap<>();
    HashMap<String, String> playersColor = new HashMap<>();
    HashMap<String, Boolean> playerTraps = new HashMap<>();
    HashMap<String, String> playerEnchantments = new HashMap<>();
    int sharpLevel, protLevel;
    boolean gameStarted;

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventReceivePacket){
            EventReceivePacket ev = (EventReceivePacket) e;
            if(ev.getPacket() instanceof S02PacketChat){
                BedwarsUtils util = new BedwarsUtils();
                S02PacketChat chat = (S02PacketChat) ev.getPacket();
                String msg = chat.getChatComponent().getUnformattedText().toLowerCase();
                if(msg.contains(util.getTrapName(BedwarsUtils.Upgrades.SHARPNESS).toLowerCase())){
                    sharpLevel++;
                    playerEnchantments.put("Sharpness",String.valueOf(sharpLevel));
                }
                if(msg.contains(util.getStartGame(BedwarsUtils.Server.BLOCKSMC).toLowerCase())){
                    gameStarted = true;
                }
                if(msg.contains(util.getTrapName(BedwarsUtils.Upgrades.PROTECTION).toLowerCase())){
                    protLevel++;
                    playerEnchantments.put("Protection",String.valueOf(protLevel));
                }
                if(msg.contains(util.getTrapName(BedwarsUtils.Upgrades.TRAP).toLowerCase())){
                    playerTraps.put("Trap", true);
                }
                if(msg.contains(util.getTrapName(BedwarsUtils.Upgrades.MINING_FATIGUE).toLowerCase())){
                    playerTraps.put("Mining Fatigue", true);
                }
                if(msg.contains(util.getTrapName(BedwarsUtils.Upgrades.HEAL_POOL).toLowerCase())){
                    playerTraps.put("Heal Pool", true);
                }
                if(msg.contains(util.getDestroyedBed(BedwarsUtils.Server.BLOCKSMC).toLowerCase())){
                    bedAlive = false;
                    Flauxy.INSTANCE.getNotificationManager().addToQueue(new Notification(NotificationType.INFO, "Bedwars Overlay", "Your bed was destroyed"));
                    mc.thePlayer.playSound("mob.wither.death", 1.0f, 1.0f);
                }
            }
        }

        if(e instanceof EventUpdate){
            if(currentWorld != mc.theWorld || mc.thePlayer.ticksExisted % 50 == 0 || mc.thePlayer.ticksExisted <= 3){
                if(currentWorld != mc.theWorld){
                    gameStarted = false;
                    playerTraps.clear();
                    playersColor.clear();
                    playersArmor.clear();
                    playerTraps.put("Trap", false);
                    playerTraps.put("Mining Fatigue", false);
                    playerTraps.put("Heal Pool", false);
                    protLevel = 0;
                    sharpLevel = 0;
                    playerEnchantments.clear();
                    bedAlive = true;
                }
                reloadEverything();
                scanAreaForBeds(mc.theWorld, mc.thePlayer.getPosition(), 100);
                BedwarsUtils util = new BedwarsUtils();
                Iterator<String> iterator = playersArmor.keySet().iterator();
                while (iterator.hasNext()) {
                    String name = iterator.next();
                    if (!allPlayers.contains(name)) {
                        iterator.remove();
                        playersColor.remove(name);
                        if(allPlayers.size() <= 1){
                            mc.thePlayer.playSound("fireworks.blast", 2.0f, 1.0f);
                            Flauxy.INSTANCE.notificationManager.addToQueue(new Notification(NotificationType.INFO, "Bedwars Overlay", "Game won!"));
                        }else{
                            Flauxy.INSTANCE.notificationManager.addToQueue(new Notification(NotificationType.INFO, "Bedwars Overlay", name + " was final killed"));
                        }
                    }
                }
                for(String playerName : allPlayers){
                    //Wrapper.instance.log(playerName + " " + allPlayers.size());
                    EntityPlayer otherPlayer = mc.theWorld.getPlayerEntityByName(playerName);
                    if(otherPlayer != null){
                        ItemStack chestplate = otherPlayer.inventory.armorInventory[2]; // upgrade
                        ItemStack leggings = otherPlayer.inventory.armorInventory[1]; // team determination
                        if(leggings != null){
                            ItemArmor.ArmorMaterial material = util.getArmorMaterial(leggings);
                            playersArmor.put(otherPlayer.getName(),material.getName().toLowerCase());
                        }
                        if(chestplate != null){
                            ItemArmor.ArmorMaterial material = util.getArmorMaterial(chestplate);
                            String color = util.getLeatherColor(chestplate);
                            //Wrapper.instance.log(material.getName() + " " + otherPlayer.getName());
                            playersColor.put(otherPlayer.getName(), color);
                        }
                    }
                }
            }
        }
        if(e instanceof EventRender2D){
            int x = 10, y = 120, width = 240, height = 24 + 12;
            if(!gameStarted){
                mc.fontRendererObj.drawStringWithShadow("Not in a game", x, y, Color.RED.getRGB());

                return;
            }
            TTFFontRenderer font = Flauxy.INSTANCE.getFontManager().getFont("arial 19");
            final int addedHeight = 16;
            if(mc.getCurrentServerData() == null){
                font.drawString("Not on hypixel or blocksmc", x, y, Color.RED.getRGB());
                return;
            }
            String ip = mc.getCurrentServerData().serverIP;
            if(!ip.contains("hypixel") && !ip.contains("blocks")){
                font.drawString("Not on hypixel or blocksmc", x, y, Color.RED.getRGB());
                return;
            }
            if(allPlayers.isEmpty()){
                return;
            }


            infos.clear();

            height = addedHeight;
            int h = 0;
            mc.fontRendererObj.drawStringWithShadow(bedAlive ? "Bed alive" : "No bed",x,y, bedAlive ? Color.green.getRGB() : Color.red.getRGB());
            h += mc.fontRendererObj.FONT_HEIGHT + 4;
            for(String entry : playersArmor.keySet()){
                if(entry == null || playersArmor.isEmpty()) continue;
                String value = playersArmor.get(entry);
                String hex = playersColor.get(entry);

                if (hex.startsWith("#")) {
                    hex = hex.substring(1);
                }
                int colorInt = Integer.parseInt(hex, 16);
                Color c = new Color(colorInt);

                mc.fontRendererObj.drawStringWithShadow(entry + ": " + EnumChatFormatting.WHITE + value,x,y + h, c.getRGB());
                h += mc.fontRendererObj.FONT_HEIGHT;
            }
            int upgradeY = y + h + 12;
            int uh = 0;
            mc.fontRendererObj.drawStringWithShadow("Traps:",x,upgradeY + uh,-1);
            for(String entry : playerTraps.keySet()){
                uh += mc.fontRendererObj.FONT_HEIGHT;
                if(entry == null || playerTraps.isEmpty()) continue;
                String value = playerTraps.get(entry) ? "true" : "false";
                int trueOrFalse = value.equals("true") ? Color.green.getRGB() : Color.red.getRGB();
                mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + entry + ": " + EnumChatFormatting.RESET + value, x,upgradeY + uh,trueOrFalse);
            }
            int eh = 0;
            eh += mc.fontRendererObj.FONT_HEIGHT;
            mc.fontRendererObj.drawStringWithShadow("Upgrades:",x,upgradeY + uh + eh,-1);
            for(String entry : playerEnchantments.keySet()){
                eh += mc.fontRendererObj.FONT_HEIGHT;
                if(entry == null || playerEnchantments.isEmpty()) continue;
                String value = playerEnchantments.get(entry);
                mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + entry + ": " + EnumChatFormatting.RESET + value, x,upgradeY + uh + eh,-1);
            }
        }
    }

    public void scanAreaForBeds(World world, BlockPos centerPos, int radius) {
        if(centerPos == null){
            Wrapper.instance.log("Error, center pos is somehow null");
            return;
        }
        int startX = centerPos.getX() - radius;
        int endX = centerPos.getX() + radius;
        int startZ = centerPos.getZ() - radius;
        int endZ = centerPos.getZ() + radius;
        int startY = centerPos.getY() - 8;
        int endY = centerPos.getY() + 8;

        // Iterate through the defined area
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    // Check if the block is a bed
                    if (block instanceof BlockBed) {
                        IBlockState state = world.getBlockState(pos);
                        boolean isHead = state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD;
                        if(!isHead)return;
                        System.out.println("Bed found at: " + pos);
                        allBeds.add(pos);  // Store the bed position in the list
                    }
                }
            }
        }
    }

    public float[] setupW2S(double x, double y, double z){
        if(mc.thePlayer == null || mc.thePlayer.ticksExisted <= 1 || mc.thePlayer.rotationPitch >= 88 || mc.thePlayer.rotationPitch <= -88){
            return new float[]{-69, -69};
        }
        float[] coords = new float[2];
        convertTo2D(x, y, z, coords);
        return coords;
    }

    @Override
    public void onDisable() {
    }

    private void convertTo2D(double x3D, double y3D, double z3D, float[] coords) {
        if (coords == null) return;

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        double translatedX = x3D - camX;
        double translatedY = y3D - camY;
        double translatedZ = z3D - camZ;

        double[] screenPos = RenderUtil.project2D(translatedX, translatedY, translatedZ);

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float top = (mc.displayHeight / (float) scaledResolution.getScaleFactor());

        float w = (float) screenPos[2];
        if (w > 1 || w < 0) return;

        coords[0] = (float) screenPos[0];
        coords[1] = top - (float) screenPos[1];
    }

    public BlockPos findClosestNonBed(World world, BlockPos pos) {
        int radius = 0;

        while (radius < 3) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    BlockPos checkPos = pos.add(dx, 0, dz);
                    Block block = world.getBlockState(checkPos).getBlock();
                    if (!(block instanceof BlockBed)) {
                        return checkPos;
                    }
                }
            }
            radius++;
        }
        return null;
    }
}
