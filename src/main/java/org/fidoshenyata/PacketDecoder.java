package org.fidoshenyata;

import org.fidoshenyata.model.Packet;
import org.fidoshenyata.model.PacketBuilder;
import org.fidoshenyata.validator.PacketValidator;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.Key;

public class PacketDecoder {

    private final PacketValidator packetValidator;
    private final PacketBuilder packetBuilder;
    private Key key;
    private final Cipher cipher;
    private boolean keyChanged;

    public PacketDecoder(String algorithm) throws Exception {
        cipher = Cipher.getInstance(algorithm);
        packetValidator = new PacketValidator();
        packetBuilder = new PacketBuilder();
    }

    public Packet decode(byte[] packetArray) throws Exception {
        if (key == null) throw new IllegalStateException("A cipher key is needed");
        if (!packetValidator.isValid(packetArray)) throw new IllegalArgumentException("The packet is corrupt");
        ByteBuffer buffer = ByteBuffer.wrap(packetArray);

        return packetBuilder
                .setSource(buffer.get(1))
                .setPacketID(buffer.getLong(2))
                .setCommandType(buffer.getInt(16))
                .setUserID(buffer.getInt(20))
                .setMessage(getMessage(buffer))
                .build();

    }

    public void setKey(Key key) {
        this.key = key;
        keyChanged = !keyChanged;
    }

    private String getMessage(ByteBuffer buffer) throws Exception {
        int messageLength = buffer.getInt(10);
        byte[] message = new byte[messageLength - 8];
        buffer.position(24);
        buffer.get(message);
        if (keyChanged) {
            cipher.init(Cipher.DECRYPT_MODE, key);
            keyChanged = false;
        }
        return new String(cipher.doFinal(message));
    }
}