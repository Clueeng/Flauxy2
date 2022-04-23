package uwu.flauxy.utils;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class MoveUtils {

    public static enum Bypass{
        VANILLA, VERUS;
    }
    public static double getSpeed() {
        Minecraft mc = Minecraft.getMinecraft();
        // nigga hypot heavy
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    /**
     * Sets current speed to itself make strafe
     */
    public static void strafe() {
        strafe(getSpeed());
    }

    public static void damage(Bypass by){
        Minecraft mc = Minecraft.getMinecraft();
        switch(by){
            case VERUS:{
                PacketUtil.packetNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
                PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.05, mc.thePlayer.posZ, false));
                PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                PacketUtil.packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.41999998688697815, mc.thePlayer.posZ, true));
                break;
            }
            case VANILLA:{
                for(int i = 0; i <= 4 / 0.0625; i++) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
                break;
            }
        }
    }

    public static boolean isOverVoid() {
        Minecraft mc = Minecraft.getMinecraft();
        for (double posY = mc.thePlayer.posY; posY > 0.0; posY--) {
            if (!(mc.theWorld.getBlockState(
                    new BlockPos(mc.thePlayer.posX, posY, mc.thePlayer.posZ)).getBlock() instanceof BlockAir))
                return false;
        }

        return true;
    }

    public static double getMotion() {
        Minecraft mc = Minecraft.getMinecraft();
        return distance(mc.thePlayer.prevPosX, mc.thePlayer.prevPosZ, mc.thePlayer.posX, mc.thePlayer.posZ);
    }

    public static double distance(final double srcX, final double srcZ,
                                  final double dstX, final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }
    public static double direction(){
        Minecraft mc = Minecraft.getMinecraft();
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0f) rotationYaw += 180f;
        float forward = 1f;
        if (mc.thePlayer.moveForward < 0f) forward = -0.5f; else if (mc.thePlayer.moveForward > 0f) forward = 0.5f;
        if (mc.thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward;
        if (mc.thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward;
        return Math.toRadians(rotationYaw);
    }
    public static double motionreset(){
        Minecraft.getMinecraft().thePlayer.motionX = 0;
        Minecraft.getMinecraft().thePlayer.motionZ = 0;
        return 0;
    }
    public static void strafe(double speed){
        Minecraft mc = Minecraft.getMinecraft();
        if(!mc.thePlayer.isMoving()) return;
        double yaw = direction();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
}
