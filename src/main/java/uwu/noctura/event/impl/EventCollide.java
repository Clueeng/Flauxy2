package uwu.noctura.event.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import uwu.noctura.event.Event;

public class EventCollide extends Event {

    private final Entity entity;

    @Getter
    @Setter
    private final double posX;

    @Getter @Setter
    private final double posY;

    @Getter @Setter
    private final double posZ;

    @Getter @Setter
    private AxisAlignedBB boundingBox;

    @Getter @Setter
    private final Block block;

    public EventCollide(Entity entity, double posX, double posY, double posZ, AxisAlignedBB boundingBox, Block block) {
        this.entity = entity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.boundingBox = boundingBox;
        this.block = block;
    }
}
