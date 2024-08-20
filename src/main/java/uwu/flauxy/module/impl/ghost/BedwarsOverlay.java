package uwu.flauxy.module.impl.ghost;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
import uwu.flauxy.event.impl.EventRender3D;
import uwu.flauxy.event.impl.EventUpdate;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.font.FontManager;
import uwu.flauxy.utils.font.TTFFontRenderer;
import uwu.flauxy.utils.render.ColorUtils;
import uwu.flauxy.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ModuleInfo(name = "BedwarsOverlay", displayName = "BedwarsOverlay", key = -1, cat = Category.Ghost)
public class BedwarsOverlay extends Module {

    public BedwarsOverlay(){
    }

    @Override
    public void onEnable() {

    }
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

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate){
            if(currentWorld != mc.theWorld || mc.thePlayer.ticksExisted % 50 == 0 || mc.thePlayer.ticksExisted <= 3){
                reloadEverything();
                Wrapper.instance.log("Reloaded Bedwars map");
                scanAreaForBeds(mc.theWorld, mc.thePlayer.getPosition(), 100);
            }
        }
        if(e instanceof EventRender2D){
            EventRender2D ev = (EventRender2D) e;
            ScaledResolution sr = new ScaledResolution(mc);
            for(BlockPos bed : allBeds){
                float[] pos = setupW2S(bed.getX(), bed.getY(), bed.getZ());
                if(pos[0] == -69 && pos[1] == -69){
                    return;
                }
                if(pos[0] < 0 || pos[0] > sr.getScaledWidth() || pos[1] < 0 || pos[1] > sr.getScaledHeight())return;
                mc.fontRendererObj.drawString("bed test", (int) pos[0], (int) pos[1], -1);
            }
        }
        if(e instanceof EventRender2D){
            TTFFontRenderer font = Flauxy.INSTANCE.getFontManager().getFont("arial 19");
            int x = 10, y = 120, width = 240, height = 24 + 12;
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
            if(allPlayers.isEmpty() || allPlayers == null){
                return;
            }


            infos.clear();

            height = addedHeight;


            Gui.drawRect(x, y, x + width, y + (addedHeight * (infos.size()+1)), new Color(0, 0, 0, 120).getRGB());

            for(int i = 0; i < infos.size(); i++){
                font.drawString(infos.get(i), x + 4, y + (addedHeight * i+1) + 2, -1);
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
