package io.eluv.crypto;

import java.util.Arrays;

import org.bitcoin.NativeSecp256k1;
import org.bitcoin.NativeSecp256k1Util;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Keys;


/**
 * Secp256k1 is a private key that uses the native implementation from:
 * <p>
 * https://github.com/ACINQ/secp256k1/tree/jni-embed/src/java
 *
 */
public class Secp256k1 implements Signer {
    
    private final byte[] mPrivKey;
    private final byte[] mPubKey;
    private final byte[] mAddress;

    public Secp256k1(String pk) throws KeysException {
        mPrivKey = secp256k1(pk);
        mPubKey = initPubKey();
        mAddress = Keys.getAddress(addressBytes());
    }
    
    public Secp256k1(byte[] pk) throws KeysException {
        mPrivKey = secp256k1(pk);
        mPubKey = initPubKey();
        mAddress = Keys.getAddress(addressBytes());
    }
    
    private byte[] initPubKey() throws KeysException {
        try {
            return NativeSecp256k1.computePubkey(mPrivKey);
        } catch (Throwable t) {
            throw new KeysException("", t);
        }
    }
    
    private byte[] addressBytes() throws KeysException {
        // Crypto.pubkeyToAddress
        byte[] pubKeyBytes = Arrays.copyOfRange(mPubKey, 1, mPubKey.length); // remove prefix
        return Crypto.toBytesPadded(pubKeyBytes, Crypto.PUBLIC_KEY_SIZE);
    }
    
    @Override
    public byte[] getAddress() {
        return mAddress;
    }
    
    @Override
    public byte[] sign(byte[] digestHash) throws SignException {
        byte[] sig;
        try {
            // R: 32 bytes | S: 32 bytes
            sig = NativeSecp256k1.signCompact(digestHash, mPrivKey);
        } catch (NativeSecp256k1Util.AssertFailException t) {
            throw new SignException("", t);
        }
        if (sig == null) {
            throw new SignException("signing failed: no signature returned");
        }
        if (sig.length != Crypto.SIGNATURE_LENGTH-1) {
            throw new SignException("signing failed: invalid signature length (" + sig.length+")");
        }
        
        // compute V
        int recId = -1;
        byte[] k;
        for (int i = 0; i < 4; i++) {
            try {
                k = NativeSecp256k1.ecdsaRecover(sig, digestHash, i);
            } catch (NativeSecp256k1Util.AssertFailException t) {
                throw new SignException("", t);
            }
            if (k != null && Arrays.equals(mPubKey, k)) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new SignException("invalid signature: invalid private key ?");
        }
        int headerByte = recId + 27;
        byte[] bsig = Arrays.copyOf(sig, sig.length+1);
        bsig[sig.length] = (byte) headerByte;
        return bsig;
    }

    
    // --- builders helpers ---
    
    static byte[] secp256k1(String pk) throws KeysException {
        try {
            return secp256k1(Hex.decode(pk));
        } catch (Exception e) {
            throw new KeysException("", e);
        }
    }
    
    static byte[] secp256k1(byte[] pk) throws KeysException {
        if (pk == null) {
            throw new KeysException("null private key");
        }
        if (pk.length != Crypto.PRIVATE_KEY_SIZE) {
            throw new KeysException("invalid private key length - "
                    + "expected: "+Crypto.PRIVATE_KEY_SIZE+", actual: " + pk.length);
        }
        return pk;
    }
    

}
