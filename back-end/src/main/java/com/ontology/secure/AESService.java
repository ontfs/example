package com.ontology.secure;


import com.github.ontio.common.Helper;
import com.ontology.utils.Base64ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESService {

    private static Logger logger = LoggerFactory.getLogger(AESService.class);

    private static final String KEY = "1992101219960210";

    private static final String IV = "1996021019921012";


    public static void main(String[] args) throws UnsupportedEncodingException {
        String content = "testpwd";
        System.out.println("加密前：" + content);
        byte[] encrypted = AES_CBC_Encrypt(content.getBytes());
        String rs = byteToHexString(encrypted);
        System.out.println("加密后：" + rs);

        byte[] bytes = Helper.hexToBytes(rs.toLowerCase());
        byte[] decrypted = AES_CBC_Decrypt(bytes);
        String str2 = new String(decrypted);
        System.out.println("解密后：" + str2);

    }

    public static byte[] AES_CBC_Encrypt(byte[] content) {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(KEY.getBytes()));
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AES_CBC_Encrypt error...", e);
        }
        return null;
    }

    public static byte[] AES_CBC_Decrypt(byte[] content) {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(KEY.getBytes()));
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AES_CBC_Decrypt error...", e);
        }
        return null;
    }

    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
}