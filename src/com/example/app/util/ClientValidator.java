package util;

public class ClientValidator {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_PHONE_LENGTH = 7;  
    private static final int MAX_PHONE_LENGTH = 20;
    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MAX_EMAIL_LENGTH = 254;

    public static void validateName(String name) {
        if (!ValidationUtil.isValidString(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Name length must be between %d and %d characters", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
            );
        }

        if (!name.trim().matches("^[a-zA-Z\\s\\-'а-яА-ЯёЁ]+$")) {
            throw new IllegalArgumentException("Name can only contain letters, spaces, hyphens, and apostrophes");
        }
    }

    public static void validatePhone(String phone) {
        if (!ValidationUtil.isValidString(phone)) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        if (phone.length() < MIN_PHONE_LENGTH || phone.length() > MAX_PHONE_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Phone number length must be between %d and %d characters", MIN_PHONE_LENGTH, MAX_PHONE_LENGTH)
            );
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    public static void validateEmail(String email) {
        if (!ValidationUtil.isValidString(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (email.length() < MIN_EMAIL_LENGTH || email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Email length must be between %d and %d characters", MIN_EMAIL_LENGTH, MAX_EMAIL_LENGTH)
            );
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static void validateClientId(int clientId) {
        if (!ValidationUtil.isValidClientId(clientId)) {
            throw new IllegalArgumentException("Client ID must be a positive integer");
        }
    }


    public static void validateClientFields(String name, String phone, String email) {
        validateName(name);
        validatePhone(phone);
        validateEmail(email);
    }

    public static void validateClientFieldsForUpdate(String name, String phone, String email) {
        if (ValidationUtil.isValidString(name)) {
            validateName(name);
        }

        if (ValidationUtil.isValidString(phone)) {
            validatePhone(phone);
        }

        if (ValidationUtil.isValidString(email)) {
            validateEmail(email);
        }
    }
}