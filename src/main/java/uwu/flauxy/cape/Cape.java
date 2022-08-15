package uwu.flauxy.cape;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;

public class Cape {
    @Getter
    public static Cape Instance;
    @Getter
    ResourceLocation location;

    public Cape(ResourceLocation location){
        this.location = location;
    }

    public Cape getCurrentCape(){
        return this;
    }
}
