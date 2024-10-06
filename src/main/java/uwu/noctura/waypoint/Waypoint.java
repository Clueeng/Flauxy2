package uwu.noctura.waypoint;

import net.minecraft.util.BlockPos;

public class Waypoint {
    public String name;
    public double x, y, z;
    public String identifier;

    public Waypoint(String name, double x, double y, double z, String identifier){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.identifier = identifier;
        // check later if loaded world's seed is equal to seed to render
    }

    public BlockPos toBlockPos(){
        return new BlockPos(x, y, z);
    }

}
