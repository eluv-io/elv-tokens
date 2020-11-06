package io.eluv.crypto;

import java.math.BigInteger;

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
    
    public static byte[] pubkeyToAddress(ECKeyPair ecKeyPair) {
        byte[] publicKey = Numeric.toBytesPadded(ecKeyPair.getPublicKey(), PUBLIC_KEY_SIZE);
        return Keys.getAddress(publicKey);
    }
    
    public static String compressPubKey(BigInteger pubKey) {
        String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
        String pubKeyHex = pubKey.toString(16);
        String pubKeyX = pubKeyHex.substring(0, 64);
        return pubKeyYPrefix + pubKeyX;
    }
    
    
    // ----- signing -----
    
    public static interface Signer  {
        SignatureData sign(byte[] digestHash) throws SignException;
    }
    
    public static byte[] sign(byte[] msg, ECKeyPair ecKeyPair) throws SignException {
        Crypto.Signer signFunc = new Crypto.Signer(){
            public SignatureData sign(byte[] digestHash) throws SignException {
                return Sign.signMessage(digestHash, ecKeyPair, false);
            }
        };
        return sign(msg, signFunc);
    }
    
    public static byte[] sign(byte[] msg, Signer signer) throws SignException {
        byte[] hsh = Hash.sha3(msg);
        SignatureData sig = signer.sign(hsh);
        int sigLen = sig.getR().length+sig.getS().length+sig.getV().length;
        if (sigLen!= 65) {
            throw new SignException("signature must be 65 bytes long, but was "+sigLen);
        }
        
        int pos = 0;
        byte[] r = new byte[65];
        System.arraycopy(sig.getR(), 0, r, 0,   sig.getR().length); pos += sig.getR().length;
        System.arraycopy(sig.getS(), 0, r, pos, sig.getS().length); pos += sig.getS().length;
        System.arraycopy(sig.getV(), 0, r, pos, sig.getV().length);
        return r;
    }
    
    public static byte[] adjustSignedBytes(byte[] signature) {
        if (signature.length < 65) {
            return new byte[0];
        }
        if (signature[64] < 4) {
            return signature;
        } else {
            byte [] adjSigBytes = new byte[65];
            System.arraycopy(signature, 0, adjSigBytes, 0, signature.length);
            adjSigBytes[64] -= 27;
            return adjSigBytes;
        }
    }
    

}
