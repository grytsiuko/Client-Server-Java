package org.fidoshenyata;

import org.fidoshenyata.model.Packet;
import org.fidoshenyata.validator.PacketBytesValidator;
import org.fidoshenyata.validator.Validator;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.Key;

public class PacketDecoder {

    private static final Validator<byte[]> packetBytesValidator = new PacketBytesValidator();

    private final Packet.PacketBuilder packetBuilder;
    private Key key;
    private final Cipher cipher;
    private boolean keyChanged;

    public PacketDecoder(String algorithm) throws Exception {
        cipher = Cipher.getInstance(algorithm);
        packetBuilder = Packet.builder();
    }

    public Packet decode(byte[] packetArray) throws Exception {
        if (key == null) {
            throw new IllegalStateException("A cipher key is needed");
        }
        if (!packetBytesValidator.isValid(packetArray)) {
            throw new IllegalArgumentException("The packet is corrupt");
        }

        ByteBuffer buffer = ByteBuffer.wrap(packetArray);

        return packetBuilder
                .source(buffer.get(1))
                .packetID(buffer.getLong(2))
                .commandType(buffer.getInt(16))
                .userID(buffer.getInt(20))
                .message(getMessage(buffer))
                .build();
    }

    public PacketDecoder setKey(Key key) {
        this.key = key;
        keyChanged = true;
        return this;
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