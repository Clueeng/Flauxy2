package uwu.flauxy.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import uwu.flauxy.Flauxy;
import uwu.flauxy.event.Event;
import uwu.flauxy.event.EventType;
import uwu.flauxy.event.impl.*;
import uwu.flauxy.event.impl.packet.EventMove;
import uwu.flauxy.event.impl.packet.EventPacket;
import uwu.flauxy.module.Category;
import uwu.flauxy.module.Module;
import uwu.flauxy.module.ModuleInfo;
import uwu.flauxy.module.setting.impl.BooleanSetting;
import uwu.flauxy.module.setting.impl.ModeSetting;
import uwu.flauxy.module.setting.impl.NumberSetting;
import uwu.flauxy.utils.MoveUtils;
import uwu.flauxy.utils.PacketUtil;
import uwu.flauxy.utils.Wrapper;
import uwu.flauxy.utils.timer.Timer;
import uwu.flauxy.utils.timer.TimerUtil;

import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;
import static org.lwjgl.input.Keyboard.isKeyDown;

@ModuleInfo(name = "Fly", displayName = "Fly", key = Keyboard.KEY_G, cat = Category.Movement)
public class Fly extends Module {
    public Timer  timer = new Timer();
    public ArrayList<Packet> packets = new ArrayList<>();
    public ConcurrentLinkedQueue<Packet> blinkpackets = new ConcurrentLinkedQueue<>();

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Verus Funny", "Verus Packet", "Verus Packet2", "Verus Slow", "Collision", "Funcraft", "Funcraft2", "Spartan", "Spartan2", "Watchdog", "Minebox", "NCP", "Mineland", "Minemora Glide", "Blink", "Hycraft", "Hycraft2", "BlocksMC Funny", "MushMC");
    public NumberSetting blinkspeed = (NumberSetting) new NumberSetting("Speed", 3f, 1f, 10f, 0.01).setCanShow(m -> mode.is("Blink"));
    public NumberSetting val = (NumberSetting) new NumberSetting("Speed", 9f, 1f, 10f, 0.01).setCanShow(m -> mode.is("Vanilla"));
    public BooleanSetting YImprove = (BooleanSetting) new BooleanSetting("Y Improve", true).setCanShow(m -> mode.is("Vanilla"));
    public BooleanSetting baseSpeed  = (BooleanSetting) new BooleanSetting("Base Speed", false).setCanShow(m -> mode.is("Vanilla"));

    public NumberSetting funnytimer = (NumberSetting) new NumberSetting("Timer", 1,0.1,3,0.01).setCanShow(m -> mode.is("Verus Funny"));
    public NumberSetting funnyspeed = (NumberSetting) new NumberSetting("Speed", 3,1,5,0.01).setCanShow(m -> mode.is("Verus Funny"));

    public NumberSetting mushmc = (NumberSetting) new NumberSetting("Speed", 3,1,5,0.01).setCanShow(m -> mode.is("MushMC"));


    public NumberSetting Collisionspeed = (NumberSetting) new NumberSetting("Speed", 3,1,5,0.01).setCanShow(m -> mode.is("Collision"));
    public NumberSetting Collisiontimer = (NumberSetting) new NumberSetting("Timer", 1,0.1,3,0.01).setCanShow(m -> mode.is("Collision"));
    public BooleanSetting Collisiondamage  = (BooleanSetting) new BooleanSetting("Damage", false).setCanShow(m -> mode.is("Collision"));
    public BooleanSetting CollisionspoofY  = (BooleanSetting) new BooleanSetting("Spoof Y", false).setCanShow(m -> mode.is("Collision"));


