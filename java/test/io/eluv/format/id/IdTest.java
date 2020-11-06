package io.eluv.format.id;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IdTest {

    @Test
    void testId() throws Exception {
        
        class testCase {
            String val;
            boolean failExpected;
            
            testCase(String s, boolean f){
                val = s;
                failExpected = f;
            }
            
            void test(){
                try {
                    Id id = new Id(val);
                    if (failExpected) {
                        fail("expected to fail [" + val + "]");
                    }
                    
                    // re-parse
                    String s = id.toString();
                    Id id2 = new Id(s);
                    assertEquals(id, id2);
                    
                    // with bytes
                    Id id3 = new Id(id.code(), id.bytes());
                    assertEquals(id, id3);
                    
                    
                } catch (Exception e) {
                    if (!failExpected) {
                        fail("expected to NOT fail [" + val + "] ", e);
                    }
                }
            }
        }
        
        testCase[] testCases = new testCase[] {
                new testCase("", true),
                new testCase("alabama", true),
                new testCase("iq__", true),
                new testCase("iq__35BUYfYD44N2vZniVHrqaadrh8", true),
                new testCase("iq__35BUYfYD44N2vZniVHrqaadrh8mC", false),
        };
        
        for (testCase tc : testCases) {
            tc.test();
        }
        
    }

}
