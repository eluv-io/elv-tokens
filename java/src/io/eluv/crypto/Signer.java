package io.eluv.crypto;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;

/**
 * Signer is the interface of objects providing a signature for a given digest.
 */
public interface Signer  {
    
    /**
     * Sign the given digest
     *  
     * @param digestHash a byte array
     * @return a byte array containing the signature data with R,S,V components
     * @throws SignException
     */
    byte[] sign(byte[] digestHash) throws SignException;
    
    /**
     * @return the address of this signer as a byte array
     */
    public byte[] getAddress();
    
    
    /** A signer that uses an ECKeyPair */
    public static class KeyPairSigner implements Signer {
        private final ECKeyPair mKeyPair;
        private byte[] mAddress;
        
        public KeyPairSigner(ECKeyPair ecKeyPair) {
            mKeyPair = ecKeyPair;
            mAddress = Crypto.pubkeyToAddress(mKeyPair);
        }
        
        @Override
        public byte[] sign(byte[] digestHash) throws SignException {
            try {
                SignatureData sd = Sign.signMessage(digestHash, mKeyPair, false);
                return Crypto.signatureBytes(sd);
            } catch (SignException e) {
                throw e;
            } catch (Throwable t) {
                throw new SignException("signing error", t);
            }
        }
        
        @Override
        public byte[] getAddress() {
            return mAddress;
        }
        
    }
}