    public boolean back,down,done, allowed, jumped;
    public int slotId;
    public boolean firstLagback = false;
    public int damagedTicks;
    public boolean damaged;
    public boolean wait;
    public double lastDist;
    public int stage;
    public boolean shouldgly;
    public boolean shouldBoost;
    public double counter;
    public int disable = 0;
    public int funcraftticks;
    public boolean canSpeed;
    public double moveSpeed;
    public int state = 0;
    private boolean blink;
    public boolean canGlide = false;
    public boolean ground;
    public int ticks = 0;
    public int eventtick;
    public int lasTick;
    public double playerSpeed;
    public boolean reset;
    public boolean y;
    public double x, z, speed;
    public int i = 0;
    public int offGroundTicks;
    public boolean flag;
    public double startY;
    public double boostState = 1;
    public double lastDistance = 0.0;
    public boolean failedStart = false;

    BlockPos oldPos;
    List<BlockPos> blockPosList = new ArrayList<>();
    boolean removeBlock = false;

    public Fly() {
        addSettings(mode, mushmc, Collisionspeed, Collisiontimer, Collisiondamage, CollisionspoofY, funnyspeed, funnytimer, blinkspeed, val, YImprove, baseSpeed);
    }
    
    
    public void onEvent(Event e) {
        if (e instanceof EventMotion) {
            EventMotion ev = (EventMotion) e;
            if (mode.is("MushMC")) {
                mc.timer.timerSpeed = 0.85f;
                MoveUtils.setSpeed(mushmc.getValue());
                if (mc.thePlayer.movementInput.jump) {
                    mc.thePlayer.motionY = 0.75;
                }
                if (mc.thePlayer.movementInput.sneak) {
                    mc.thePlayer.motionY = -0.75;
                }
                mc.thePlayer.motionY = 0.0;
                mc.thePlayer.motionY -= ((mc.thePlayer.ticksExisted % 10 == 0) ? 0.08 : 0.0);
            }

            if (mode.is("Verus Slow")) {
                if(ticks <= 1){
                    mc.thePlayer.motionY = 0.42f;
                }else{
                    if(mc.thePlayer.isCollidedVertically){
                        mc.thePlayer.motionY = 0.42f;
                        MoveUtils.strafe(MoveUtils.getSpeed() * 1.35f);
                    }else{
                        MoveUtils.strafe();
                    }
                    if(mc.thePlayer.fallDistance >= 0.2f){
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.9, mc.thePlayer.posZ);
                        if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir){
                            blockPosList.add(pos);
                            oldPos =  pos;
                            mc.theWorld.setBlock(pos, Blocks.barrier);
                            mc.thePlayer.fallDistance = 0;
                        }
                    }
                    if(mc.thePlayer.fallDistance >= 0 && mc.thePlayer.fallDistance <= 0.5f && oldPos != null && mc.theWorld.getBlockState(oldPos).getBlock() instanceof BlockBarrier){
                        removeBlock = true;
                    }
                    if(removeBlock){
                        if(timer.hasTimeElapsed(200, true)){
                            for(BlockPos bp : blockPosList){
                                mc.theWorld.setBlockToAir(bp);
                            }
                            mc.theWorld.setBlockToAir(oldPos);
                            removeBlock = false;
                        }
                    }


                }
                ticks++;
            }

            if (mode.is("Watchdog")) {
                if (damaged) {
                    Entity ent = this.mc.getRenderViewEntity();
                    double y = this.mc.thePlayer.posY - startY;
                    ent.posY = this.mc.getRenderViewEntity().posY - y;
                    resumeWalk();
                    if (wait) {
                        MoveUtils.setSpeed( 0);
                    } else if (mc.thePlayer.isMoving()) {
                        if (mc.thePlayer.onGround) {
                            if (shouldBoost) {
                                ev.setY(mc.thePlayer.motionY = 0.42F + 0.1 * (4 - (mc.thePlayer.isPotionActive(Potion.jump) ?
                                        mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0)));
                                moveSpeed *= 1.11;
                            } else {
                                moveSpeed = MoveUtils.getSpeed() * 2.3;
                            }
                        } else {
                            if(mc.thePlayer.motionY == 0.03196443960654492){
                                mc.thePlayer.motionY = -0.13;
                            }
                            moveSpeed *= 1.1;
                        }

                        MoveUtils.setSpeed(moveSpeed = Math.max(moveSpeed, MoveUtils.getSpeed()));
                        shouldBoost = mc.thePlayer.onGround;
                    }
                }
            }

