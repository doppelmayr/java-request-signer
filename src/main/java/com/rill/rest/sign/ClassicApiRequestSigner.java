package com.rill.rest.sign;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.rill.rest.util.MultiMapUtil.addValueToMap;
import static com.rill.rest.util.MultiMapUtil.getSingleValueOrNull;

public class ClassicApiRequestSigner extends BaseApiRequestSigner {

    public static final String SIGNATURE_METHOD_PARAM_NAME = "signatureMethod";
    
    public static class Builder implements SignatureBuilder {

        private SortedMap<String, List<String>> paramMap = new TreeMap<String, List<String>>();
        private EncryptionAlgorithm encryptionAlgorithm = null;
        private String encryptionKey;
        
        public Builder withEncryptionAlgorithm(final EncryptionAlgorithm encryptionAlgorithm){
            this.encryptionAlgorithm = encryptionAlgorithm;
            return this;
        }
        public Builder withParameterValue(final String parameter, final String value){
            addValueToMap(this.paramMap, parameter, value);
            return this;
        }
        public Builder withEncryptionKey(final String encryptionKey){
            this.encryptionKey = encryptionKey;
            return this;
        }
        public String sign(){
            if(this.encryptionAlgorithm==null){
                throw new IllegalStateException("Encryption method required, please specify with withEncryptionAlgorithm()");
            }
            if(this.encryptionKey==null){
                throw new IllegalStateException("Encryption key (secret) required, please specify with withEncryptionKey()");
            }
            //overrides the encryption method if there was one in the map
            this.paramMap.put(SIGNATURE_METHOD_PARAM_NAME, Arrays.asList(encryptionAlgorithm.getName()));
            return new ClassicApiRequestSigner().formatAndSign(paramMap, this.encryptionKey);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ClassicApiRequestSigner.class);

    //private String encryptionAlgorithm;

    public EncryptionAlgorithm getEncryptionAlgorithm(Map<String, List<String>> parameterMap){
        String encryptionAlgorithmString = getSingleValueOrNull(parameterMap, SIGNATURE_METHOD_PARAM_NAME);
	if(encryptionAlgorithmString==null){
	    log.warn("no parameter with name {} found, cannot determine encryption method", SIGNATURE_METHOD_PARAM_NAME);
	}
        return encryptionAlgorithmString!=null ? EncryptionAlgorithm.fromString(encryptionAlgorithmString) : null;
    }
    public String formatAndSign(Map<String, List<String>> params, String key){
        return super.formatAndSign(/*method=*/null, /*url=*/null, params, key);
    }

    protected String processParamNamePreEncryption(final String param){
        return param; //no url encoding for names pre-encryption
    }
    protected String processParamValuePreEncryption(final String value){
        return value; //no url encoding for values pre-encryption
    }

    protected String processSortedParameterStringPreEncryption(final String sortedParamString){
        return sortedParamString; //no url encoding for sorted param string 
    }
    
    protected String getBaseStr(final String method, final String url){
        return "";
    }

}
