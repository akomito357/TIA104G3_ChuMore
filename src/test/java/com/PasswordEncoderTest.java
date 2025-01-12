package com;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Ab123456"; // 替換成你的原始密碼
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("加密後的密碼: " + encodedPassword);
    }
}
