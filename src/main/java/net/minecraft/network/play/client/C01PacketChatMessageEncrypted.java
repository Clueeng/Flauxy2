package net.minecraft.network.play.client;

import java.io.IOException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.PrivateKey;
import java.security.PublicKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import uwu.noctura.Noctura;

public class C01PacketChatMessageEncrypted implements Packet<INetHandlerPlayServer> {
    private String message;
    private byte[] signature;
    public C01PacketChatMessageEncrypted(String messageIn, PrivateKey privateKey) throws SignatureException {
        if (messageIn.length() > Noctura.INSTANCE.MAX_CHAT_LENGTH) {
            messageIn = messageIn.substring(0, Noctura.INSTANCE.MAX_CHAT_LENGTH);  // Enforce max message length
        }
        this.message = messageIn;
        this.signature = signMessage(messageIn, privateKey);  // Sign the message
    }

    // Method to sign the message with a private key
    private byte[] signMessage(String message, PrivateKey privateKey) throws SignatureException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");  // Example algorithm
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();  // Return the signed data
        } catch (Exception e) {
            throw new SignatureException("Error signing message", e);
        }
    }

    // Method to verify the message's signature using the public key
    public boolean verifySignature(PublicKey publicKey) throws SignatureException {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");  // Matching the algorithm
            sig.initVerify(publicKey);
            sig.update(message.getBytes());
            return sig.verify(signature);  // Verify if the signature matches
        } catch (Exception e) {
            throw new SignatureException("Error verifying message signature", e);
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.message = buf.readStringFromBuffer(Noctura.INSTANCE.MAX_CHAT_LENGTH);  // Read message
        int signatureLength = buf.readInt();  // Read the signature length
        this.signature = new byte[signatureLength];
        buf.readBytes(this.signature);  // Read the signature
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.message);  // Write the message
        buf.writeInt(this.signature.length);  // Write signature length
        buf.writeBytes(this.signature);  // Write the signature
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processChatMessage(this);
    }

    // Getter methods for the message and signature
    public String getMessage() {
        return this.message;
    }

    public byte[] getSignature() {
        return this.signature;
    }
}