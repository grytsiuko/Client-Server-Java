package org.fidoshenyata.network.utils;

import lombok.Getter;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Keys {

    @Getter
    private PublicKey publicKey;

    private KeyAgreement keyAgreement;
    private byte[] sharedSecret;

    public Keys(){
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(128);

            KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();

            keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(kp.getPrivate());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public Key doHandShake(InputStream inputStream, OutputStream outputStream) throws IOException {
        PublicKey publicKey = getPublicKey();
        byte[] publicKeyEncoded = publicKey.getEncoded();

        outputStream.write(publicKeyEncoded.length);
        outputStream.write(publicKeyEncoded);
        outputStream.flush();

        int length = inputStream.read();
        byte[] inputKey = new byte[length];
        if (inputStream.read(inputKey) == -1)
            return null;

        try {
            PublicKey serverPublicKey = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(inputKey));
            setReceiverPublicKey(serverPublicKey);
            return generateKey();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
    }

    private Key generateKey() {
        return new SecretKeySpec(sharedSecret, "AES");
    }

    private void setReceiverPublicKey(PublicKey publickey) throws InvalidKeyException {
        keyAgreement.doPhase(publickey, true);
        sharedSecret = keyAgreement.generateSecret();
    }
}
