package com;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 資料庫中的加密密碼
        String storedPassword = "$2a$10$sI20jjwCie5CvLJ27VzPq.xkcmuTpEpybxud5y.TC9JOSaOsYKtre";

        // 測試原始密碼
        String rawPassword = "Ab123456";

        // 驗證
        boolean matches = encoder.matches(rawPassword, storedPassword);
        System.out.println("密碼匹配結果: " + matches);
    }
}