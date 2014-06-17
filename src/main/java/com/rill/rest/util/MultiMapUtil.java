package com.rill.rest.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MultiMapUtil {
    
    public static void addValueToMap(Map<String, List<String>> map, String key, String value){
        List<String> values = map.get(key);
        if(values == null){
            values = new LinkedList();
            map.put(key, values);
        }
        values.add(value);
    }

    public static String getSingleValueOrNull(Map<String, List<String>> paramMap, String param){
        List<String> values = paramMap.get(param);
        if(values==null || values.size()>1){
            return null;
        }
        return values.get(0);
    }

    public static boolean hasMultipleValues(Map<String, List<String>> paramMap, String param){
        List<String> values = paramMap.get(param);
        return values!=null && values.size() > 1;
    }

}
