package com.rill.rest.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static com.rill.rest.util.MultiMapUtil.addValueToMap;

public abstract class BaseApiRequestSignerTest {

    protected void runTestGetHashAlgorithmFromParamters(final BaseApiRequestSigner signer,
                                                              final String hashMethodParam){

        final HashAlgorithm[] hashAlgorithms 
            = new HashAlgorithm[]{HashAlgorithm.HMAC_SHA256_ALGORITHM,
                                  HashAlgorithm.HMAC_SHA1_ALGORITHM,
                                  HashAlgorithm.HMAC_MD5_ALGORITHM};
        final String[][] algorithmSpellings = new String[][]{
            {"HmacSHA256", "hmac-sha-256", "HMAC_sha-256", "hmac-SHA_256", "HMAC_SHA_256"},
            {"HmacSHA1", "hmac-sha-1", "HMAC_sha-1", "hmac-SHA_1", "HMAC_SHA_1"},
            {"HmacMD5", "hmac-md-5", "HMAC_md5", "hmac-MD_5", "HMAC_MD_5"}};

        assertEquals(hashAlgorithms.length, algorithmSpellings.length);

        Map<String, List<String>> paramMap = getTestParamMap();
        HashAlgorithm hashAlgorithmFromParams = null;

        //verify null hash is returned if parameter is not found
        paramMap.remove(hashMethodParam);
        hashAlgorithmFromParams = signer.getHashAlgorithm(paramMap);
        assertNull(hashAlgorithmFromParams);
            
        //verify null hash is returned when parameter is there but value cannot be recognized
        addValueToMap(paramMap, hashMethodParam, "some bogus value");
        hashAlgorithmFromParams = signer.getHashAlgorithm(paramMap);
        assertNull(hashAlgorithmFromParams);
        paramMap.remove(hashMethodParam);
        
        //verify happy cases
        int i=0;
        for(HashAlgorithm hashAlgorithm : hashAlgorithms){
            
            paramMap = getTestParamMap();
            hashAlgorithmFromParams = null;

            for(String hashAlgorithmStr : algorithmSpellings[i++]){
                paramMap.remove(hashMethodParam);
                addValueToMap(paramMap, hashMethodParam, hashAlgorithmStr);
                hashAlgorithmFromParams = signer.getHashAlgorithm(paramMap);
                assertNotNull("expected "+hashAlgorithmStr+" to be recognized as valid hash algorithm",
                              hashAlgorithmFromParams);
                assertEquals("expected "+hashAlgorithmStr+" to be recognized as "+hashAlgorithm,
                             hashAlgorithm, hashAlgorithmFromParams);
            }
        }
        assertEquals(hashAlgorithms.length, i);
    }

    protected void runFormatAndSignTest(final BaseApiRequestSigner signer,
                                        final HashAlgorithm hashAlgorithm,
                                        final String signatureParamName,
                                        final String key,
                                        final String expectedSignature){
        Map<String, List<String>> paramMap = getTestParamMap();
        addValueToMap(paramMap, signatureParamName, hashAlgorithm.getName());
        String signature = doFormatAndSign(signer, paramMap, key);
        System.out.println(signature);
        assertEquals(expectedSignature, signature);
    }

    protected abstract String doFormatAndSign(final BaseApiRequestSigner signer, final Map<String, List<String>> paramMap, final String key);

    protected void runTestNoAlgInFormatAndSign(final BaseApiRequestSigner signer, final String signatureParamName){
        Map<String, List<String>> paramMap = getTestParamMap(); //doesn't have SIGNATURE_PARAM_NAME
        String signature = doFormatAndSign(signer, paramMap, getTestKey()); 
        assertNull(signature);
 
        addValueToMap(paramMap, signatureParamName, "some bogus value");
        signature = doFormatAndSign(signer, paramMap, getTestKey()); 
        assertNull(signature);
    }

    protected void runTestEmptyKeyFormatAndSign(final BaseApiRequestSigner signer, final String signatureParamName){
        Map<String, List<String>> paramMap = getTestParamMap(); 
        addValueToMap(paramMap, signatureParamName, "HmacSHA256"); //valid hash algorithm, to get to the error
        String signature = doFormatAndSign(signer, paramMap, ""); 
        assertNull(signature);
    }

    protected Map<String, List<String>> getTestParamMap(){
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        addValueToMap(map, "somevalue", "a%bc");
        addValueToMap(map, "someothervalue", "1 2 3");
        addValueToMap(map, "somevalue", "x-y+z");
        return map;
    }
    protected String getTestKey(){
        return "abc";
    }
}
        
