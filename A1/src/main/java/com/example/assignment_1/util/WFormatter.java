package com.example.assignment_1.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WFormatter {

    /**
     * Map of escapable characters. Links the character that needs to be escaped to the html escape
     */
    private final static Map<Character,String> htmlMap;

    static {
        Map<Character,String> escapeMap = new HashMap<>();
        //add any characters that should be escaped
        escapeMap.put('<',"&lt;");
        escapeMap.put('>',"&gt;");
        escapeMap.put('&',"&amp;");
        escapeMap.put('"',"&quot;");
        escapeMap.put('\'',"&#39;");
        htmlMap = Collections.unmodifiableMap(escapeMap);
    }

    /**
     * Escapes the given string so that it is presentable for HTML. This will replace any signs
     * that are normally used for HTML
     *
     * @param content The content to escape. This cannot be null
     * @return The new escaped string.
     */
    public static String escapeHTML(String content) {
        StringBuilder escaped = new StringBuilder();
        for(int i=0;i<content.length();i++) {
            char key = content.charAt(i);
            if(htmlMap.containsKey(key)) {
                escaped.append(htmlMap.get(key));
            } else {
                escaped.append(key);
            }
        }
        return escaped.toString();
    }
}
