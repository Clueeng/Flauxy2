package net.minecraft.client;

public class ClientBrandRetriever
{
    public static String brand = "vanilla";
    public static String getClientModName()
    {
        return brand;
    }
    public static void setClientModName(String s){
        brand = s;
        Minecraft.getMinecraft().getPlayerUsageSnooper().addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
    }
}
