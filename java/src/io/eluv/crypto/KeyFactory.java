package io.eluv.crypto;

public class KeyFactory {
    
    public static String NATIVE_SECP256K1_DISABLED_PROP = "native.secp256k1.disabled";
    public static boolean NATIVE_SECP256K1_DISABLED;
    
    static {
        String p = System.getProperty(NATIVE_SECP256K1_DISABLED_PROP, "false");
        NATIVE_SECP256K1_DISABLED = Boolean.valueOf(p);
    }
    
    
    public static Signer createSigner(String hexEncodedPk) throws KeysException {
        if (NATIVE_SECP256K1_DISABLED) {
            return new PrivateKey(Crypto.KeyPairFrom(hexEncodedPk));
        }
        return new Secp256k1(hexEncodedPk);
    }

}
