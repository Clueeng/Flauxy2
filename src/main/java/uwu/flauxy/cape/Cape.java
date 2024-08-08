package uwu.flauxy.cape;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;

public class Cape {
    @Getter
    public static Cape Instance;
    @Getter
    ResourceLocation location;
    @Getter
    String name;
    @Getter
    String source;

    public Cape(ResourceLocation location, String name, String source){
        this.location = location;
        this.name = name;
        this.source = source;
    }

    public Cape getCurrentCape(){
        return this;
    }
}
