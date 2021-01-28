package io.eluv.format.eat;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.eluv.json.Json;

public class TokenSign {

    private static void print(String msg) {
        System.out.println(msg);
    }
    
    private static void invalid(String msg, boolean printUsage) {
        print(msg);
        if (printUsage) {
            print("");
            usage();
        }
        System.exit(1);
    }
    
    private static void usage() {
        print("usage:");
        print("TokenSign private_key spaceId libraryId contentId [delegationId] [json context] [subject]");
        print("  create an editor-signed token valid for 24 hours");
    }
    

    public static void main(String[] args) throws Exception {
        int minArgs = 4;
        if (args.length < minArgs) {
            invalid("At least "+minArgs+" arguments are expected", true);
        }
        
        TokenFactory.EditorSigned es = new TokenFactory.EditorSigned(
            args[1], 
            args[2], 
            args[3],
            false)
            //.withAFGHPublicKey("my_afgh")
            .withExpiresIn(TokenFactory.HOUR * 24);
        
        if (args.length > minArgs) {
            es.withDelegationId(args[minArgs]);
        }
        if (args.length > minArgs+1) {
            Json json = new Json();
            @SuppressWarnings("unchecked")
            HashMap<String,Object> ctx = json.deserialize(args[minArgs+1], HashMap.class);
            es.withContext(ctx);
        } 
        if (args.length > minArgs+2) {
            es.withSubject(args[minArgs+2]);
        } 
        
        String stok = es.signEncode(args[0]);
        print("");
        print("bearer");
        print("-----");
        print(stok);    
        print("");
        print("token");
        print("-----");
        Json json = new Json(false, false, true);
        print(new String(json.serialize(es.mToken.mTokenData), StandardCharsets.UTF_8));
        
    }
   
}
