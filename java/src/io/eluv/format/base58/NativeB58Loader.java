package io.eluv.format.base58;

import java.io.*;
import java.util.UUID;

import io.eluv.os.OSInfo;
import jnr.ffi.LibraryLoader;
import jnr.ffi.provider.FFIProvider;

/**
 * The library files are automatically extracted from this project's package (JAR). 
 */
public class NativeB58Loader<T> {
    
    private final String mTempDir;
    private final String mSearchPattern;
    /** The simple library name */
    private final String mNativeLibraryName;
    /** The library path (can be the same as library name) */
    private final String mNativeLibraryPath;
    private final boolean mDisableSelfExtract;
    
    NativeB58Loader(
        String nativeLibraryName,    
        String nativeLibraryPath,    
        String tmpDir, 
        String searchPattern,
        boolean disableSelfExtract) {
        
        mNativeLibraryName = nativeLibraryName;
        mNativeLibraryPath = nativeLibraryPath;
        mTempDir = tmpDir;
        mSearchPattern = searchPattern;
        mDisableSelfExtract = disableSelfExtract;
    }

    /**
     * Loads the native library.
     *
     * @return True if the native library is successfully loaded, false otherwise.
     * @throws Exception if loading fails
     */
    T load(Class<T> pClass) throws Throwable {
        cleanup();

        LibraryLoader<T> loader = FFIProvider.getSystemProvider().createLibraryLoader(pClass);
        loader.failImmediately();
        
        Throwable loadError;
        try {
            return loader.load(mNativeLibraryPath);
        } catch (Throwable t) {
            loadError = t;
            if (mDisableSelfExtract) {
                throw t;
            }
        }
        
        String sysLibraryName = System.mapLibraryName(mNativeLibraryName);
        
        // Load the os-dependent library from the jar file
        String packagePath = pClass.getPackage().getName().replaceAll("\\.", "/");
        String resourceLibraryPath = String.format(
            "/%s/native/%s", 
            packagePath,
            sysLibraryName);
        boolean hasNativeLib = hasResource(resourceLibraryPath);

        if (!hasNativeLib) {
            throw new IOException(
                String.format(
                    "No native library found for os.name=%s and os.arch=%s. path=%s",
                    OSInfo.getOSName(), 
                    OSInfo.getArchName(), 
                    resourceLibraryPath), 
                loadError);
        }
        
        File lib;        
        try {
            // Try extracting the library from jar
            lib = extractLibraryFile(
                sysLibraryName,    
                resourceLibraryPath,
                getTempDir().getAbsolutePath());
        } catch (Exception e) {
            e.initCause(loadError);
            throw e;
        }
        
        loader = FFIProvider.getSystemProvider().createLibraryLoader(pClass);
        loader.failImmediately();
        return loader.load(lib.getAbsolutePath());
    }
    
    private File getTempDir() {
        return new File(mTempDir);
    }

    /**
     * Delete old native libraries that were not removed on VM-Exit
     */
    void cleanup() {
        String tempFolder = getTempDir().getAbsolutePath();
        File   dir        = new File(tempFolder);

        File[] nativeLibFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(mSearchPattern) && !name.endsWith(".lck");
            }
        });
        if (nativeLibFiles != null) {
            for (File nativeLibFile : nativeLibFiles) {
                File lckFile = new File(nativeLibFile.getAbsolutePath() + ".lck");
                if (!lckFile.exists()) {
                    try {
                        nativeLibFile.delete();
                    } catch (SecurityException e) {
                        System.err.println("Failed to delete old native lib" + e.getMessage());
                    }
                }
            }
        }
    }

    private boolean contentsEquals(InputStream in1, InputStream in2) throws IOException {
        if (!(in1 instanceof BufferedInputStream)) {
            in1 = new BufferedInputStream(in1);
        }
        if (!(in2 instanceof BufferedInputStream)) {
            in2 = new BufferedInputStream(in2);
        }

        int ch = in1.read();
        while (ch != -1) {
            int ch2 = in2.read();
            if (ch != ch2) {
                return false;
            }
            ch = in1.read();
        }
        int ch2 = in2.read();
        return ch2 == -1;
    }

    /**
     * Extracts the specified library file to the target folder
     *
     * @param resourceFilePath Library resource path.
     * @param targetFolder     Target folder.
     * @return
     */
    private File extractLibraryFile(
        String systemLibraryName,    
        String resourceFilePath, 
        String targetFolder) throws Exception {
        
        // Include architecture name in temporary filename in order to avoid conflicts
        // when multiple JVMs with different architectures running at the same time
        String uuid                 = UUID.randomUUID().toString();
        String extractedLibFileName = String.format(mSearchPattern + "%s-%s", uuid, systemLibraryName);
        String extractedLckFileName = extractedLibFileName + ".lck";

        File extractedLibFile = new File(targetFolder, extractedLibFileName);
        File extractedLckFile = new File(targetFolder, extractedLckFileName);

        // Extract a native library file into the target directory
        InputStream reader = NativeB58Loader.class.getResourceAsStream(resourceFilePath);
        if (!extractedLckFile.exists()) {
            FileOutputStream los = new FileOutputStream(extractedLckFile);
            try {los.close(); } catch (Exception io) {}
        }
        FileOutputStream writer = new FileOutputStream(extractedLibFile);
        try {
            byte[] buffer    = new byte[8192];
            int    bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
        } finally {
            // Delete the extracted lib file on JVM exit.
            extractedLibFile.deleteOnExit();
            extractedLckFile.deleteOnExit();

            if (writer != null) {
                try { writer.close(); } catch (Exception io) {}
            }
            if (reader != null) {
                try { reader.close(); } catch (Exception io) {}
            }
        }

        // Set executable (x) flag to enable Java to load the native library
        extractedLibFile.setReadable(true);
        extractedLibFile.setWritable(true, true);
        extractedLibFile.setExecutable(true);

        // Check whether the contents are properly copied from the resource folder
        {
            InputStream nativeIn       = NativeB58Loader.class.getResourceAsStream(resourceFilePath);
            InputStream extractedLibIn = new FileInputStream(extractedLibFile);
            try {
                if (!contentsEquals(nativeIn, extractedLibIn)) {
                    throw new IOException(String.format(
                        "Failed to write a native library file at %s", 
                        extractedLibFile));
                }
            } finally {
                if (nativeIn != null) {
                    nativeIn.close();
                }
                if (extractedLibIn != null) {
                    extractedLibIn.close();
                }
            }
        }
        return extractedLibFile;
    }

    private boolean hasResource(String path) {
        return NativeB58Loader.class.getResource(path) != null;
    }

}
