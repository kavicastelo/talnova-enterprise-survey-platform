package com.talnova.tesp.employeeservice.security;

public class PiiMaskingUtil {

    private PiiMaskingUtil() {
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.***";
        }
        int atIndex = email.indexOf('@');
        String namePart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (namePart.length() <= 2) {
            return namePart.charAt(0) + "***" + domainPart;
        }
        return namePart.charAt(0) + "***" + namePart.charAt(namePart.length() - 1) + domainPart;
    }

    public static String maskFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "*** ***";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.length() <= 1) {
                sb.append(p).append("***");
            } else {
                sb.append(p.charAt(0)).append("***");
            }
            if (i < parts.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 7) {
            return "+00****0000";
        }
        int len = phone.length();
        return phone.substring(0, 3) + "****" + phone.substring(len - 3);
    }
}
