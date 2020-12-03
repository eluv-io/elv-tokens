package io.eluv.format.eat;


import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;

import io.eluv.crypto.KeysTest;
import io.eluv.crypto.PrivateKey;
import io.eluv.crypto.Secp256k1;
import io.eluv.crypto.Signer;
import io.eluv.json.Json;



class TokenBenchTest {

    //@Test
    void testTokenBenchKeyPair() throws Exception {
        ECKeyPair sk = KeysTest.staticPrivateKey();
        Signer pk = new Signer.KeyPairSigner(sk);
        doTestTokenBenchTest("EcKeyPair", pk, 10000);
        //duration: 10365 millis
    }
    
    //@Test
    void testTokenBenchPrivateKey() throws Exception {
        PrivateKey pk = new PrivateKey(KeysTest.staticPrivateKey());
        doTestTokenBenchTest("PrivateKey", pk, 10000);
        //duration: 10393 millis
    }
    
    @Test
    void testTokenBenchNative() throws Exception {
        Secp256k1 pk = new Secp256k1(KeysTest.STATIC_PK);
        doTestTokenBenchTest("Secp256k1", pk, 10000);
        //duration without encoding a/o base58: 2691 millis
        //duration: 7102 millis
    }
    
    void doTestTokenBenchTest(String testName, Signer pk, int runCount) throws Exception {
        
        String sctx = "{\"foo\": \"bar\"}";
        String spcId = "ispc329GX6UVyuWzwPzqDHm5shxfNgrc";
        String libId = "ilib329GX6UVyuWzwPzqDHm5shxfNgrc";
        String qId   = "iq__329GX6UVyuWzwPzqDHm5shxfNgrc";
        Json json = new Json();
        @SuppressWarnings("unchecked")
        HashMap<String,Object> ctx = json.deserialize(sctx, HashMap.class);
        
        long t0 = System.currentTimeMillis();
        
        for (int i=0; i< runCount; i++) {
            TokenFactory.EditorSigned es = new TokenFactory.EditorSigned(
                    spcId, 
                    libId, 
                    qId,
                    false)
                    //.withAFGHPublicKey("my_afgh")
                    .withExpiresIn(TokenFactory.HOUR * 24);
                    
            es.withDelegationId("iq__329GX6UVyuWzwPzqDHm5shxfNgrc");
            es.withContext(ctx);
            es.signEncode(pk);
        }
        
        long d = System.currentTimeMillis() - t0;
        System.out.println(testName + " duration: " + d + " millis");
        
    }
    

}
