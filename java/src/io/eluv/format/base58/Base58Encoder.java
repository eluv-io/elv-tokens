package io.eluv.format.base58;

public class Base58Encoder {
    
    public static String encode(byte[] input) {
        if (NativeB58Encoder.hasLib()) {
            return NativeB58Encoder.encode(input);
        }
        return Base58.encode(input);
    }

}
