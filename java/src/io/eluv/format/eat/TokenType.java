package io.eluv.format.eat;

public enum TokenType {

    UNKNOWN("aun", "unknown"),
    ANONYMOUS("aan", "anonymous"),
    TX("atx", "tx"),
    STATE_CHANNEL("asc", "state-channel"),
    CLIENT("acl", "client"),
    PLAIN("apl", "plain"),
    EDITOR_SIGNED("aes", "editor-signed");

    String mPrefix;
    String mName;

    TokenType(String prefix, String name) {
        mPrefix = prefix;
        mName = name;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public String getName() {
        return mName;
    }

}
