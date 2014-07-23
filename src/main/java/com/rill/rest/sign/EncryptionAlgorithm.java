package com.rill.rest.sign;

public enum EncryptionAlgorithm {
    HMAC_SHA256_ALGORITHM("HmacSHA256"),
    HMAC_SHA1_ALGORITHM("HmacSHA1"),
    HMAC_MD5_ALGORITHM("HmacMD5");
    
    private String name;
    private EncryptionAlgorithm(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public static EncryptionAlgorithm fromString(String name) {
        String normalizedName = name.replace("-","").replace("_","").toUpperCase();
        for (EncryptionAlgorithm alg : values()) {
            if (alg.getName().toUpperCase().equals(normalizedName)) {
                return alg;
            }
        }
        return null;
    }
}
