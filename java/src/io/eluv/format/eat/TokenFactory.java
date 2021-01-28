package io.eluv.format.eat;


import java.util.HashMap;

import org.web3j.crypto.ECKeyPair;

import io.eluv.constants.Constants;
import io.eluv.crypto.KeyFactory;
import io.eluv.crypto.Signer;
import io.eluv.format.id.Id;


/**
 *  TokenFactory provides builders for tokens.
 *  
 */
public class TokenFactory {
    static final long HOUR = 3600 * 1000;
    
    
    public static class EditorSigned {
        
        Token mToken;

        
        /**
         * Construct a new EditorSigned builder using uncompressed JSON format.
         * 
         * @param sid the space ID
         * @param lib the library ID
         * @param qid the content ID
         */
        public EditorSigned(String sid, String lid, String qid) throws TokenException {
            this(sid, lid, qid, false);
        }
        
        /**
         * Construct a new EditorSigned builder using JSON format.
         * 
         * @param sid the space ID
         * @param lib the library ID
         * @param qid the content ID
         * @param compressed true to use compressed format
         */
        public EditorSigned(String sid, String lid, String qid, boolean compressed) throws TokenException {
            TokenFormat format = compressed 
                ? TokenFormat.JSON_COMPRESSED 
                : TokenFormat.JSON;
            mToken = new Token(TokenType.EDITOR_SIGNED, format);
            mToken.mTokenData.Grant = "read";
            long now = System.currentTimeMillis();
            // UTC
            mToken.mTokenData.IssuedAt = now;
            // allow 4 hours validity - note: fabric cap is 24 hours
            mToken.mTokenData.Expires = now + (HOUR * 4);
            
            try {
                // validate IDs
                new Id(sid).assertCode(Id.Code.QSpace);
                new Id(lid).assertCode(Id.Code.QLib);
                new Id(qid).assertCode(Id.Code.Q);
            } catch (Exception e) {
                throw new TokenException("", e);
            }

            mToken.mTokenData.SID = sid;
            mToken.mTokenData.LID = lid;
            mToken.mTokenData.QID = qid;
        }
        
        /**
         * Adds AFGH public key to the token
         * 
         * @param afghPk the AFGH public key
         * @return this EditorSigned
         */
        public EditorSigned withAFGHPublicKey(String afghPk) {
            mToken.mTokenData.AFGHPublicKey = afghPk;
            return this;
        }
        
        /**
         * Set the expiration delay of the token
         * 
         * @param expiresIn the expiration in millis
         * @return this EditorSigned
         */
        public EditorSigned withExpiresIn(long expiresIn) {
            mToken.mTokenData.Expires = mToken.mTokenData.IssuedAt + expiresIn;
            return this;
        }
        
        /**
         * Set the ID of the content ID of the delegate object storing a policy
         * 
         * @param policyId the ID of the delegate
         * @return this EditorSigned
         */
        public EditorSigned withDelegationId(String policyId) {
            // validate id
            new Id(policyId).assertCode(Id.Code.Q);
            
            mToken.mTokenData.Ctx.put(Constants.ElvDelegationId, policyId);
            return this;
        }
        
        /**
         * Add context information that might be used in policy evaluation
         * <p>
         * All values from the map are added to the existing context
         * 
         * @param ctx the context
         * @return this EditorSigned
         */
        public EditorSigned withContext(HashMap<String, Object> ctx) {
            mToken.mTokenData.Ctx.putAll(ctx);
            return this;
        }

        /**
         * Set the subject of this token.  
         * <p>
         * When signing - if no subject was set - the subject is taken from the
         * address of the signer.
         * 
         * @param subject the subject
         * @return this EditorSigned
         */
        public EditorSigned withSubject(String subject) {
            mToken.mTokenData.Subject = subject;
            return this;
        }

        /**
         * Signs and encodes the token.
         * 
         * @param hexEncodedPk an hex encoded SECP-256k1 key to sign the token
         * @return a 'bearer' string authorization
         * @throws TokenException
         */
        public String signEncode(String hexEncodedPk) throws TokenException {
            try {
                return this.signEncode(KeyFactory.createSigner(hexEncodedPk));
            } catch (TokenException e) {
                throw e;
            } catch (Exception e) {
                throw new TokenException("", e);
            }
        }
        
        /**
         * Signs and encodes the token.
         * 
         * @param sk the SECP-256k1 key pair to sign the token
         * @return a 'bearer' string authorization
         * @throws TokenException
         */
        public String signEncode(ECKeyPair sk) throws TokenException {
            return this.signEncode(new Signer.KeyPairSigner(sk));
        }
        
        /**
         * Signs and encodes the token.
         * 
         * @param sk the SECP-256k1 key pair to sign the token
         * @return a 'bearer' string authorization
         * @throws TokenException
         */
        public String signEncode(Signer sk) throws TokenException {
            try {
                if (mToken.mTokenData.Subject == null || 
                    mToken.mTokenData.Subject.length() == 0) {
                    mToken.mTokenData.Subject = new Id(Id.Code.User,sk.getAddress()).toString();
                }
                mToken.sign(sk);
                return mToken.encode();
            } catch (TokenException e) {
                throw e;
            } catch (Exception e) {
                throw new TokenException("", e);
            }
        }
        
        String encode() throws TokenException {
            try {
                return mToken.encode();
            } catch (TokenException e) {
                throw e;
            } catch (Exception e) {
                throw new TokenException("", e);
            }
        }
        
    }

}
