package io.eluv.format.eat;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.HashMap;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;

import io.eluv.constants.Constants;
import io.eluv.crypto.Crypto;
import io.eluv.crypto.KeysTest;
import io.eluv.crypto.Signer;


class TokenTest {
    
    
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
        ECKeyPair sk = KeysTest.staticPrivateKey();
        
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
        assertNotNull(stok);
        Token built = es.mToken;
        
        BigInteger pubKeyRec = Sign.signedMessageToKey(
                built.mTokenBytes, 
                Crypto.signatureData(built.mSignature));
        //System.out.println("Recovered public key: " + pubKeyRec.toString(16));
        assertTrue(sk.getPublicKey().equals(pubKeyRec));
        

        // build the same manually
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
        
        tok.sign(new Signer.KeyPairSigner(sk));
        assertNotNull(tok.mTokenData.EthAddr);
        assertNotNull(tok.mSignature);
        String ser = tok.encode();
        
        assertEquals(built, tok);
        System.out.println("testTokenEncode: " + ser);
        
        BigInteger pubKeyRec2 = Sign.signedMessageToKey(
                tok.mTokenBytes, 
                Crypto.signatureData(tok.mSignature));
        assertTrue(sk.getPublicKey().equals(pubKeyRec2));
    }

}
