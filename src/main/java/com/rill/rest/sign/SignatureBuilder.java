package com.rill.rest.sign;

public interface SignatureBuilder {

    SignatureBuilder withHashAlgorithm(HashAlgorithm hashAlgorithm);
    SignatureBuilder withParameterValue(String parameter, final String value);
    SignatureBuilder withHashKey(String hashKey);
    String sign();

}
