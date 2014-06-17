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

    public static enum EncryptionAlgorithm {
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

    private static final Logger log = LoggerFactory.getLogger(BaseApiRequestSigner.class);

    protected abstract String getBaseStr(final String method, final String url);
    public abstract EncryptionAlgorithm getEncryptionAlgorithm(Map<String, List<String>> parameterMap);
    protected abstract String processParamNamePreEncryption(final String param);
    protected abstract String processParamValuePreEncryption(final String param);
    protected abstract String processSortedParameterStringPreEncryption(final String sortedParamString);

    public String formatAndSign(String method, String url, Map<String, List<String>> params, String key){

        SortedMap sortedMap = getSortedMap(params);
        String sortedParamStr = getSortedParameterString(sortedMap);
        String base = getBaseStr(method, url);
        EncryptionAlgorithm encryptionAlgorithm = getEncryptionAlgorithm(params);
        if(encryptionAlgorithm == null){ //should not be; we validate upfront
            log.error("this shouldn't happen: could not figure out encryption alg: {}", params);
            return null;
        }
        return signString(encryptionAlgorithm, base+sortedParamStr, key);
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
                sb.append(processParamNamePreEncryption(paramName))
                    .append("=")
                    .append(processParamValuePreEncryption(paramValue)).append("&");
            }
        }
        
        sb.deleteCharAt(sb.length()-1);  // remove last '&' char
        return processSortedParameterStringPreEncryption(sb.toString());
    }
    
        
    private String signString(EncryptionAlgorithm encryptionAlgorithm, String str, String key) {
        log.debug("signing [{}] with [{}]", str, encryptionAlgorithm);
        String result = null;
        try {
            // Get an hmac_sha256 key from the raw key bytes.
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF8"), encryptionAlgorithm.getName());

            // Get an hmac_sha256 Mac instance and initialize with the signing key.
            Mac mac = Mac.getInstance(encryptionAlgorithm.getName());
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
