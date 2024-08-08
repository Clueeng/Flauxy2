package uwu.flauxy.cape;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

public class CapeManager {
    @Getter
    public int index;
    @Getter
    public static CapeManager INSTANCE;
    @Getter
    public ArrayList<Cape> capes = new ArrayList<>();

    public int CURRENT_CAPE = 0;
    public CapeManager(){
        capes.add(new Cape(new ResourceLocation("capes/landscape.png"), "Landscape", "Custom"));
        capes.add(new Cape(new ResourceLocation("capes/purple.png"), "Purple", "Custom"));
        capes.add(new Cape(new ResourceLocation("capes/symbol.png"), "Symbol", "Custom"));
        capes.add(new Cape(new ResourceLocation("capes/wave.png"), "Wave", "Custom"));
        capes.add(new Cape(new ResourceLocation("capes/cherry.png"), "Cherry", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/cobalt.png"), "Coblat", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/enderman.png"), "Enderman", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/migrator.png"), "Migration", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/mojira.png"), "Mojira", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/piston.png"), "Piston", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/prismarine.png"), "Prismarine", "Mojang"));
        capes.add(new Cape(new ResourceLocation("capes/realms.png"), "Realms", "Mojang"));
    }



}
