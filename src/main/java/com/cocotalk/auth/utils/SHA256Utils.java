package com.cocotalk.auth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * SHA-256 암호화
 *
 * @author Minchang Jang
 *
 */
@Slf4j
@Component
public class SHA256Utils {

    private static String salt;

    @Value("${sha256.salt}")
    public void setSalt(String saltKey) {
        salt = saltKey;
    }

    /**
     * SHA-256 암호화 함
     * @param source 원본
     * @return
     */
    public static String getEncrypt(String source) {

        byte[] saltBytes = salt.getBytes();
        String result = "";

        byte[] a = source.getBytes();
        byte[] bytes = new byte[a.length + saltBytes.length];

        System.arraycopy(a, 0, bytes, 0, a.length);
        System.arraycopy(saltBytes, 0, bytes, a.length, saltBytes.length);

        try {
            // 암호화 방식 지정 메소드
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);

            byte[] byteData = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xFF) + 256, 16).substring(1));
            }

            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

}