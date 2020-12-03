package io.eluv.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

public class SignTest {

    @Test
    void testECKeyPairSign() throws Exception {
        ECKeyPair keyPair = KeysTest.staticPrivateKey();
        
        String message = "hello";
        byte[] signature = Crypto.sign(message.getBytes(), keyPair);
        signature = Crypto.adjustSignedBytes(signature);
        assertEquals(
            "e58f5a0fd01032c607103fde4ea65be179fdb81ba09d401206d2828f458e92ca06b776009320cb8e6a4db48ec1ea71c6034c3a91656a0b850ebf413aaf24d2a401",
            Hex.toHexString(signature));
    }
    
    @Test
    void testSecp256k1Sign() throws Exception {
        Secp256k1 kp = new Secp256k1(KeysTest.STATIC_PK);
        
        String message = "hello";
        byte[] signature = Crypto.sign(message.getBytes(), kp);
        signature = Crypto.adjustSignedBytes(signature);
        assertEquals(
            "e58f5a0fd01032c607103fde4ea65be179fdb81ba09d401206d2828f458e92ca06b776009320cb8e6a4db48ec1ea71c6034c3a91656a0b850ebf413aaf24d2a401",
            Hex.toHexString(signature));
    }
    
    @Test
    void testNativeSecp256k1SignatureBench() throws Exception {
        Secp256k1 kp = new Secp256k1(KeysTest.STATIC_PK);
        String message = "hello";
        
        long t0 = System.currentTimeMillis();
        for  (int cc = 0; cc< 10000; cc++) {
            byte[] signature = Crypto.sign(message.getBytes(), kp);
            signature = Crypto.adjustSignedBytes(signature);
            assertEquals(
                "e58f5a0fd01032c607103fde4ea65be179fdb81ba09d401206d2828f458e92ca06b776009320cb8e6a4db48ec1ea71c6034c3a91656a0b850ebf413aaf24d2a401",
                Hex.toHexString(signature));
        }
        long d = System.currentTimeMillis() - t0;
        System.out.println("testNativeSecp256k1SignatureBench duration: " + d + " millis");
        // duration: 1993 millis
    }
    

    public static ECKeyPair createRandomPrivateKey() throws Exception {
        BigInteger privKey = Keys.createEcKeyPair().getPrivateKey();
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }
    

    @Test
    void testSigningSample() throws Exception {
        ECKeyPair keyPair = createRandomPrivateKey();
        //System.out.println("Private key: " + keyPair.getPrivateKey().toString(16));
        //System.out.println("Public key: " + keyPair.getPublicKey().toString(16));
        //System.out.println("Public key (compressed): " + Crypto.compressPubKey(keyPair.getPublicKey()));

        String msg = "Message for signing";
        byte[] msgHash = Hash.sha3(msg.getBytes());
        Sign.SignatureData signature = Sign.signMessage(msgHash, keyPair, false);
        //System.out.println("Msg: " + msg);
        //System.out.println("Msg hash: " + Hex.toHexString(msgHash));
        //System.out.printf("Signature: [v = %d, r = %s, s = %s]\n",
        //        signature.getV()[0] - 27,
        //        Hex.toHexString(signature.getR()),
        //        Hex.toHexString(signature.getS()));
        //System.out.printf("Address: %s ", Hex.toHexString(Crypto.pubkeyToAddress(keyPair)));

        //System.out.println();

        BigInteger pubKeyRecovered = Sign.signedMessageToKey(msg.getBytes(), signature);
        //System.out.println("Recovered public key: " + pubKeyRecovered.toString(16));

        boolean validSig = keyPair.getPublicKey().equals(pubKeyRecovered);
        //System.out.println("Signature valid? " + validSig);
        assertTrue(validSig);
    }    
    
}
