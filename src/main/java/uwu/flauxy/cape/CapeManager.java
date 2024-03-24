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

    public CapeManager(){
        capes.add(new Cape(new ResourceLocation("capes/landscape.png")));
        capes.add(new Cape(new ResourceLocation("capes/purple.png")));
        capes.add(new Cape(new ResourceLocation("capes/symbol.png")));
        capes.add(new Cape(new ResourceLocation("capes/wave.png")));
    }



}
