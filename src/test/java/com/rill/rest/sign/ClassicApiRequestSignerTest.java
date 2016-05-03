package com.rill.rest.sign;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static com.rill.rest.util.MultiMapUtil.addValueToMap;

public class ClassicApiRequestSignerTest extends BaseApiRequestSignerTest {
    
    @Test
    public void testGetHashAlgorithmFromParamters(){
        runTestGetHashAlgorithmFromParamters(new ClassicApiRequestSigner(), ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME);
    }

    protected String doFormatAndSign(final BaseApiRequestSigner signer, final Map<String, List<String>> paramMap, final String key){
        return ((ClassicApiRequestSigner)signer).formatAndSign(paramMap, key); 
    }

    @Test
    public void testSha1Sign(){
        //generated at http://www.freeformatter.com/hmac-generator.html 
        //for string "signatureMethod=HmacSHA1&someothervalue=1 2 3&somevalue=a%bc&somevalue=x-y+z"
        //and key "abc"
        //hex 8664c9906e71d1ef527248a08088d787a0d8bb03
        final String controlString = Base64.encodeBase64String(new byte[]{(byte)0x86, (byte)0x64, (byte)0xc9, (byte)0x90, 
                                                                          (byte)0x6e, (byte)0x71, (byte)0xd1, 
                                                                          (byte)0xef, (byte)0x52, (byte)0x72, (byte)0x48, 
                                                                          (byte)0xa0, (byte)0x80, (byte)0x88,
                                                                          (byte)0xd7, (byte)0x87, (byte)0xa0, (byte)0xd8, 
                                                                          (byte)0xbb, (byte)0x03});
        runFormatAndSignTest(new ClassicApiRequestSigner(), HashAlgorithm.HMAC_SHA1_ALGORITHM,
                             ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME, getTestKey(), controlString);
    }

    @Test
    public void testMD5Sign(){
        //generated at http://www.freeformatter.com/hmac-generator.html 
        //for string "signatureMethod=HmacMD5&someothervalue=1 2 3&somevalue=a%bc&somevalue=x-y+z"
        //and key "abc"
        //hex a7b1589772d11975983c7b2c843054bc
        final String controlString = Base64.encodeBase64String(new byte[]{(byte)0xa7, (byte)0xb1, (byte)0x58, (byte)0x97, 
                                                                          (byte)0x72, (byte)0xd1, (byte)0x19, (byte)0x75, 
                                                                          (byte)0x98, (byte)0x3c, (byte)0x7b, (byte)0x2c, 
                                                                          (byte)0x84, (byte)0x30, (byte)0x54, (byte)0xbc});
        runFormatAndSignTest(new ClassicApiRequestSigner(), HashAlgorithm.HMAC_MD5_ALGORITHM,
                             ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME, getTestKey(), controlString);
    }

    @Test
    public void testSha256Sign(){
        //generated at http://www.freeformatter.com/hmac-generator.html 
        //for string "signatureMethod=HmacSHA256&someothervalue=1 2 3&somevalue=a%bc&somevalue=x-y+z"
        //and key "abc"
        //hex 5dd9e898fb640835e150b8ba3647d7dce4f7a54963c227be128aef6d40c1a457
        final String controlString = Base64.encodeBase64String(new byte[]{(byte)0x5d, (byte)0xd9, (byte)0xe8, (byte)0x98, (byte)0xfb, 
                                                                          (byte)0x64, (byte)0x08, (byte)0x35, (byte)0xe1, (byte)0x50, 
                                                                          (byte)0xb8, (byte)0xba, (byte)0x36, (byte)0x47, (byte)0xd7, 
                                                                          (byte)0xdc, (byte)0xe4, (byte)0xf7, (byte)0xa5, (byte)0x49, 
                                                                          (byte)0x63, (byte)0xc2, (byte)0x27, (byte)0xbe, (byte)0x12, 
                                                                          (byte)0x8a, (byte)0xef, (byte)0x6d, (byte)0x40, (byte)0xc1, 
                                                                          (byte)0xa4, (byte)0x57});
        runFormatAndSignTest(new ClassicApiRequestSigner(), HashAlgorithm.HMAC_SHA256_ALGORITHM,
                             ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME, getTestKey(), controlString);
    }

    @Test
    public void testNoAlgInFormatAndSign(){
        runTestNoAlgInFormatAndSign(new ClassicApiRequestSigner(), ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME);
    }

    @Test
    public void testEmptyKeyFormatAndSign(){
        runTestEmptyKeyFormatAndSign(new ClassicApiRequestSigner(), ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME);
    }

    @Test
    public void testBuilder(){
	final Map<String, List<String>> paramMap = getTestParamMap();
	final HashAlgorithm hash = HashAlgorithm.HMAC_SHA1_ALGORITHM;
	final String hashKey = getTestKey();

	ClassicApiRequestSigner.Builder builder = new ClassicApiRequestSigner.Builder()
	    .withHashAlgorithm(hash).withHashKey(hashKey);
	for(String param : paramMap.keySet()){
	    for(String value : paramMap.get(param)){
		builder.withParameterValue(param, value);
	    }
	}
	final String signatureFromBuilder = builder.sign();

	addValueToMap(paramMap, ClassicApiRequestSigner.SIGNATURE_METHOD_PARAM_NAME, hash.getName());
	final String signatureFromMap = doFormatAndSign(new ClassicApiRequestSigner(), paramMap, hashKey); 

	assertEquals("expect same signature from map and bulder", signatureFromMap, signatureFromBuilder);
	
    }

}
