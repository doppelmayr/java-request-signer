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
        private HashAlgorithm hashAlgorithm = null;
        private String hashKey;
        
        public Builder withHashAlgorithm(final HashAlgorithm hashAlgorithm){
            this.hashAlgorithm = hashAlgorithm;
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
            //overrides the hash method if there was one in the map
            this.paramMap.put(SIGNATURE_METHOD_PARAM_NAME, Arrays.asList(hashAlgorithm.getName()));
            return new ClassicApiRequestSigner().formatAndSign(paramMap, this.hashKey);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ClassicApiRequestSigner.class);


    public HashAlgorithm getHashAlgorithm(Map<String, List<String>> parameterMap){
        String hashAlgorithmString = getSingleValueOrNull(parameterMap, SIGNATURE_METHOD_PARAM_NAME);
	if(hashAlgorithmString==null){
	    log.warn("no parameter with name {} found, cannot determine hash method", SIGNATURE_METHOD_PARAM_NAME);
	}
        return hashAlgorithmString!=null ? HashAlgorithm.fromString(hashAlgorithmString) : null;
    }
    public String formatAndSign(Map<String, List<String>> params, String key){
        return super.formatAndSign(/*method=*/null, /*url=*/null, params, key);
    }

    protected String processParamNamePreHash(final String param){
        return param; //no url encoding for names pre-hash
    }
    protected String processParamValuePreHash(final String value){
        return value; //no url encoding for values pre-hash
    }

    protected String processSortedParameterStringPreHash(final String sortedParamString){
        return sortedParamString; //no url encoding for sorted param string 
    }
    
    protected String getBaseStr(final String method, final String url){
        return "";
    }

}
