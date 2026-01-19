package util;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    public static String extractValue(String jsonString, String key) {
        if (jsonString == null || key == null) {
            return null;
        }
        
        String cleanJson = jsonString.trim()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "");
            
        String[] pairs = cleanJson.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String extractedKey = keyValue[0].trim();
                String extractedValue = keyValue[1].trim();
                
                if (extractedKey.equals(key)) {
                    return extractedValue;
                }
            }
        }
        
        return null;
    }
    

    public static Map<String, String> parseJsonToMap(String jsonString) {
        Map<String, String> result = new HashMap<>();
        
        if (jsonString == null) {
            return result;
        }
        
        String cleanJson = jsonString.trim()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "");
            
        String[] pairs = cleanJson.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                result.put(key, value);
            }
        }
        
        return result;
    }

    public static boolean containsKey(String jsonString, String key) {
        return extractValue(jsonString, key) != null;
    }
}