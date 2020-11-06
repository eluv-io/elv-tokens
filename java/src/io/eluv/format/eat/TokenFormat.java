package io.eluv.format.eat;

public enum TokenFormat {
    UNKNOWN("nk", "unknown"),                 // 0
    LEGACY("__", "legacy"),                   // 1
    JSON("j_", "json"),                       // 2
    JSON_COMPRESSED("jc", "json-compressed"), // 3
    CBOR("c_", "cbor"),                       // 4
    CBOR_COMPRESSED("cc", "cbor-compressed"), // 5
    CUSTOM("b_", "custom");                   // 6

    String mPrefix;
    String mName;

    TokenFormat(String prefix, String name) {
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