            if (mode.is("Verus Packet")) {
                this.mc.timer.timerSpeed = 0.4f;
                this.mc.thePlayer.onGround = true;
                this.mc.thePlayer.motionY = 0.0;
                MoveUtils.setSpeed(5);
                if (this.timer.hasTimeElapsed(1585L, true)) {
                    MoveUtils.setSpeed(5);
                    if (this.mc.thePlayer.onGround) {
                        PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.035D, mc.thePlayer.posZ, false));
                        PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                        PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    }
                    this.mc.thePlayer.motionY = 0.25;
                }
            }

            if (mode.is("Verus Packet2")) {
                mc.timer.timerSpeed = 0.4f;
                mc.thePlayer.onGround = true;
                mc.thePlayer.motionY = 0.0;
                PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, true));
                if (mc.thePlayer.ticksExisted % 10 == 0) {
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.8, mc.thePlayer.posZ, true));
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.8, mc.thePlayer.posZ, true));
                    MoveUtils.setSpeed(5);
                } else {
                    MoveUtils.setSpeed(1.3);
                }

            }


            if (mode.is("Vulcan")) {
                if (e.getType() == EventType.PRE && canGlide) {
                    mc.timer.timerSpeed = 1f;
                    mc.thePlayer.motionY = -ticks % 2 == 0 ? 0.17 : 0.10;
                    if(ticks == 0) {
                        mc.thePlayer.motionY = -0.07;
                    }
                    ticks++;
                }
            }

            if (mode.is("Spartan2")) {
                mc.thePlayer.motionY = 0;
                if(timer.hasTimeElapsed(12, false)) {
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 8, mc.thePlayer.posZ, true));
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8, mc.thePlayer.posZ, true));
                    timer.reset();
                }
            }


            if (mode.is("Hycraft")) {
                mc.timer.timerSpeed = 1;
                if(mc.thePlayer.fallDistance > 0.0 && !done){
                    PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY +3.25, mc.thePlayer.posZ, true));
                    PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    mc.timer.timerSpeed = 0.2f;
                    done = true;
                }
                if(mc.thePlayer.fallDistance > 0.0 && done){
                    mc.thePlayer.motionY = 0.0;
                    MoveUtils.setSpeed(1.8);
                    mc.timer.timerSpeed = 0.6f;
                }
            }

            if (mode.is("Hycraft2")) {
                mc.thePlayer.motionY = 0;
                MoveUtils.setSpeed(2);
                mc.timer.timerSpeed = 0.25f;
                if (mc.thePlayer.ticksExisted % 6 == 0) {
                    tp();
                }
            }

            if (mode.is("Minemora Glide")) {
                mc.timer.timerSpeed =  0.7f;
                if (mc.thePlayer.isMoving()) {
                    mc.thePlayer.motionY = -0.0784000015258789;
                }
            }

            if (mode.is("Funcraft2")) {
                if(startY > mc.thePlayer.posY)
                    mc.thePlayer.motionY = -0.000000000000000000000000000000001D;

                if(mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -0.2D;

                if(mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.posY < (startY - 0.1D))
                    mc.thePlayer.motionY = 0.2D;
                MoveUtils.strafe(0.0f);
                MoveUtils.setSpeed(mc.thePlayer.ticksExisted % 10 == 0 ? MoveUtils.getBaseSpeed() * 1.3 : MoveUtils.getBaseSpeed() );
            }


            if (mode.is("Collision")) {
                MoveUtils.setSpeed(Collisionspeed.getValue());
                mc.timer.timerSpeed = (float) Collisiontimer.getValue();
                if (CollisionspoofY.isEnabled()) {
                    switch (funcraftticks) {
                        case 0:
                            ((EventMotion) e).setY(-0.1D-10D);
                            break;
                        case 1:
                            ((EventMotion) e).setY((float) (+0.1D-10D));
                            break;
                        case 2:
                            funcraftticks = 0;
                            break;
                    }
                    funcraftticks++;
                }

            }

            if(mode.is("Blink")) {
                MoveUtils.setSpeed(blinkspeed.getValue());
                mc.thePlayer.motionY = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = 0.1f;
                } else if (mc.thePlayer.isSneaking()) {
                    mc.thePlayer.motionY = -0.1f;
                }
            }

            if (mode.is("BlocksMC Funny")) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
            }



            if (mode.is("Vanilla")) {
                mc.thePlayer.motionY = 0;

                mc.thePlayer.capabilities.isFlying = true;
                mc.thePlayer.capabilities.allowFlying = true;
                if (!baseSpeed.isEnabled()) {
                    MoveUtils.setSpeed(val.getValue());
                }
                if (YImprove.isEnabled()) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY = 1f;
                    } else if (mc.thePlayer.isSneaking()) {
                        mc.thePlayer.motionY = -1f;
                    }
                }
            }



            if (mode.is("Spartan")) {
                if (e.getType() == EventType.PRE) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }

                    if (mc.thePlayer.fallDistance > 1) {
                        mc.thePlayer.motionY = -((mc.thePlayer.posY) - Math.floor(mc.thePlayer.posY));
                    }

                    if (mc.thePlayer.motionY == 0) {
                        mc.thePlayer.jump();

                        mc.thePlayer.onGround = true;
                        ((EventMotion) e).setOnGround(true);
                        mc.thePlayer.fallDistance = 0;
                    }
                }
            }

            if (mode.is("NCP")) {
                if (mc.thePlayer.hurtTime == 9) {
                    damaged = true;
                }

                if (!damaged) {
                    stopWalk();
                    if (mc.thePlayer.ticksExisted - ticks == 3) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -89.5f, true));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));

                        if (i != slotId) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slotId));
                        }
                    }
                }

                if (damaged) {
                    resumeWalk();
                    if (e.getType() == EventType.PRE) {
                        if (timer.hasTimeElapsed(2, false)) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
                            timer.reset();
                        }
                        if (!failedStart) mc.thePlayer.motionY = 0.0;
                    } else {
                        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                        lastDistance = sqrt(xDist * xDist + zDist * zDist);
                    }
                }
            }


            if (mode.is("Verus Funny")) {
                if (!mc.thePlayer.onGround) {
                    MoveUtils.setSpeed(funnyspeed.getValue());
                    mc.thePlayer.motionY = 0.0;
                    mc.timer.timerSpeed = (float) funnytimer.getValue();
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.thePlayer.ticksExisted % 10 == 0) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY = 1;
                        }
                    }
                }
            }

            if (mode.is("Verus") && damaged) {
                final double x = mc.thePlayer.posX;
                final double y = mc.thePlayer.posY;
                final double z = mc.thePlayer.posZ;
                if (ticks == 0) {
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 3.001, z, false));
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                }
                if (state == 2 || ticks >= 11 && mc.thePlayer.isMoving() && !damaged) {
                    damagedTicks = 0;
                    damaged = true;
                }
                if (!mc.thePlayer.isMoving()) {
                    MoveUtils.setMotion(0.0);
                }
                if (!damaged) {
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                }
                else {
                    if (damagedTicks == 0) {
                        mc.thePlayer.jump();
                    }
                    else {
                        mc.thePlayer.motionY = 0.0;
                        if (mc.thePlayer.hurtTime > 0) {
                            MoveUtils.setSpeed(1.8);
                        }
                        else {
                            MoveUtils.setSpeed(MoveUtils.getSpeed());
                        }
                    }
                    ((EventMotion) e).setOnGround(true);
                    ++damagedTicks;
                }
                ++ticks;
            }

            if(mode.is("Minebox")) {
                this.blink = true;
                mc.timer.timerSpeed = 0.2F;
                ((EventMotion) e).y = mc.thePlayer.motionY = mc.gameSettings.keyBindJump.pressed ? 0.44999998807907104D : (mc.gameSettings.keyBindSneak.pressed ? -0.20000000298023224D : 0.0D);
                if (mc.thePlayer.isMoving()) {
                    MoveUtils.setSpeed(6.0);
                } else {
                    ((EventMotion) e).setZ(((EventMotion) e).x = 0.0D);
                }
            }

            if (mode.is("Funcraft")) {
            }
        }

        if (e instanceof EventCollide) {
            EventCollide ec = (EventCollide) e;
            if (mode.is("Collision") && !mc.thePlayer.isSneaking()) {
                if (mc.thePlayer.isSneaking())
                    return;
                if (ec.getBlock() instanceof net.minecraft.block.BlockAir && ec.getPosY() < mc.thePlayer.posY)
                    ec.setBoundingBox(AxisAlignedBB.fromBounds(ec.getPosX(), ec.getPosY(), ec.getPosZ(), ec.getPosX() + 1.0D, mc.thePlayer.posY, ec.getPosZ() + 1.0D));
            }

            if (mode.is("Verus Packet2")) {
                if (mc.thePlayer.isSneaking())
                    return;
                if (ec.getBlock() instanceof net.minecraft.block.BlockAir && ec.getPosY() < mc.thePlayer.posY)
                    ec.setBoundingBox(AxisAlignedBB.fromBounds(ec.getPosX(), ec.getPosY(), ec.getPosZ(), ec.getPosX() + 1.0D, mc.thePlayer.posY, ec.getPosZ() + 1.0D));
            }

            if (mode.is("NCP")) {
                if (damaged) {
                    if (ec.getBlock() instanceof BlockAir && ec.getPosY() <= mc.thePlayer.posY) {
                        ec.setBoundingBox(AxisAlignedBB.fromBounds(ec.getPosX(), ec.getPosY(), ec.getPosZ(), ec.getPosX() + 1.0D, mc.thePlayer.posY, ec.getPosZ() + 1.0D));
                    }
                }
            }
        }

        if (e instanceof EventUpdate) {
            lasTick = (int) mc.thePlayer.getLastTickDistance();
            playerSpeed = mc.thePlayer.getRealMoveSpeed(true, 0.2D);
            if (mode.is("Hycraft")) {
                if(mc.thePlayer.onGround){
                    mc.thePlayer.jump();
                }
                if(mc.thePlayer.fallDistance > 0.0 && done){
                    mc.thePlayer.motionY = 0.0;
                }
            }

            if (mode.is("Watchdog")) {
                if (eventtick > 70) {
                    if (mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 1;
                        toggle();
                    }
                }

            }
        }

        if (e instanceof EventPacket) {
            EventPacket ep = (EventPacket) e;

            if (mode.is("Watchdog")) {
                if(wait){
                    if (ep.getPacket() instanceof C03PacketPlayer || ep.getPacket() instanceof C08PacketPlayerBlockPlacement || ep.getPacket() instanceof C07PacketPlayerDigging || ep.getPacket() instanceof C09PacketHeldItemChange) {
                        ep.setCancelled(true);
                    }
                }
            }

            if (mode.is("NCP")) {
                if (damaged) {
                    if (ep.getPacket() instanceof C03PacketPlayer) {
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) ep.getPacket();
                        c03PacketPlayer.setOnGround(true);
                    }
                    if (ep.getPacket() instanceof S08PacketPlayerPosLook) {
                        failedStart = true;
                    }
                }
            }


            if (mode.is("Minebox")) {
                if (ep.getPacket() instanceof net.minecraft.network.play.client.C0FPacketConfirmTransaction)
                    ep.setCancelled(true);
                if (ep.getPacket() instanceof net.minecraft.network.play.client.C00PacketKeepAlive)
                    ep.setCancelled(true);
                if (blink && ep.getPacket() instanceof net.minecraft.network.play.client.C03PacketPlayer)
                    ep.setCancelled(true);
            }

            if (mode.is("Funcraft")) {

            }


            if (mode.is("Blink")) {
                if (ep.getPacket() instanceof C0BPacketEntityAction || ep.getPacket() instanceof C03PacketPlayer || ep.getPacket() instanceof C02PacketUseEntity || ep.getPacket() instanceof C0APacketAnimation || ep.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                    blinkpackets.add(ep.getPacket());
                    ep.setCancelled(true);
                }
            }

            if (mode.is("Verus") && damaged) {
                if (ep.getPacket() instanceof C0FPacketConfirmTransaction) {
                    final C0FPacketConfirmTransaction c0f = (C0FPacketConfirmTransaction)ep.getPacket();
                    if (c0f.getWindowId() < 5.0) {
                        ep.setCancelled(true);
                        PacketUtil.sendSilentPacket(new C0FPacketConfirmTransaction(ThreadLocalRandom.current().nextInt(100, 500), (short)500, true));
                    }
                }
                if (ep.getPacket() instanceof C03PacketPlayer) {
                    final C03PacketPlayer packetPlayer = (C03PacketPlayer)ep.getPacket();
                    packetPlayer.setOnGround(true);
                    if (mc.thePlayer.ticksExisted > 15) {
                        ep.setCancelled(mc.thePlayer.ticksExisted % 3 == 0);
                    }
                }
            }
        }

        if (e instanceof EventMove) {
            EventMove em = (EventMove) e;
            if (mode.is("Verus")) {
                if (!damaged) {
                    em.setX(0.0);
                    em.setZ(0.0);
                }
            }

            if(mode.is("Watchdog") && !damaged) {
                stopWalk();
                ++this.ticks;
                if (this.ticks > 52) {
                    int i = 0;
                    while ((double)i <= 64.0) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, (double)i == 64.0));
                        damaged = true;
                        i = (short)(i + 1);
                    }
                }
            }

            if (mode.is("NCP")) {
                if (damaged) {
                    if (!isMoving()) {
                        em.setX(0.0);
                        em.setZ(0.0);
                        return;
                    }

                    if (failedStart) {
                        return;
                    }

                    moveSpeed = mc.thePlayer.ticksExisted % 35 == 0 ? 1.178654 : MoveUtils.getBaseSpeed();

                    double yaw = getDirection();
                    em.setX(-sin(yaw) * moveSpeed);
                    em.setZ(cos(yaw) * moveSpeed);
                    mc.thePlayer.motionX = em.getX();
                    mc.thePlayer.motionZ = em.getZ();
                }
            }
        }

        if (e instanceof EventTick) {
            eventtick++;

            if (mode.is("Verus Funny")) {
                if(mc.thePlayer.ticksExisted % 175 == 0) {
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ, false));
                    PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    mc.thePlayer.motionY = 0.0;
                }
            }

            if (mode.is("BlocksMC Funny")) {
                if (!mc.thePlayer.onGround) {
                    MoveUtils.setSpeed(9.5);
                    mc.thePlayer.motionY = 0;
                    mc.timer.timerSpeed = 0.5f;
                } else{
                    MoveUtils.setSpeed(0.3);
                    mc.thePlayer.jump();
                }
            }
        }
    }





    @Override
    public void onEnable() {
        shouldBoost = false;
        firstLagback = false;;
        canSpeed = false;
        shouldgly = false;
        blink = true;
        damaged = false;
        done = false;
        wait = false;
        ticks = 0;
        eventtick = 0;
        funcraftticks = 0;
        lastDist = 0;
        ground = false;
        stage = 0;
        jumped = false;
        offGroundTicks = 0;
        flag = false;
        state = 0;
        damagedTicks = 0;
        blinkpackets.clear();

        if(mode.is("Watchdog") || mode.is("Funcraft2")) {
            startY = mc.thePlayer.posY;
        }

        if (mode.is("NCP")) {
            damaged = false;

            if (!mc.thePlayer.inventory.hasItem(Items.arrow)) {
                toggle();
                return;
            }

            ItemStack itemStack = null;

            for (i = 0; i < 9; i++) {
                itemStack = mc.thePlayer.inventory.mainInventory[i];
                if (itemStack != null && itemStack.getItem() instanceof ItemBow)
                    break;
            }

            if (i == 9) {
                Wrapper.instance.log("You need a bow");
                toggle();
                return;
            } else {
                slotId = mc.thePlayer.inventory.currentItem;
                if (i != slotId) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(i));
                }
                ticks = mc.thePlayer.ticksExisted;
                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(itemStack));
            }

            if (damaged) {
                if (!mc.thePlayer.onGround) {
                    return;
                }

                for (int i = 0; i < 10; i++) {
                    {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    }

                    double fallDistance = 3.0125;


                    while (fallDistance > 0) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212, mc.thePlayer.posZ, false));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000013579, mc.thePlayer.posZ, false));
                        fallDistance -= 0.7531999805212;
                    }
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

                    mc.thePlayer.jump();
                    mc.thePlayer.posY += 0.42;

                    boostState = 1;
                    moveSpeed = 0.1;
                    lastDistance = 0.0;
                    failedStart = false;
                }
            }
        }

        if (mode.is("Collision")) {
            if (Collisiondamage.isEnabled()) {
                PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.035D, mc.thePlayer.posZ, false));
                PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            }
        }

        if (mode.is("Watchdog")) {
            if (bowSlot() == -1 || !mc.thePlayer.inventory.hasItem(Items.arrow)) {

                toggle();
                return;
            }
        }

        if (mode.is("Verus Funny")){
            mc.timer.timerSpeed = (float) funnytimer.getValue();
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ, false));
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05, mc.thePlayer.posZ, true));
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.13, mc.thePlayer.posZ, true));
        }


        if (mode.is("Verus")) {
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.035D, mc.thePlayer.posZ, false));
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            PacketUtil.sendSilentPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            damaged = true;
        }

    }


    @Override
    public void onDisable() {
        if (mode.is("Funcraft")) {
            MoveUtils.strafe((float) (MoveUtils.getSpeed() / 2.0));
        }

        if (mode.is("Blink")) {
            for (Packet p : blinkpackets) {
                PacketUtil.sendPacket(p);
            }
        }

        if(!blockPosList.isEmpty()){

            for(BlockPos bp : blockPosList){
                mc.theWorld.setBlockToAir(bp);
            }
            blockPosList.clear();
        }
        if(oldPos != null){
            mc.theWorld.setBlockToAir(oldPos);
        }
        removeBlock = false;
        blockPosList.clear();
        removeBlock = false;

        shouldgly = false;
        ticks = (int) 0.0;
        blink = false;
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.capabilities.isCreativeMode = false;
        mc.thePlayer.capabilities.allowFlying = false;
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.speedInAir = 0.02f;
        mc.thePlayer.stepHeight = 0.5f;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
    }

    private int bowSlot() {
        return mc.thePlayer.getSlotByItem(Items.bow);
    }

    public void resumeWalk() {
        mc.gameSettings.keyBindForward.pressed = isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
    }

    public void stopWalk() {
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
    }

    public void tp() {
        PacketUtil.sendPacket((Packet) new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 50, mc.thePlayer.posZ, false));
    }


    public float getDirection() {
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float forward = Minecraft.getMinecraft().thePlayer.moveForward;
        float strafe = Minecraft.getMinecraft().thePlayer.moveStrafing;
        yaw += (float)(forward < 0.0f ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += (float)(forward < 0.0f ? -45 : (forward == 0.0f ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= (float)(forward < 0.0f ? -45 : (forward == 0.0f ? 90 : 45));
        }
        return yaw * ((float)Math.PI / 180);
    }


}
