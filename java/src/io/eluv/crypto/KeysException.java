package io.eluv.crypto;


/**
 * KeysException is thrown when dealing with keys
 */
@SuppressWarnings("serial")
public class KeysException extends Exception {

    public KeysException(String message) {
        this(message, null);
    }

    public KeysException(String message, Throwable cause) {
        super(message, cause);
    }

}
