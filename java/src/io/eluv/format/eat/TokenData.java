package io.eluv.format.eat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenData {

    @JsonProperty("txh")
    public byte[] EthTxHash;            // ethereum transaction hash - stored as []byte to enable 'nil'
    @JsonProperty("adr")
    public byte[] EthAddr;              // ethereum address of the user - stored as []byte to enable 'nil'
    @JsonProperty("apk")
    public String AFGHPublicKey;        // AFGH public key
    @JsonProperty("qph")
    public String QPHash;               // types.QPHash - qpart hash for node 2 node

    // Common
    @JsonProperty("spc")
    public String SID;                  // types.QSpaceID - space ID
    @JsonProperty("lib")
    public String   LID;                // types.QLibID - lib ID

    // ElvAuthToken ==> elvmaster
    @JsonProperty("qid")
    public String QID;                 // types.QID - content ID
    @JsonProperty("sub")
    public String    Subject;          // the entity the token was granted to
    @JsonProperty("gra")
    public String    Grant;            // type of grant
    @JsonProperty("iat")
    public long      IssuedAt;         // Issued At (millis - UTC)
    @JsonProperty("exp")
    public long      Expires;          // Expiration Time (millis - UTC)
    @JsonProperty("ctx")
    public HashMap<String,Object> Ctx; // additional, arbitrary information conveyed in the token
    
    
    TokenData() {
        Ctx = new HashMap<String,Object>();
    }


    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + Arrays.hashCode(EthAddr);
        result = prime * result + Arrays.hashCode(EthTxHash);
        result = prime * result
                + Objects.hash(AFGHPublicKey, Ctx, Expires, Grant, IssuedAt, LID, QID, QPHash, SID, Subject);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TokenData)) {
            return false;
        }
        TokenData other = (TokenData) obj;
        return Objects.equals(AFGHPublicKey, other.AFGHPublicKey) 
                && sameContext(Ctx, other.Ctx)
                && Arrays.equals(EthAddr, other.EthAddr) 
                && Arrays.equals(EthTxHash, other.EthTxHash)
                && Expires == other.Expires 
                && Objects.equals(Grant, other.Grant) 
                && IssuedAt == other.IssuedAt
                && Objects.equals(LID, other.LID) 
                && Objects.equals(QID, other.QID)
                && Objects.equals(QPHash, other.QPHash) 
                && Objects.equals(SID, other.SID)
                && Objects.equals(Subject, other.Subject);
    }
    
    private boolean sameContext(HashMap<String,Object> c1, HashMap<String,Object> c2) {
        if (c1 == null && c2 == null) {
            return true;
        }
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1.size() != c2.size()) {
            return false;
        }
        for (String k : c1.keySet()) {
            if (!Objects.deepEquals(c1.get(k), c2.get(k))) {
                return false;
            }
        }
        return true;
    }
    
}
