package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[1-9]\\d{1,14}$"); // Basic international phone format


    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidClientId(int clientId) {
        return clientId > 0;
    }

    public static boolean isValidFutureDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean isValidPoolTime(LocalTime time) {
        if (time == null) {
            return false;
        }
        
        int hour = time.getHour();
        int minute = time.getMinute();
        
        if (hour < 8 || hour >= 22) {
            return false;
        }
        
        if (minute != 0) {
            return false;
        }
        
        return true;
    }

    public static boolean isValidUuid(String uuidStr) {
        try {
            java.util.UUID.fromString(uuidStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}