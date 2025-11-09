package com.example.englishlearningapp.util;

import java.security.SecureRandom;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    private static final int BCRYPT_COST = 12;

    /**
     * Mã hóa mật khẩu bằng BCrypt
     */
    public static String hash(String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }

    /**
     * Kiểm tra mật khẩu nhập có khớp với hash không
     */
    public static boolean verify(String password, String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }

    public static class RandomPasswordGenerator {
        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        private static final SecureRandom random = new SecureRandom();

        public static String generate(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            return sb.toString();
        }
    }
}