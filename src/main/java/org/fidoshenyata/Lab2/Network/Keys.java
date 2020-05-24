package org.fidoshenyata.Lab2.Network;

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

    private String ALGO = "AES";

    Keys(){
        makeKeyExchangeParams();
    }

    public Key doHandShake(InputStream inputStream, OutputStream outputStream) throws IOException {
        Keys keys = new Keys();

        PublicKey publicKey = keys.getPublicKey();
        byte[] publicKeyEncoded = publicKey.getEncoded();

        outputStream.write(publicKeyEncoded.length);
        outputStream.write(publicKeyEncoded);
        outputStream.flush();

        int length = inputStream.read();
        byte[] inputKey = new byte[length];
        inputStream.read(inputKey);

        try {
            PublicKey serverPublicKey = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(inputKey));
            keys.setReceiverPublicKey(serverPublicKey);
            return keys.generateKey();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void makeKeyExchangeParams(){
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

    private Key generateKey() {
        return new SecretKeySpec(sharedSecret, ALGO);
    }

    private void setReceiverPublicKey(PublicKey publickey) throws InvalidKeyException {
        keyAgreement.doPhase(publickey, true);
        sharedSecret = keyAgreement.generateSecret();
    }
}
