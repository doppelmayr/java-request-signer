package com.rill.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import static com.rill.rest.util.MultiMapUtil.addValueToMap;

/**
 * User: rsmith
 * Date: 11/8/13 1:28 PM
 */
public class HttpQueryParamParser {

    public static Map<String, List<String>> parseParameters(String queryParamStr) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        URLEncodedUtils.parse(params, new Scanner(queryParamStr), "UTF-8");
        Map<String, List<String>> paramMap = toMap(params);

        return paramMap;
    }

    private static Map<String, List<String>> toMap(List<NameValuePair> params) {
        Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
        for (NameValuePair param : params) {
            String value = StringUtils.trimToEmpty(param.getValue());
            addValueToMap(paramMap, param.getName(), value);
        }

        return paramMap;
    }
    
}
