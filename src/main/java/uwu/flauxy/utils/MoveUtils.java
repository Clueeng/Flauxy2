package uwu.flauxy.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import uwu.flauxy.event.impl.packet.EventMove;

public class MoveUtils {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static enum Bypass{
        VANILLA, VERUS;
    }

    public static void stopMoving(){
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }


    public static double getBaseSpeed() {
        EntityPlayerSP player = mc.thePlayer;
        double baseSpeed = player.isSneaking() ? 0.0663D : (mc.thePlayer.isSprinting() ? 0.2873D : 0.221D);
        int amplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0) - (mc.thePlayer.isPotionActive(Potion.moveSlowdown) ? mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1 : 0);
        baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        return baseSpeed;
    }


    public static float getSpeedMotion() {
        return (float) Math.sqrt(Math.pow(mc.thePlayer.motionX, 2) + Math.pow(mc.thePlayer.motionZ, 2));
    }

    public static double getBaseSpeed(int speed, boolean sprinting) {
        EntityPlayerSP player = mc.thePlayer;
        double baseSpeed = player.isSneaking() ? 0.0663D : (sprinting ? 0.2873D : 0.221D);
        int amplifier = speed - (mc.thePlayer.isPotionActive(Potion.moveSlowdown) ? mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1 : 0);
        baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        return baseSpeed;
    }


    public static double getSpeed() {
        Minecraft mc = Minecraft.getMinecraft();
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

    public static void strafe(EventMove e, double speed){
        Minecraft mc = Minecraft.getMinecraft();
        if(!mc.thePlayer.isMoving()) return;
        double yaw = direction();
        e.setX(mc.thePlayer.motionX = -Math.sin(yaw) * speed);
        e.setZ(mc.thePlayer.motionZ = Math.cos(yaw) * speed);
    }

    public static boolean isOnLiquid() {
        Minecraft mc = Minecraft.getMinecraft();
        boolean onLiquid = false;
        final AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        final WorldClient world = mc.theWorld;
        final int y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;
        for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static void setSpeed(final double moveSpeed) {
        final float rotationYaw = mc.thePlayer.rotationYaw;
        final double strafe = MovementInput.moveStrafe;
        setSpeed(moveSpeed, rotationYaw, strafe, MovementInput.moveForward);
    }

    public static void setSpeed(final double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            } else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        if (strafe > 0.0) {
            strafe = 1.0;
        } else if (strafe < 0.0) {
            strafe = -1.0;
        }
        final double x = Math.cos(Math.toRadians(yaw + 90.0f));
        final double z = Math.sin(Math.toRadians(yaw + 90.0f));
        mc.thePlayer.motionX = forward * moveSpeed * x + strafe * moveSpeed * z;
        mc.thePlayer.motionZ = forward * moveSpeed * z - strafe * moveSpeed * x;
    }

    public static void setSpeed(EventMove event, final double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            } else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        if (strafe > 0.0) {
            strafe = 1.0;
        } else if (strafe < 0.0) {
            strafe = -1.0;
        }
        final double x = Math.cos(Math.toRadians(yaw + 90.0f));
        final double z = Math.sin(Math.toRadians(yaw + 90.0f));
        event.setX(forward * moveSpeed * x + strafe * moveSpeed * z);
        event.setZ(forward * moveSpeed * z - strafe * moveSpeed * x);
    }

    public static void setMotion(final double speed) {
        final MovementInput movementInput = mc.thePlayer.movementInput;
        double forward = MovementInput.moveForward;
        final MovementInput movementInput2 = mc.thePlayer.movementInput;
        double strafe = MovementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }
    public static void teleport(final double[] startPos, final BlockPos endPos){
        double distx = startPos[0] - endPos.getX()+ 0.5;
        double disty = startPos[1] - endPos.getY();
        double distz = startPos[2] - endPos.getZ()+ 0.5;
        double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
        double distanceEntreLesPackets = 5;
        double xtp, ytp, ztp = 0;

        if(dist> distanceEntreLesPackets){
            double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;
            xtp = mc.thePlayer.posX;
            ytp = mc.thePlayer.posY;
            ztp = mc.thePlayer.posZ;
            double count = 0;
            for (int i = 1; i < nbPackets;i++){
                double xdi = (endPos.getX() - mc.thePlayer.posX)/( nbPackets);
                xtp += xdi;

                double zdi = (endPos.getZ() - mc.thePlayer.posZ)/( nbPackets);
                ztp += zdi;

                double ydi = (endPos.getY() - mc.thePlayer.posY)/( nbPackets);
                ytp += ydi;
                count ++;
                C03PacketPlayer.C04PacketPlayerPosition Packet= new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, true);

                mc.thePlayer.sendQueue.addToSendQueue(Packet);
            }

            mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);
        }else{
            mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());
        }
    }
    public static void jump() {
        if(!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.jump();
        }
    }
    public static void jump(double motion) {
        if(!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.jump();
        }
        mc.thePlayer.motionY = motion;
    }
    public static boolean isWalking() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

}
