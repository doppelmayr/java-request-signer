package com.rill.rest.sign;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.rill.rest.util.MultiMapUtil.getSingleValueOrNull;
import static com.rill.rest.util.MultiMapUtil.addValueToMap;

/**
 * Applies bells and whistles as described by https://dev.twitter.com/docs/auth/creating-signature
 * This is also compatible with mashape's OAUth bible and manally tested against their test page.
 * TODO - write tests for this.
 */
public class OAuthApiRequestSigner extends BaseApiRequestSigner {

    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";

    public static class Builder implements SignatureBuilder {

	private SortedMap<String, List<String>> paramMap = new TreeMap<String, List<String>>();
	private HashAlgorithm hashAlgorithm = null;
	private String hashKey = null;
	private String method = null;
	private String url = null;

	public Builder withHashAlgorithm(final HashAlgorithm hashAlgorithm){
	    this.hashAlgorithm = hashAlgorithm;
	    return this;
	}
	public Builder withMethod(final String method){
	    //RESOLVE validate
	    this.method = method;
	    return this;
	}
	public Builder withUrl(final String url){
	    //RESOLVE validate
	    this.url = url;
	    return this;
	}
	public Builder withParameterValue(final String parameter, final String value){
	    addValueToMap(this.paramMap, parameter, value);
	    return this;
	}
	public Builder withHashKey(final String hashKey){
	    this.hashKey = hashKey;
	    return this;
	}
	public String sign(){
	    if(this.hashAlgorithm==null){
		throw new IllegalStateException("Hash method required, please specify with withHashAlgorithm()");
	    }
	    if(this.hashKey==null){
		throw new IllegalStateException("Hash key (secret) required, please specify with withHashKey()");
	    }
	    if(this.url==null){
		throw new IllegalStateException("Url required, please specify with withUrl()");
	    }
	    if(this.method==null){
		throw new IllegalStateException("Method required, please specify with withMethod()");
	    }
	    //overrides the hash method if there was one in the map
	    this.paramMap.put(OAUTH_SIGNATURE_METHOD, Arrays.asList(hashAlgorithm.getName()));
	    return new OAuthApiRequestSigner().formatAndSign(this.method, this.url, paramMap, this.hashKey);
	}
    }
        
    private static final Logger log = LoggerFactory.getLogger(OAuthApiRequestSigner.class);

    private URLCodec urlCodec = new URLCodec();

    public HashAlgorithm getHashAlgorithm(Map<String, List<String>> parameterMap){

        String hashAlgorithmString = getSingleValueOrNull(parameterMap, OAUTH_SIGNATURE_METHOD);
        return hashAlgorithmString!=null ? HashAlgorithm.fromString(hashAlgorithmString) : null;
    }

    protected String processParamNamePreHash(final String param){
        return urlEncode(param);
    }

    protected String processParamValuePreHash(final String value){
        return urlEncode(value);
    }

    protected String processSortedParameterStringPreHash(final String sortedParamString){
        return urlEncode(sortedParamString);
    }
    
    protected String getBaseStr(final String method, final String url){

        String encodedUrl = urlEncode(url);
        
        //deal with empty values gracefully
        return 
            method + (StringUtils.isEmpty(method) ? "" : "&")
            + encodedUrl + (StringUtils.isEmpty(encodedUrl) ? "" : "&");

    }

    private String urlEncode(final String value){
        String encodedValue = value;
        try{
            encodedValue = urlCodec.encode(encodedValue, "UTF-8");
            encodedValue = encodedValue.replace("+", "%20"); //wtf, rest of the world, java is right and you are wrong
        }catch (java.io.UnsupportedEncodingException uex) {
            log.error("error url encoding {}", value, uex);
            throw new RuntimeException(uex.getMessage(), uex);
        }
        return encodedValue;
    }

}
