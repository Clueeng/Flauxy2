package uwu.noctura.utils;

import net.minecraft.client.Minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
public class BlockUtil {
    // credits to YesCheatPlus for giving me this
    private static Minecraft mc = Minecraft.getMinecraft();
    public double yaw;
    public double pitch;
    
    public BlockUtil(double startX, double startY, double startZ, double endX, double endY, double endZ) {
    	double yawX = this.distance(startX,startZ, endX, endZ);
    	double yawY = endY - startY;
    	double yawDownCount = this.distance(0.0D,  0.0d, yawX, yawY);
    	double finalYaw = -Math.toDegrees(Math.asin(yawY));
    	double pitchX = endX - startX;
    	double pitchY = endZ - startZ;
    	double pitchDownCount = this.distance(0.0D, 0.0D, pitchX, pitchY);
    	double finalPitch = Math.toDegrees(Math.asin(pitchY));
    	finalPitch -= 90.0D;
    	if(startX > endX) {
    		finalPitch *= -1.0D;
    	}
    	this.yaw = finalYaw;
    	this.pitch = finalPitch;
    }

    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(mc.theWorld);
        var4.posX = (double) var0 + 0.5D;
        var4.posY = (double) var1 + 0.5D;
        var4.posZ = (double) var2 + 0.5D;
        var4.posX += (double) var3.getDirectionVec().getX() * 0.25D;
        var4.posY += (double) var3.getDirectionVec().getY() * 0.25D;
        var4.posZ += (double) var3.getDirectionVec().getZ() * 0.25D;
        return getDirectionToEntity(var4);
    }

    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{getYaw(var0) + mc.thePlayer.rotationYaw, getPitch(var0) + mc.thePlayer.rotationPitch};
    }

    public static float[] getRotationNeededForBlock(EntityPlayer paramEntityPlayer, BlockPos pos) {
        double d1 = pos.getX() - paramEntityPlayer.posX;
        double d2 = pos.getY() + 0.5 - (paramEntityPlayer.posY + paramEntityPlayer.getEyeHeight());
        double d3 = pos.getZ() - paramEntityPlayer.posZ;
        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
        float f1 = (float) (Math.atan2(d3, d1) * 180.0D / Math.PI) - 90.0F;
        float f2 = (float) -(Math.atan2(d2, d4) * 180.0D / Math.PI);
        return new float[]{f1, f2};
    }

    public static float getYaw(Entity var0) {
        double var1 = var0.posX - mc.thePlayer.posX;
        double var3 = var0.posZ - mc.thePlayer.posZ;
        double var5;

        if (var3 < 0.0D && var1 < 0.0D) {
            var5 = 90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else if (var3 < 0.0D && var1 > 0.0D) {
            var5 = -90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else {
            var5 = Math.toDegrees(-Math.atan(var1 / var3));
        }

        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) var5));
    }
    public double getYaw() {
    	return this.pitch;
    }
    public double getPitch() {
    	return this.yaw;
    }

    public static float getPitch(Entity var0) {
        double var1 = var0.posX - mc.thePlayer.posX;
        double var3 = var0.posZ - mc.thePlayer.posZ;
        double var5 = var0.posY - 1.6D + (double) var0.getEyeHeight() - mc.thePlayer.posY;
        double var7 = (double) MathHelper.sqrt_double(var1 * var1 + var3 * var3);
        double var9 = -Math.toDegrees(Math.atan(var5 / var7));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) var9);
    }
    
    public double distance(double value1, double value2, double x, double y) {
    	double a = Math.abs(value1 - x);
      	double b = Math.abs(value2 - y);
      	double c = Math.abs(a* a + b* b);
      	return c;
    }
}