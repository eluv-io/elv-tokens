package io.eluv.format.eat;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.HashMap;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

import io.eluv.constants.Constants;
import io.eluv.crypto.Crypto;
import io.eluv.flate.Flate;

class TokenTest {
    
    
    public static ECKeyPair createPrivateKey() throws Exception {
        BigInteger privKey = Keys.createEcKeyPair().getPrivateKey();
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }
    
    public static ECKeyPair staticPrivateKey() {
        BigInteger privKey = new BigInteger("c205dfefd9885f368684ecdeb4e8079ba9d16350403c848da26f3106b83c18e6", 16);
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }
    
    
    @Test
    void testCreateKeyPair() throws Exception {
        ECKeyPair expected = staticPrivateKey();
        ECKeyPair kp = Crypto.KeyPairFrom("c205dfefd9885f368684ecdeb4e8079ba9d16350403c848da26f3106b83c18e6");
        assertEquals(expected, kp);

        kp = Crypto.KeyPairFrom("0xc205dfefd9885f368684ecdeb4e8079ba9d16350403c848da26f3106b83c18e6");
        assertEquals(expected, kp);
    }

    @Test
    void testSigningSample() throws Exception {
        ECKeyPair keyPair = createPrivateKey();
        System.out.println("Private key: " + keyPair.getPrivateKey().toString(16));
        System.out.println("Public key: " + keyPair.getPublicKey().toString(16));
        System.out.println("Public key (compressed): " + Crypto.compressPubKey(keyPair.getPublicKey()));

        String msg = "Message for signing";
        byte[] msgHash = Hash.sha3(msg.getBytes());
        Sign.SignatureData signature = Sign.signMessage(msgHash, keyPair, false);
        System.out.println("Msg: " + msg);
        System.out.println("Msg hash: " + Hex.toHexString(msgHash));
        System.out.printf("Signature: [v = %d, r = %s, s = %s]\n",
                signature.getV()[0] - 27,
                Hex.toHexString(signature.getR()),
                Hex.toHexString(signature.getS()));
        System.out.printf("Address: %s ", Hex.toHexString(Crypto.pubkeyToAddress(keyPair)));

        System.out.println();

        BigInteger pubKeyRecovered = Sign.signedMessageToKey(msg.getBytes(), signature);
        System.out.println("Recovered public key: " + pubKeyRecovered.toString(16));

        boolean validSig = keyPair.getPublicKey().equals(pubKeyRecovered);
        System.out.println("Signature valid? " + validSig);
        assertTrue(validSig);
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
    void testSign() throws Exception {
        ECKeyPair keyPair = staticPrivateKey();
        
        String message = "hello";
        byte[] signature = Crypto.sign(message.getBytes(), keyPair);
        signature = Crypto.adjustSignedBytes(signature);
        assertEquals(
            "e58f5a0fd01032c607103fde4ea65be179fdb81ba09d401206d2828f458e92ca06b776009320cb8e6a4db48ec1ea71c6034c3a91656a0b850ebf413aaf24d2a401",
            Hex.toHexString(signature));
    }
    
    @Test
    void testCompress() throws Exception {
        byte[] res = Flate.compressData("hello".getBytes());
        //System.out.println("compressed " + Hex.toHexString(res));
        
        byte[] dec = Flate.decompressData(res);
        assertEquals("hello", new String(dec));
    }
    
    
    static void fillSampleContext(HashMap<String, Object> ctx) {
        ctx.put(
            Constants.ElvDelegationId, 
            "iq__4567");
        ctx.put(
            "authorized_meta", 
            "/preferences");
        ctx.put(
            "authorized_files", 
            "/files/assets/birds2.jpg");
        ctx.put(
            "authorized_offerings",
            new String[] {
                "default",
                "special",
            });
    }
             
    @Test
    void testTokenEncode() throws Exception {
        ECKeyPair sk = staticPrivateKey();
        
        HashMap<String, Object> ctx = new HashMap<String, Object>();
        fillSampleContext(ctx);
        TokenFactory.EditorSigned es = new TokenFactory.EditorSigned(
            "ispc218Pn4tTNJELz8ASyV8o4KRggfoD", 
            "ilib3FfPwGraXTRgoq2Xu4oC7eJgT5Tj", 
            "iq__35BUYfYD44N2vZniVHrqaadrh8mC",
            true)
            .withAFGHPublicKey("my_afgh")
            .withExpiresIn(TokenFactory.HOUR * 4)
            .withContext(ctx);
        String stok = es.signEncode(sk);
        Token built = es.mToken;

        // build same manually
        Token tok = new Token(TokenType.EDITOR_SIGNED, TokenFormat.JSON_COMPRESSED);
        assertNotNull(tok.mTokenData);
        
        tok.mTokenData.Subject = "0x" + Hex.toHexString(Crypto.pubkeyToAddress(sk));
        tok.mTokenData.Grant = "read";
        tok.mTokenData.SID = "ispc218Pn4tTNJELz8ASyV8o4KRggfoD";
        tok.mTokenData.LID = "ilib3FfPwGraXTRgoq2Xu4oC7eJgT5Tj";
        tok.mTokenData.QID = "iq__35BUYfYD44N2vZniVHrqaadrh8mC";
        tok.mTokenData.AFGHPublicKey = "my_afgh";
        tok.mTokenData.IssuedAt = built.mTokenData.IssuedAt;
        tok.mTokenData.Expires = built.mTokenData.Expires;
        fillSampleContext(tok.mTokenData.Ctx);
        
        tok.signWith(sk);
        assertNotNull(tok.mTokenData.EthAddr);
        assertNotNull(tok.mSignature);
        String ser = tok.Encode();
        
        assertFalse(stok.length()==0);
        assertEquals(built, tok);
        
        System.out.println(ser);
        // PENDING(GG): add token signature verification
    }

}
