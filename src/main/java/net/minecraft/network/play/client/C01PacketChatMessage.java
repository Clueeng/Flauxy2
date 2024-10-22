package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import uwu.noctura.Noctura;

public class C01PacketChatMessage implements Packet<INetHandlerPlayServer>
{
    private String message;

    public C01PacketChatMessage()
    {
    }

    public C01PacketChatMessage(String messageIn)
    {
        System.out.println(Noctura.INSTANCE.MAX_CHAT_LENGTH);
        if (messageIn.length() > Noctura.INSTANCE.MAX_CHAT_LENGTH)
        {
            messageIn = messageIn.substring(0, Noctura.INSTANCE.MAX_CHAT_LENGTH);
        }

        this.message = messageIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.message = buf.readStringFromBuffer(Noctura.INSTANCE.MAX_CHAT_LENGTH);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.message);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processChatMessage(this);
    }

    public String getMessage()
    {
        return this.message;
    }
}
