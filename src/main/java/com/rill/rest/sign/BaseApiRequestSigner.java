package com.rill.rest.sign;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: rsmith
 * Date: 11/7/13 11:29 AM
 */
public abstract class BaseApiRequestSigner {

    private static final Logger log = LoggerFactory.getLogger(BaseApiRequestSigner.class);

    protected abstract String getBaseStr(final String method, final String url);
    public abstract HashAlgorithm getHashAlgorithm(Map<String, List<String>> parameterMap);
    protected abstract String processParamNamePreHash(final String param);
    protected abstract String processParamValuePreHash(final String param);
    protected abstract String processSortedParameterStringPreHash(final String sortedParamString);

    public String formatAndSign(String method, String url, Map<String, List<String>> params, String key){

        SortedMap sortedMap = getSortedMap(params);
        String sortedParamStr = getSortedParameterString(sortedMap);
        String base = getBaseStr(method, url);
        HashAlgorithm hashAlgorithm = getHashAlgorithm(params);
        if(hashAlgorithm == null){ //should not be; we validate upfront
            log.error("this shouldn't happen: could not figure out hash alg: {}", params);
            return null;
        }
        return signString(hashAlgorithm, base+sortedParamStr, key);
    }

    protected SortedMap getSortedMap(Map<String, List<String>> params){

        TreeMap<String, List<String>> sortedMap = new TreeMap<String, List<String>>();
        for(Entry<String, List<String>> param : params.entrySet()) {
            List values = param.getValue();
            Collections.sort(values);
            sortedMap.put(param.getKey(), values);
        }
        return sortedMap;
    }

    protected String getSortedParameterString(SortedMap<String, List<String>> params) {
        StringBuilder sb = new StringBuilder();
        for (String paramName : params.keySet()) {
            for (String paramValue : params.get(paramName)){
                sb.append(processParamNamePreHash(paramName))
                    .append("=")
                    .append(processParamValuePreHash(paramValue)).append("&");
            }
        }
        
        sb.deleteCharAt(sb.length()-1);  // remove last '&' char
        return processSortedParameterStringPreHash(sb.toString());
    }
    
        
    private String signString(HashAlgorithm hashAlgorithm, String str, String key) {
        log.debug("signing [{}] with [{}]", str, hashAlgorithm);
        String result = null;
        try {
            // Get an hmac_sha256 key from the raw key bytes.
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF8"), hashAlgorithm.getName());

            // Get an hmac_sha256 Mac instance and initialize with the signing key.
            Mac mac = Mac.getInstance(hashAlgorithm.getName());
            mac.init(signingKey);
            
            // Compute the hmac on input data bytes.
            byte[] rawHmac = mac.doFinal(str.getBytes("UTF8"));

            // Base64-encode the hmac by using the utility in the SDK
//            result = BinaryUtils.toBase64(rawHmac);
            result = Base64.encodeBase64String(rawHmac);
        }
        catch (Exception e) {
            log.error("error generating request", e);
        }

        return result;
    }
}
