package io.eluv.crypto;

import java.math.BigInteger;

import org.bouncycastle.util.Arrays;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.utils.Numeric;




/**
 * Crypto Utilities
 * 
 */
public class Crypto {
    
    public static final int PRIVATE_KEY_SIZE = 32;
    public static final int PUBLIC_KEY_SIZE = 64;
    public static final int SIGNATURE_LENGTH = 65;
    

    // ----- keys -----
    
    public static ECKeyPair KeyPairFrom(String hexEncoded) throws KeysException {
        try {
            if (hexEncoded.startsWith("0x") || hexEncoded.startsWith("0X")) {
                hexEncoded = hexEncoded.substring(2);
            }
            BigInteger privKey = new BigInteger(hexEncoded, 16);
            BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
            return new ECKeyPair(privKey, pubKey);
        } catch (Exception ex) {
            throw new KeysException("", ex);
        }
    }

    public static byte[] pubkeyToAddress(BigInteger pubKey) {
        byte[] publicKey = Numeric.toBytesPadded(pubKey, PUBLIC_KEY_SIZE);
        return Keys.getAddress(publicKey);
    }
    
    public static byte[] pubkeyToAddress(ECKeyPair ecKeyPair) {
        return pubkeyToAddress(ecKeyPair.getPublicKey());
    }
    
    public static String compressPubKey(BigInteger pubKey) {
        String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
        String pubKeyHex = pubKey.toString(16);
        String pubKeyX = pubKeyHex.substring(0, 64);
        return pubKeyYPrefix + pubKeyX;
    }
    
    public static byte[] toBytesPadded(byte[] value, int length) throws KeysException {
        byte[] result = new byte[length];
        byte[] bytes = value;

        int bytesLength;
        int srcOffset;
        if (bytes[0] == 0) {
            bytesLength = bytes.length - 1;
            srcOffset = 1;
        } else {
            bytesLength = bytes.length;
            srcOffset = 0;
        }

        if (bytesLength > length) {
            throw new KeysException("Input is too large ("+bytesLength+") to put in byte array of size " + length);
        }

        int destOffset = length - bytesLength;
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
        return result;
    }
    
    // ----- signing -----
    
    public static byte[] sign(byte[] msg, ECKeyPair ecKeyPair) throws SignException {
        return sign(msg, new Signer.KeyPairSigner(ecKeyPair));
    }
    
    public static byte[] signatureBytes(SignatureData sig) throws SignException {
        if (sig == null) {
            throw new SignException("null signature");
        }
        int sigLen = sig.getR().length+sig.getS().length+sig.getV().length;
        if (sigLen!= SIGNATURE_LENGTH) {
            throw new SignException("signature must be "+SIGNATURE_LENGTH+" bytes long, but was "+sigLen);
        }
        
        int pos = 0;
        byte[] r = new byte[SIGNATURE_LENGTH];
        System.arraycopy(sig.getR(), 0, r, 0,   sig.getR().length); pos += sig.getR().length;
        System.arraycopy(sig.getS(), 0, r, pos, sig.getS().length); pos += sig.getS().length;
        System.arraycopy(sig.getV(), 0, r, pos, sig.getV().length);
        return r;
    }
    
    public static SignatureData signatureData(byte[] sig) throws SignException {
        if (sig == null) {
            throw new SignException("null signature");
        }
        int sigLen = sig.length;
        if (sigLen!= SIGNATURE_LENGTH) {
            throw new SignException("signature must be "+SIGNATURE_LENGTH+" bytes long, but was "+sigLen);
        }
        return new SignatureData(
            Arrays.copyOfRange(sig, 64, 65), // V
            Arrays.copyOfRange(sig, 0, 32),  // R
            Arrays.copyOfRange(sig, 32, 64)  // S
        );
    }
    
    
    public static byte[] sign(byte[] msg, Signer signer) throws SignException {
        byte[] hsh = Hash.sha3(msg);
        return signer.sign(hsh);
    }
    
    public static byte[] adjustSignedBytes(byte[] signature) throws SignException {
        if (signature == null) {
            throw new SignException("Null signature");
        }
        if (signature.length < SIGNATURE_LENGTH) {
            throw new SignException(
                "invalid signature length. " +
                "Must be "+SIGNATURE_LENGTH+" bytes long, but was "+signature.length);
        }
        if (signature[64] < 4) {
            return signature;
        } else {
            byte [] adjSigBytes = new byte[SIGNATURE_LENGTH];
            System.arraycopy(signature, 0, adjSigBytes, 0, signature.length);
            adjSigBytes[64] -= 27;
            return adjSigBytes;
        }
    }
    

}
