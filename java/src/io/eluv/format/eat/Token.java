package io.eluv.format.eat;

import java.io.IOException;
import java.util.Objects;


import io.eluv.crypto.Crypto;
import io.eluv.crypto.SignException;
import io.eluv.crypto.Signer;
import io.eluv.flate.Flate;
import io.eluv.format.base58.Base58Encoder;
import io.eluv.json.Json;

/**
 * Token is an auth token, defined by it's type, format, and token data.
 * 
 */
public class Token {
    private static final int prefixLen = 6; // length of entire prefix including type, sig-type and format

    private final TokenType    mType;
    private final TokenFormat  mFormat;
    private       TokenSigType mSigType;
    TokenData                  mTokenData;
    byte[]                     mTokenBytes;
    byte[]                     mSignature;

    public Token(TokenType type, TokenFormat format) {
        mType = type;
        mFormat = format;
        mSigType = TokenSigType.UNSIGNED;
        mTokenData = new TokenData();
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        //result = prime * result + Arrays.hashCode(mSignature);
        //result = prime * result + Arrays.hashCode(mTokenBytes);
        result = prime * result + Objects.hash(mFormat, mSigType, mTokenData, mType);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Token)) {
            return false;
        }
        Token other = (Token) obj;
        return mFormat == other.mFormat 
                && mSigType == other.mSigType 
                //&& Arrays.equals(mSignature, other.mSignature)
                //&& Arrays.equals(mTokenBytes, other.mTokenBytes) 
                && Objects.equals(mTokenData, other.mTokenData)
                && mType == other.mType;
    }


    void validate() throws TokenException {
        if (mType == TokenType.UNKNOWN) {
            throw new TokenException("Invalid token type (unknown)");
        }
        
        if (mFormat == TokenFormat.UNKNOWN) {
            throw new TokenException("Invalid token format (unknown)");
        }
        
        switch (mSigType) {
        case UNKNOWN:
            throw new TokenException("Invalid signature format (unknown)");
        case UNSIGNED:
            break;
        case ES256K:
            if (mSignature == null || mSignature.length == 0 
                || mTokenBytes == null || mTokenBytes.length == 0) {
                throw new TokenException("Missing signature or data bytes");
            }
        }
    }
    
    String encodePrefix() {
        String prefix = mType.getPrefix() + mSigType.getPrefix() + mFormat.getPrefix();
        if (prefix.length() != prefixLen) {
            throw new IllegalStateException("expected prefix len: " + prefixLen + ", but was: " + prefix.length());
        }
        return prefix;
    }
    
    byte[] encodeBytesNoCompression() throws TokenException {
        byte[] data;
        
        switch (mFormat) {
        case JSON: 
        case JSON_COMPRESSED:
            Json json = new Json();
            try {
                data = json.serialize(mTokenData);
            } catch (Exception e) {
                throw new TokenException("error serializing token data", e);
            }
            break;
        case LEGACY:
        case CBOR:
        case CBOR_COMPRESSED:    
            //data, err = mTokenData.EncodeCBOR()
        case CUSTOM:
        default:
            throw new TokenException("format not supported: " + mFormat.name());
        }
        
        return data;
    }
    
    
    byte[] encodeBytes() throws TokenException {
        
        byte[] data = encodeBytesNoCompression();
        
        switch (mFormat) {
        case JSON_COMPRESSED:
        case CBOR_COMPRESSED:
            
            try {
                data = Flate.compressData(data);
            } catch (IOException e) {
                throw new TokenException("error compressing data", e);
            } 
            
            break;
        default:
            break;
        }

        return data;
    }
    
    byte[] encodeTokenAndSigBytes() throws TokenException {
        byte[] data;
        if (mSigType == TokenSigType.UNSIGNED) {
            data = encodeBytes();
        } else {
            data = new byte[mSignature.length+mTokenBytes.length];
            System.arraycopy(mSignature,0, data,0, mSignature.length);
            System.arraycopy(mTokenBytes,0, data,mSignature.length, mTokenBytes.length);
        }
        return data;        
    }
    
    public String encode() throws TokenException {
        validate();
        
        byte[] data = encodeTokenAndSigBytes();
        return encodePrefix() + Base58Encoder.encode(data);
    }
    
    //sign signs this token using the provided signer.
    void sign(Signer signer) throws TokenException {

        mTokenData.EthAddr = signer.getAddress();
        mTokenBytes = encodeBytes();
        
        try {
            mSignature = Crypto.sign(mTokenBytes, signer);
            mSigType = TokenSigType.ES256K;
        } catch (SignException ex) {
            throw new TokenException("", ex);
        }

    }
    

}
