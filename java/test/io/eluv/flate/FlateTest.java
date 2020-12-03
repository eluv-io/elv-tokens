package io.eluv.flate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FlateTest {

    @Test
    void testCompress() throws Exception {
        byte[] res = Flate.compressData("hello".getBytes());
        //System.out.println("compressed " + Hex.toHexString(res));
        
        byte[] dec = Flate.decompressData(res);
        assertEquals("hello", new String(dec));
    }

}
