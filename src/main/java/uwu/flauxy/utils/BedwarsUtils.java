package uwu.flauxy.utils;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BedwarsUtils implements Utils{

    public String getWinMessage(Server server) {
        switch (server){
            default:
            case BLOCKSMC:
                return "global points for winning the game";
        }
    }

    public enum Server{
        BLOCKSMC;
    }

    public String getStartGame(Server server){
        switch (server){
            default:
                return "None";
            case BLOCKSMC:{
                return "You are now playing on the";
            }
        }
    }

    public String preGame(Server server){
        switch (server){
            default:
                return "None";
            case BLOCKSMC:{
                return "Warmup started.";
            }
        }
    }

    public enum Upgrades{
        TRAP,
        PROTECTION,
        MINING_FATIGUE,
        HEAL_POOL,
        SHARPNESS;
    }

    public String getDestroyedBed(Server server){
        switch (server){
            default:
                return "None";
            case BLOCKSMC:
                return "Your bed has been destroyed!";
        }
    }

    public ItemArmor.ArmorMaterial getArmorMaterial(ItemStack armorPiece) {
        if (armorPiece != null && armorPiece.getItem() instanceof ItemArmor) {
            ItemArmor armorItem = (ItemArmor) armorPiece.getItem();
            return armorItem.getArmorMaterial();
        }
        return null;
    }

    public String getLeatherColor(ItemStack leather) {
        if (leather != null && leather.getItem() instanceof ItemArmor) {
            ItemArmor armorItem = (ItemArmor) leather.getItem();
            if (armorItem.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                NBTTagCompound nbt = leather.getTagCompound();
                if (nbt != null && nbt.hasKey("display", 10)) {
                    NBTTagCompound displayTag = nbt.getCompoundTag("display");
                    if (displayTag.hasKey("color", 3)) {
                        int color = displayTag.getInteger("color");
                        return String.format("#%06X", color);
                    }
                }
            }
        }
        return "None";
    }


    public String getTrapName(Upgrades upgrade, Server server){
        switch (server){
            default:
            case BLOCKSMC:{
                switch (upgrade){
                    case TRAP:{
                        return "Trapper";
                    }
                    case PROTECTION:{
                        return "Reinforced Armor";
                    }
                    case MINING_FATIGUE:{
                        return "Miner Fatigue";
                    }
                    case SHARPNESS:{
                        return "Sharpened Swords";
                    }
                    case HEAL_POOL:{
                        return "Heal Pool";
                    }
                    default:
                        return "None";
                }
            }
        }

    }

}
