package io.eluv.format.base58;


import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.provider.FFIProvider;


public class NativeB58Encoder {
    
    /**
     * The name of the library can be passed via a system property 'native.b58.library'
     * The value of the property can be:
     * - the absolute path to the library file
     * - otherwise the simple name of the library. In that case the paths 
     *   defined by the system property 'java.library.path' will be searched.
     *   
     * The 'simple' name of the library can be (assuming a library named 'xx':
     * - the actual file name: like libxx.so under linux or libxx.dylib under Mac
     * - or the 'library' name: 'xx' (without the 'lib' prefix and without file extension)
     * 
     * System.getProperty("java.library.path") returns the following
     * on MAC:
     *   /Users/xx/eluv.io/ws/src/elv-tokens/java
     *   /Users/xx/Library/Java/Extensions
     *   /Library/Java/Extensions
     *   /Network/Library/Java/Extensions
     *   /System/Library/Java/Extensions
     *   /usr/lib/java
     *   .
     * on linux (Ubuntu 18.04.3 LTS):
     *   /usr/java/packages/lib/amd64
     *   /usr/lib64
     *   /lib64
     *   /lib:/usr/lib  
     */
    public static final String NATIVE_B58_LIBRARY_NAME_PROP = "native.b58.library";
    public static final String NATIVE_B58_LIBRARY_NAME_DEF = "elvb58";
    public static final String NATIVE_B58_LIBRARY_NAME;
    
    @IgnoreError
    public static interface nativeB58 {
        Pointer Base58(byte[] inputBytes, int length);
        void FreeCString(Pointer p);
    }

    //
    // using JNR
    //
    static nativeB58 JNR_GO_LIB;
    static Throwable JNR_GO_LIB_LOAD_ERROR;
    static {
        NATIVE_B58_LIBRARY_NAME = System.getProperty(
                NATIVE_B58_LIBRARY_NAME_PROP, 
                NATIVE_B58_LIBRARY_NAME_DEF);
        loadNative(NATIVE_B58_LIBRARY_NAME);
    }
    
    static void loadNative(String name) {
        try {
            JNR_GO_LIB_LOAD_ERROR = null;        
            LibraryLoader<nativeB58> loader = FFIProvider.getSystemProvider().createLibraryLoader(nativeB58.class);
            loader.failImmediately();
            JNR_GO_LIB = loader.load(name);
        } catch (Throwable t) {
            JNR_GO_LIB_LOAD_ERROR = t;        
        }
    }
    
    public static boolean hasLib() {
        return JNR_GO_LIB != null;
    }
    
    public static Throwable loadError() {
        return JNR_GO_LIB_LOAD_ERROR;
    }
    
    private static void checkNolib() throws IllegalStateException {
        if (JNR_GO_LIB_LOAD_ERROR != null) {
            throw new IllegalStateException("", JNR_GO_LIB_LOAD_ERROR);
        }
    }
    
    
    public static String encode(byte[] input) {
        checkNolib();
        if (input == null) {
            throw new RuntimeException("no input bytes");
        }
        Pointer p = JNR_GO_LIB.Base58(input, input.length);
        String s = p.getString(0);
        JNR_GO_LIB.FreeCString(p);
        return s;
    }

}
