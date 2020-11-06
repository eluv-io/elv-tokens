package io.eluv.format.eat;


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
        print("TokenSign private_key spaceId libraryId contentId [delegationId]");
        print("  create an editor-signed token valid for 4 hours");
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
            true)
            //.withAFGHPublicKey("my_afgh")
            .withExpiresIn(TokenFactory.HOUR * 4);
        
        if (args.length > minArgs) {
            es.withDelegationId(args[4]);
        }
        
        String stok = es.signEncode(args[0]);
        print(stok);      
    }
   
}
