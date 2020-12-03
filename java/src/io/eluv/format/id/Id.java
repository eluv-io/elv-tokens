package io.eluv.format.id;


import org.bouncycastle.util.Arrays;

import io.eluv.format.base58.Base58;

/**
 * Id represents an ID in the content-fabric.
 * <p>
 * An Id is an ethereum address prefixed with a code indicating the kind of
 * object it points to. 
 *
 */
public class Id {
    
    public static final int CodeLen   = 1; // 1 byte
    public static final int PrefixLen = 4; // 4 chars

    // HashLength is the expected length of the hash
    //public static final int HashLength = 32;
    
    // AddressLength is the expected length of an address
    public static final int AddressLength = 20;
    
    
    public static enum Code {
        
        UNKNOWN        ("iukn",   "unknown"),
        Account        ("iacc",   "account"),
        User           ("iusr",   "user"),
        QLib           ("ilib",   "content library"),
        Q              ("iq__",   "content"),
        QStateStore    ("iqss",   "content state store"),
        QSpace         ("ispc",   "content space"),
        QFileUpload    ("iqfu",   "content file upload"),
        QFilesJob      ("iqfj",   "content files job"),
        QNode          ("inod",   "fabric node"),
        Network        ("inet",   "network"),
        KMS            ("ikms",   "KMS"),
        CachedResultSet("icrs",   "cached result set"),
        Tenant         ("iten",   "tenant"),
        Group          ("igrp",   "group");
        
        
        String mPrefix;
        String mName;
        
        Code(String prefix, String name) {
            mPrefix = prefix;
            mName = name;
        }
        
        byte getCode() {
            return (byte) ordinal();
        }
        
        public String toString() {
            return mName + " (" + mPrefix + ")";
        }
        
        static Code FromString(String s) {
            for (Code c : Code.values()) {
                if (c.mPrefix.equals(s)) {
                    return c;
                }
            }
            return UNKNOWN;
        }

    }

    
    private final byte[] mBytes;
    

    public Id(Code prefix, byte[] address) throws IllegalArgumentException {
        this(new Builder(prefix, address));
    }
    
    public Id(String s) throws IllegalArgumentException {
        this(new Builder(s));
    }
    
    private Id(Builder builder) throws IllegalArgumentException {
        builder.validate();
        byte[] b = new byte[AddressLength+CodeLen];
        b[0] = builder.mPrefix.getCode();
        System.arraycopy(builder.mAddress, 0, b, 1, builder.mAddress.length);
        mBytes = b;
    }
    
    public Code code() {
        return Code.values()[mBytes[0]];
    }
    
    public byte[] bytes() {
        return Arrays.copyOfRange(mBytes, 1, mBytes.length);
    }
    
    public boolean hasCode(Code c) {
        return c == code();
    }
    
    /**
     * Assert the code of this Id
     * 
     * @param c the code to assert
     * @return this Id for chained calls
     * @throws IllegalArgumentException
     */
    public Id assertCode(Code c) throws IllegalArgumentException {
        if (c != code()) {
            throw new IllegalArgumentException("expecting " + code() + ", but received " + c);
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + java.util.Arrays.hashCode(mBytes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Id)) {
            return false;
        }
        Id other = (Id) obj;
        return java.util.Arrays.equals(mBytes, other.mBytes);
    }
    
    @Override
    public String toString() {
        return Code.values()[mBytes[0]].mPrefix 
                + Base58.encode(Arrays.copyOfRange(mBytes, 1, mBytes.length));
    }
    
    
    private static final class Builder {
        Code mPrefix; 
        byte[] mAddress;
        
        Builder(Code prefix, byte[] address) throws IllegalArgumentException {
            mPrefix = prefix;
            mAddress = address;
        }
        
        Builder(String s) throws IllegalArgumentException {
            if (s == null || s.length() < PrefixLen) {
                throw new IllegalArgumentException("invalid prefix ID in [" + s + "]");
            }
            Code pref = Code.FromString(s.substring(0, PrefixLen));
            if (pref == Code.UNKNOWN) {
                throw new IllegalArgumentException("unknown prefix (" + s.substring(0, PrefixLen) + ")");
            }
            byte[] address;
            try {
                address = Base58.decode(s.substring(PrefixLen));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            mPrefix = pref;
            mAddress = address;
        }
        
        void validate() throws IllegalArgumentException {
            if (mPrefix == null) {
                throw new IllegalArgumentException("prefix is null");
            }
            if (mAddress == null || mAddress.length != AddressLength) {
                throw new IllegalArgumentException("invalid address length");
            }
        }
        
    }

}
