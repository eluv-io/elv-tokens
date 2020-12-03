package io.eluv.crypto;

import java.math.BigInteger;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.utils.Numeric;


public class PrivateKey implements Signer {
    
    static final ECDomainParameters CURVE =
            new ECDomainParameters(
                    Sign.CURVE_PARAMS.getCurve(),
                    Sign.CURVE_PARAMS.getG(),
                    Sign.CURVE_PARAMS.getN(),
                    Sign.CURVE_PARAMS.getH());
    
    
    private final ECKeyPair keyPair;
    private final byte[] address;
    private final ECDSASigner signer;
    
    public PrivateKey(ECKeyPair k) {
        keyPair = k;
        address = Crypto.pubkeyToAddress(keyPair);
        
        signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(keyPair.getPrivateKey(), CURVE);
        signer.init(true, privKey);
    }
    
    public ECKeyPair getKeyPair() {
        return keyPair;
    }
    
    public byte[] getAddress() {
        return address;
    }
    
    private ECDSASignature signWithKeyPair(byte[] digestHash) throws SignException {
        BigInteger[] components = signer.generateSignature(digestHash);

        return new ECDSASignature(components[0], components[1]).toCanonicalised();
    }
    
    @Override
    public byte[] sign(byte[] digestHash) throws SignException {
        //
        //return Sign.signMessage(digestHash, pk, false);
        //
        
        ECDSASignature sig = signWithKeyPair(digestHash);
        
        BigInteger publicKey = keyPair.getPublicKey();
        // Now we have to work backwards to figure out the recId needed to recover the signature.
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            BigInteger k = Sign.recoverFromSignature(i, sig, digestHash);
            if (k != null && k.equals(publicKey)) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new SignException(
                "Could not construct a recoverable key. Are your credentials valid?");
        }

        int headerByte = recId + 27;

        // 1 header + 32 bytes for R + 32 bytes for S
        byte[] v = new byte[] {(byte) headerByte};
        byte[] r = Numeric.toBytesPadded(sig.r, 32);
        byte[] s = Numeric.toBytesPadded(sig.s, 32);

        SignatureData sd = new SignatureData(v, r, s);
        return Crypto.signatureBytes(sd);
    }
}
