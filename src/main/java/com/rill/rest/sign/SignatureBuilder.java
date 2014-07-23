package com.rill.rest.sign;

public interface SignatureBuilder {

    SignatureBuilder withEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm);
    SignatureBuilder withParameterValue(String parameter, final String value);
    SignatureBuilder withEncryptionKey(String encryptionKey);
    String sign();

}
