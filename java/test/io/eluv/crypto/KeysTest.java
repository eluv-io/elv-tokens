package io.eluv.crypto;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;

import org.bitcoin.NativeSecp256k1;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

public class KeysTest {

    public static String STATIC_PK = "c205dfefd9885f368684ecdeb4e8079ba9d16350403c848da26f3106b83c18e6";
    
    public static ECKeyPair staticPrivateKey() {
        BigInteger privKey = new BigInteger(STATIC_PK, 16);
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }
    
    
    @Test
    void testCreateKeyPair() throws Exception {
        ECKeyPair expected = staticPrivateKey();
        ECKeyPair kp = Crypto.KeyPairFrom(STATIC_PK);
        assertEquals(expected, kp);

        kp = Crypto.KeyPairFrom(STATIC_PK);
        assertEquals(expected, kp);
    }
    
    @Test
    void testPubKeyToAddress() throws Exception {
        ECKeyPair keyPair = staticPrivateKey();
        String addr = Hex.toHexString(Crypto.pubkeyToAddress(keyPair));
        assertEquals(
            "65419C9f653703ED7Fb6CC636cf9fda6cC024E2e".toLowerCase(), 
            addr.toLowerCase());
    }
    
    @Test
    void testSecp256k1() throws Exception {
        Secp256k1 kp = new Secp256k1(STATIC_PK);
        byte[] addr = kp.getAddress();
        assertEquals(
            "65419C9f653703ED7Fb6CC636cf9fda6cC024E2e".toLowerCase(), 
            Hex.toHexString(addr).toLowerCase());
    }
    
    
    @Test
    void testNativeSecp256k1Adress() throws Exception {
        byte[] privKeyBytes = Hex.decode(STATIC_PK);
        BigInteger privKey = new BigInteger(STATIC_PK, 16);
        
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        ECKeyPair ecpair = new ECKeyPair(privKey, pubKey);
        PrivateKey sk = new PrivateKey(ecpair);
        String addr1 = Hex.toHexString(sk.getAddress());
        String addr3 = Hex.toHexString(Crypto.pubkeyToAddress(ecpair));
        // 65419c9f653703ed7fb6cc636cf9fda6cc024e2e
        assertEquals(addr1, addr3);
        
        byte[] pubKeyBytes = NativeSecp256k1.computePubkey(privKeyBytes);
        // Crypto.pubkeyToAddress
        pubKeyBytes = Arrays.copyOfRange(pubKeyBytes, 1, pubKeyBytes.length); // remove prefix
        byte[] publicKey = Crypto.toBytesPadded(pubKeyBytes, Crypto.PUBLIC_KEY_SIZE);
        byte[] address = Keys.getAddress(publicKey);
        String addr2 = Hex.toHexString(address);
        assertEquals(addr1, addr2);
    }

}
