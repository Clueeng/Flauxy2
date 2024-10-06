package uwu.noctura.commands.impl;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import uwu.noctura.commands.Command;
import uwu.noctura.utils.PacketUtil;
import uwu.noctura.utils.Wrapper;

import java.lang.reflect.Constructor;

public class CommandPacket extends Command {
    @Override
    public String getName() {
        return "packet";
    }

    @Override
    public String getSyntax() {
        return ".packet <name> <args>";
    }

    @Override
    public String getDescription() {
        return "Sends a specified packet";
    }

    @Override
    public void onCommandRun(Minecraft mc, String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: " + getSyntax());
        }

        String packetName = args[1];
        Packet<?> packet = null;
        switch (packetName){
            case "C03":{
                packetName = "C03PacketPlayer";
                break;
            }
            case "C04":{
                packetName = "C04PacketPlayerPosition";
                break;
            }
            case "C05":{
                packetName = "C05PacketPlayerLook";
                break;
            }
            case "C08":{
                packetName = "C08PacketPlayerBlockPlacement";
                break;
            }
            case "C00":{
                packetName = "C00PacketKeepAlive";
                break;
            }
            case "C0F":{
                packetName = "C0FPacketConfirmTransaction";
                break;
            }
            case "C0C":{
                packetName = "C0CPacketInput";
                break;
            }
            case "C07":{
                packetName = "C07PacketPlayerDigging";
                break;
            }
        }
        try {
            // Handle specific packets directly
            switch (packetName) {
                case "C08PacketPlayerBlockPlacement":
                    if (args.length < 7) {
                        Wrapper.instance.log("Usage for C08PacketPlayerBlockPlacement: .packet C08PacketPlayerBlockPlacement <x> <y> <z> <direction> <item_id>");
                        return;
                    }
                    BlockPos position = new BlockPos(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    int placedBlockDirection = Integer.parseInt(args[5]);
                    ItemStack stack = new ItemStack(Block.getBlockById(Integer.parseInt(args[6])));
                    packet = new C08PacketPlayerBlockPlacement(position, placedBlockDirection, stack, 0.0F, 0.0F, 0.0F);
                    break;

                case "C0CPacketInput":
                    if (args.length < 6) {
                        Wrapper.instance.log("Usage for C0CPacketInput: .packet C0CPacketInput <strafeSpeed> <forwardSpeed> <jumping> <sneaking>");
                        return;
                    }
                    float strafeSpeed = Float.parseFloat(args[2]);
                    float forwardSpeed = Float.parseFloat(args[3]);
                    boolean jumping = Boolean.parseBoolean(args[4]);
                    boolean sneaking = Boolean.parseBoolean(args[5]);
                    packet = new C0CPacketInput(strafeSpeed, forwardSpeed, jumping, sneaking);
                    break;

                case "C00PacketKeepAlive":
                    if (args.length < 2) {
                        Wrapper.instance.log("Usage for C00PacketKeepAlive: .packet C00PacketKeepAlive <key>");
                        return;
                    }
                    int key = Integer.parseInt(args[2]);
                    packet = new C00PacketKeepAlive(key);
                    Wrapper.instance.log(((C00PacketKeepAlive) packet).key + " key");
                    break;

                case "C0FPacketConfirmTransaction":
                    if (args.length < 5) {
                        Wrapper.instance.log("Usage for C0FPacketConfirmTransaction: .packet C0FPacketConfirmTransaction <windowId> <uid> <accepted>");
                        return;
                    }
                    int windowId = Integer.parseInt(args[2]);
                    short uid = Short.parseShort(args[3]);
                    boolean accepted = Boolean.parseBoolean(args[4]);
                    packet = new C0FPacketConfirmTransaction(windowId, uid, accepted);
                    break;

                case "C03":
                    Wrapper.instance.log("Conflict: Please specify the full packet name (C03PacketPlayer, C03PacketPlayer.C04PacketPlayerPosition, C03PacketPlayer.C05PacketPlayerLook, or C03PacketPlayer.C06PacketPlayerPosLook).");
                    return;

                case "C04PacketPlayerPosition":
                    if (args.length < 5) {
                        Wrapper.instance.log("Usage for C04PacketPlayerPosition: .packet C04PacketPlayerPosition <x> <y> <z> <onGround>");
                        return;
                    }
                    double posX = Double.parseDouble(args[2]);
                    double posY = Double.parseDouble(args[3]);
                    double posZ = Double.parseDouble(args[4]);
                    boolean onGround = Boolean.parseBoolean(args[5]);
                    packet = new C03PacketPlayer.C04PacketPlayerPosition(posX, posY, posZ, onGround);
                    break;

                case "C05PacketPlayerLook":
                    if (args.length < 5) {
                        Wrapper.instance.log("Usage for C05PacketPlayerLook: .packet C05PacketPlayerLook <yaw> <pitch> <onGround>");
                        return;
                    }
                    float playerYaw = Float.parseFloat(args[2]);
                    float playerPitch = Float.parseFloat(args[3]);
                    onGround = Boolean.parseBoolean(args[4]);
                    packet = new C03PacketPlayer.C05PacketPlayerLook(playerYaw, playerPitch, onGround);
                    break;

                case "C06PacketPlayerPosLook":
                    if (args.length < 6) {
                        Wrapper.instance.log("Usage for C06PacketPlayerPosLook: .packet C06PacketPlayerPosLook <x> <y> <z> <yaw> <pitch> <onGround>");
                        return;
                    }
                    posX = Double.parseDouble(args[2]);
                    posY = Double.parseDouble(args[3]);
                    posZ = Double.parseDouble(args[4]);
                    playerYaw = Float.parseFloat(args[5]);
                    playerPitch = Float.parseFloat(args[6]);
                    onGround = Boolean.parseBoolean(args[7]);
                    packet = new C03PacketPlayer.C06PacketPlayerPosLook(posX, posY, posZ, playerYaw, playerPitch, onGround);
                    break;

                case "C07PacketPlayerDigging":
                    if (args.length < 4) {
                        Wrapper.instance.log("Usage for C07PacketPlayerDigging: .packet C07PacketPlayerDigging <action> <x> <y> <z> <facing>");
                        return;
                    }
                    // Parse action
                    C07PacketPlayerDigging.Action action;
                    try {
                        action = C07PacketPlayerDigging.Action.valueOf(args[2].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        Wrapper.instance.log("Invalid action specified. Available actions: START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM");
                        return;
                    }
                    // Parse position
                    BlockPos diggingPosition = new BlockPos(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                    // Parse facing
                    EnumFacing facing = EnumFacing.getFront(Integer.parseInt(args[6]));
                    Wrapper.instance.log("Facing : " + facing);
                    packet = new C07PacketPlayerDigging(action, diggingPosition, facing);
                    break;

                default:
                    // Use reflection for other packets
                    String className = "net.minecraft.network.play.client." + packetName;
                    Class<?> packetClass = Class.forName(className);
                    Constructor<?> constructor = packetClass.getDeclaredConstructors()[0]; // Get the first constructor
                    Object[] constructorArgs = new Object[constructor.getParameterCount()];

                    // Initialize constructor arguments based on types
                    for (int i = 1; i < constructorArgs.length; i++) {
                        constructorArgs[i] = args[i + 1]; // Starting from the second argument
                    }
                    packet = (Packet<?>) constructor.newInstance(constructorArgs);
            }

            if (packet != null) {
                PacketUtil.sendSilentPacket(packet);
                if (packet instanceof C00PacketKeepAlive) {
                    Wrapper.instance.log("sent debug : " + ((C00PacketKeepAlive) packet).key);
                }
                Wrapper.instance.log("Sent packet: " + packetName);
            }
        } catch (ClassNotFoundException e) {
            Wrapper.instance.log("Packet not found: " + packetName);
        } catch (Exception e) {
            Wrapper.instance.log("Error sending packet: " + e.getMessage());
        }
    }
}