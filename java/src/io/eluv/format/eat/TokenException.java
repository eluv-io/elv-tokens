package io.eluv.format.eat;

@SuppressWarnings("serial")
public class TokenException extends Exception {

    
    public TokenException(String msg) {
        this(msg, null);
    }
    
    public TokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
