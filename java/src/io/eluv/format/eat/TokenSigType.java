package io.eluv.format.eat;



public enum TokenSigType {

    UNKNOWN("_", "unknown"), 
    UNSIGNED("u", "unsigned"), 
    ES256K("s", "ES256K");

    String mPrefix;
    String mName;

    TokenSigType(String prefix, String name) {
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
