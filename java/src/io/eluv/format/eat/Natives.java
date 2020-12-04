package io.eluv.format.eat;

import org.bitcoin.Secp256k1Context;

import fr.acinq.OSInfo;
import io.eluv.crypto.KeyFactory;
import io.eluv.format.base58.NativeB58Encoder;

public class Natives {

    static void print(String msg) {
        System.out.println(msg);
    }
    
    static void printReport() {
        // general info
        print("java.library.path=" + System.getProperty("java.library.path"));
        print("");
        
        // b58 encoding
        print("== Native b58 library ==");
        print("native.b58.library=" + NativeB58Encoder.NATIVE_B58_LIBRARY_NAME);
        print("default library name: " + System.mapLibraryName(NativeB58Encoder.NATIVE_B58_LIBRARY_NAME));
        if (NativeB58Encoder.hasLib()) {
            print("native library found.");
        } else {
            print("native library not found.");
            print("error: " + NativeB58Encoder.loadError());
        }
        print("");
        
        // secp256k1
        print("== secp256k1 ==");
        print(KeyFactory.NATIVE_SECP256K1_DISABLED_PROP+"="+KeyFactory.NATIVE_SECP256K1_DISABLED);
        print("secp256k1 available: " + Secp256k1Context.isEnabled());
        
        print("fr.acinq.secp256k1.tmpdir=" + System.getProperty("fr.acinq.secp256k1.tmpdir"));
        print("fr.acinq.secp256k1.lib.path=" + System.getProperty("fr.acinq.secp256k1.lib.path"));
        print("fr.acinq.secp256k1.lib.name=" + System.getProperty("fr.acinq.secp256k1.lib.name"));
        print("os-info: " + OSInfo.getNativeLibFolderPathForCurrentOS());
        print("default library name: " + System.mapLibraryName("secp256k1"));
    }
    
    /**
     * Reports some trouble shooting information about natives libraries used.. 
     */
    public static void main(String[] args) throws Exception {
        printReport();        
    }

}
