package io.eluv.flate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Flate is a utility to compress / decompress data in a way compatible with the
 * golang flate package. 
 * 
 * The both use 'nowrap' as true to not use the ZLIB header and checksum fields
 * in order to support the compression format used in both GZIP and PKZIP - 
 * as in the 'flate' Golang package.
 */
public class Flate {

    /**
     * Compresses data with zlib compression.
     */
    public static byte[] compressData(byte[] raw) throws IOException {
        InputStream in = new ByteArrayInputStream(raw);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
        def.setStrategy(Deflater.HUFFMAN_ONLY);
        OutputStream out = new DeflaterOutputStream(baos, def);
        copyAll(in, out);
        in.close();
        out.close();
        return baos.toByteArray();
    }

    /**
     * Decompresses zlib compressed data.
     */
    public static byte[] decompressData(byte[] compressed) throws IOException {
        InputStream in = new InflaterInputStream(
                new ByteArrayInputStream(compressed),
                new Inflater(true));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAll(in, out);
        in.close();
        out.close();
        return out.toByteArray();
    }

    static final int BUFFER_SIZE = 4096;
    static long copyAll(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }
    

}
