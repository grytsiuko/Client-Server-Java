package org.fidoshenyata.Lab2;

import lombok.Getter;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class Keys {

    @Getter
    private PublicKey publicKey;

    private KeyAgreement keyAgreement;
    private byte[] sharedSecret;

    private String ALGO = "AES";

    Keys() {
        makeKeyExchangeParams();
    }

    private void makeKeyExchangeParams() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(128);

            KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();

            keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(kp.getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Key generateKey() {
        return new SecretKeySpec(sharedSecret, ALGO);
    }

    public void setReceiverPublicKey(PublicKey publickey) {
        try {
            keyAgreement.doPhase(publickey, true);
            sharedSecret = keyAgreement.generateSecret();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
