package io.eluv.format.eat;

import java.util.HashMap;

import io.eluv.crypto.KeyFactory;
import io.eluv.crypto.Signer;
import io.eluv.json.Json;

public class TokenBench {

    void doTestTokenBenchTest(Signer pk, int runCount) throws Exception {
        
        String sctx = "{\"foo\": \"bar\"}";
        String spcId = "ispc329GX6UVyuWzwPzqDHm5shxfNgrc";
        String libId = "ilib329GX6UVyuWzwPzqDHm5shxfNgrc";
        String qId   = "iq__329GX6UVyuWzwPzqDHm5shxfNgrc";
        Json json = new Json();
        @SuppressWarnings("unchecked")
        HashMap<String,Object> ctx = json.deserialize(sctx, HashMap.class);
        
        long t0 = System.currentTimeMillis();
        String tok = "";
        
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
            tok = es.signEncode(pk);
        }
        
        long d = System.currentTimeMillis() - t0;
        System.out.println("");
        System.out.println("Generated " + runCount + " tokens.");
        System.out.println("Duration: " + d + " millis");
        System.out.println("token: " + tok);
    }

    
    public static void main(String[] args) throws Exception {
        TokenBench t = new TokenBench();
        Signer pk = KeyFactory.createSigner("c205dfefd9885f368684ecdeb4e8079ba9d16350403c848da26f3106b83c18e6");
        t.doTestTokenBenchTest(pk, 10000);
        
        System.out.println();
        Natives.printReport();        
    }
    
}
