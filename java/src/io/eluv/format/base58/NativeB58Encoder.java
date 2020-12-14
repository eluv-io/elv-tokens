package io.eluv.format.base58;


import jnr.ffi.Pointer;
import jnr.ffi.annotations.IgnoreError;


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
     *   /lib
     *   /usr/lib  
     */
    public static final String NATIVE_B58_LIBRARY_NAME_PROP   = "native.b58.library";
    public static final String NATIVE_B58_LIBRARY_SIMPLE_NAME = "elvb58";
    public static final String NATIVE_B58_LIBRARY_NAME;

    /** 
     * Pass true to disable use of native library
     */
    public static final String NATIVE_B58_DISABLED_PROP = "native.b58.disabled";
    public static final boolean NATIVE_B58_DISABLED;
    
    /** 
     * Path to a temporary folder. 
     * If not set the result of System.getProperty("java.io.tmpdir") is used.  
     */
    public static final String NATIVE_B58_TMP_DIR_PROP = "io.eluv.format.base58.tmpdir";
    public static final String NATIVE_B58_TMP_DIR;
    
    public static final String NATIVE_B58_PREFIX = "elvb58-";
    
    /* for tests */
    static boolean NATIVE_B58_DISABLE_SELF_EXTRACT = false;
    
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
        String p = System.getProperty(NATIVE_B58_DISABLED_PROP, "false");
        NATIVE_B58_DISABLED = Boolean.valueOf(p);
        
        NATIVE_B58_LIBRARY_NAME = System.getProperty(
            NATIVE_B58_LIBRARY_NAME_PROP, 
            NATIVE_B58_LIBRARY_SIMPLE_NAME);
        NATIVE_B58_TMP_DIR = System.getProperty(
            NATIVE_B58_TMP_DIR_PROP, 
            System.getProperty("java.io.tmpdir"));
        
        loadNative(NATIVE_B58_LIBRARY_NAME);
    }
    
    static synchronized void loadNative(String libName) {
        JNR_GO_LIB_LOAD_ERROR = null;
        if (NATIVE_B58_DISABLED) {
            return; 
        }
        NativeB58Loader<nativeB58> loader = new NativeB58Loader<>(
            NATIVE_B58_LIBRARY_SIMPLE_NAME,
            libName,
            NATIVE_B58_TMP_DIR, 
            NATIVE_B58_PREFIX,
            NATIVE_B58_DISABLE_SELF_EXTRACT);
        try {
            JNR_GO_LIB = loader.load(nativeB58.class);
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
