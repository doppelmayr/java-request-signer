package com.rill.rest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpQueryParamParserTest {

    @Test
    public void testTestParse(){
        
        final String paramString = "x=a&y=b";
        Map<String, List<String>> paramMap = HttpQueryParamParser.parseParameters(paramString);
        assertEquals(2, paramMap.size());
        assertNotNull(paramMap.get("x"));
        assertEquals(1, paramMap.get("x").size());
        assertEquals("a", paramMap.get("x").get(0));
        assertNotNull(paramMap.get("y"));
        assertEquals(1, paramMap.get("y").size());
        assertEquals("b", paramMap.get("y").get(0));
        
    }

    @Test
    public void testParseMultipleParamValues(){
        final String paramString = "x=a&x=b";
        Map<String, List<String>> paramMap = HttpQueryParamParser.parseParameters(paramString);
        assertEquals(1, paramMap.size());
        List<String> values = paramMap.get("x");
        assertNotNull(values);
        assertEquals(2, values.size());
        Collections.sort(values);
        assertEquals("a", paramMap.get("x").get(0));
        assertEquals("b", paramMap.get("x").get(1));
    }
}
