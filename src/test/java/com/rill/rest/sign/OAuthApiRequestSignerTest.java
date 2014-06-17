package com.rill.rest.sign;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static com.rill.rest.util.MultiMapUtil.addValueToMap;

public class OAuthApiRequestSignerTest extends BaseApiRequestSignerTest {

    @Test
    public void testGetEncryptionAlgorithmFromParamters(){        
        runTestGetEncryptionAlgorithmFromParamters(new OAuthApiRequestSigner(), OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD);
    }

    protected String doFormatAndSign(final BaseApiRequestSigner signer, final Map<String, List<String>> paramMap, final String key){
        return ((OAuthApiRequestSigner)signer).formatAndSign(getMethod(), getUrl(), paramMap, key); 
    }

    @Test
    public void testSha1Sign(){
        //generated at http://www.freeformatter.com/hmac-generator.html 
        //for METHOD&https%3A%2F%2Fmyurl.com& + urlencoded string "oauth_signature_method=HmacSHA1&someothervalue=1%202%203&somevalue=a%25bc&somevalue=x-y%2Bz"
        //that is, METHOD&https%3A%2F%2Fmyurl.com&oauth_signature_method%3DHmacSHA1%26someothervalue%3D1%25202%25203%26somevalue%3Da%2525bc%26somevalue%3Dx-y%252Bz
        //and key "abc"
        //hex 621c4fa3d656611ff23b68da24d4ac1b5f330336
        final String controlString = Base64.encodeBase64String(new byte[]{(byte)0x62, (byte)0x1c, (byte)0x4f, (byte)0xa3, (byte)0xd6, 
                                                                          (byte)0x56, (byte)0x61, (byte)0x1f, (byte)0xf2, (byte)0x3b, 
                                                                          (byte)0x68, (byte)0xda, (byte)0x24, (byte)0xd4, (byte)0xac, 
                                                                          (byte)0x1b, (byte)0x5f, (byte)0x33, (byte)0x03, (byte)0x36});
        runFormatAndSignTest(new OAuthApiRequestSigner(), BaseApiRequestSigner.EncryptionAlgorithm.HMAC_SHA1_ALGORITHM,
                             OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD, getTestKey(), controlString);
    }

    @Test
    public void testMD5Sign(){
        //generated at http://www.freeformatter.com/hmac-generator.html 
        //for METHOD&https%3A%2F%2Fmyurl.com& + urlencoded string "oauth_signature_method=HmacMD5&someothervalue=1%202%203&somevalue=a%25bc&somevalue=x-y%2Bz"
        //that is, METHOD&https%3A%2F%2Fmyurl.com&oauth_signature_method%3DHmacMD5%26someothervalue%3D1%25202%25203%26somevalue%3Da%2525bc%26somevalue%3Dx-y%252Bz
        //and key "abc"
        //hex 378b6500a171793fb9e24c7091b9ec90
        final String controlString = Base64.encodeBase64String(new byte[]{(byte)0x37, (byte)0x8b, (byte)0x65, (byte)0x00, (byte)0xa1, 
                                                                          (byte)0x71, (byte)0x79, (byte)0x3f, (byte)0xb9, (byte)0xe2, 
                                                                          (byte)0x4c, (byte)0x70, (byte)0x91, (byte)0xb9, (byte)0xec, 
                                                                          (byte)0x90});
        runFormatAndSignTest(new OAuthApiRequestSigner(), BaseApiRequestSigner.EncryptionAlgorithm.HMAC_MD5_ALGORITHM,
                             OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD, getTestKey(), controlString);
    }

    @Test
    public void testSha256Sign(){
        //generated at http://www.freeformatter.com/hmac-generator.html 
        //for METHOD&https%3A%2F%2Fmyurl.com& + urlencoded string "oauth_signature_method=HmacSHA256&someothervalue=1%202%203&somevalue=a%25bc&somevalue=x-y%2Bz"
        //that is, METHOD&https%3A%2F%2Fmyurl.com&oauth_signature_method%3DHmacSHA256%26someothervalue%3D1%25202%25203%26somevalue%3Da%2525bc%26somevalue%3Dx-y%252Bz
        //and key "abc"
        //hex 70e059a9c50e01290c937720b276d1488b78b0bcfd5c321561fe002e4f65e98e
        final String controlString = Base64.encodeBase64String(new byte[]{(byte)0x70, (byte)0xe0, (byte)0x59, (byte)0xa9, (byte)0xc5, 
                                                                          (byte)0x0e, (byte)0x01, (byte)0x29, (byte)0x0c, (byte)0x93, 
                                                                          (byte)0x77, (byte)0x20, (byte)0xb2, (byte)0x76, (byte)0xd1, 
                                                                          (byte)0x48, (byte)0x8b, (byte)0x78, (byte)0xb0, (byte)0xbc, 
                                                                          (byte)0xfd, (byte)0x5c, (byte)0x32, (byte)0x15, (byte)0x61, 
                                                                          (byte)0xfe, (byte)0x00, (byte)0x2e, (byte)0x4f, (byte)0x65, 
                                                                          (byte)0xe9, (byte)0x8e});
        runFormatAndSignTest(new OAuthApiRequestSigner(), BaseApiRequestSigner.EncryptionAlgorithm.HMAC_SHA256_ALGORITHM,
                             OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD, getTestKey(), controlString);

    }

    @Test
    public void testNoAlgInFormatAndSign(){
        runTestNoAlgInFormatAndSign(new OAuthApiRequestSigner(), OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD);
    }

    @Test
    public void testEmptyKeyFormatAndSign(){
        runTestEmptyKeyFormatAndSign(new OAuthApiRequestSigner(), OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD);
    }
 
    @Test
    public void testBuilder(){
	final Map<String, List<String>> paramMap = getTestParamMap();
	final BaseApiRequestSigner.EncryptionAlgorithm encryption = BaseApiRequestSigner.EncryptionAlgorithm.HMAC_SHA1_ALGORITHM;
	final String encryptionKey = getTestKey();

	OAuthApiRequestSigner.Builder builder = new OAuthApiRequestSigner.Builder()
	    .withEncryptionAlgorithm(encryption).withEncryptionKey(encryptionKey)
	    .withMethod(getMethod())
	    .withUrl(getUrl());
	for(String param : paramMap.keySet()){
	    for(String value : paramMap.get(param)){
		builder.withParameterValue(param, value);
	    }
	}
	final String signatureFromBuilder = builder.sign();

	addValueToMap(paramMap, OAuthApiRequestSigner.OAUTH_SIGNATURE_METHOD, encryption.getName());
	final String signatureFromMap = doFormatAndSign(new OAuthApiRequestSigner(), paramMap, encryptionKey); 

	assertEquals("expect same signature from map and bulder", signatureFromMap, signatureFromBuilder);

    }

    private String getMethod(){
        return "METHOD";
    }

    private String getUrl(){
        return "https://myurl.com";
    }

}
