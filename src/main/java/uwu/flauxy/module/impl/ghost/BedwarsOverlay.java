package uwu.flauxy.module.impl.ghost;

import com.google.common.base.Predicate;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.impl.EventRender2D;
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
            if(currentWorld != mc.theWorld || mc.thePlayer.ticksExisted % 2 == 0 || mc.thePlayer.ticksExisted == 0){
                reloadEverything();
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

    @Override
    public void onDisable() {
    }
}
