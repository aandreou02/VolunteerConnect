package com.example.volunteerconnect;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    public AESUtil() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        secretKey = keyGenerator.generateKey();

        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);
    }

    public AESUtil(String key, String iv) {
        secretKey = new SecretKeySpec(Base64.decode(key, Base64.DEFAULT), ALGORITHM);
        ivParameterSpec = new IvParameterSpec(Base64.decode(iv, Base64.DEFAULT));
    }

    public String encrypt(String input) throws Exception {
        Log.d("Encrypt", input);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        Log.d("Encrypt", Base64.encodeToString(encrypted, Base64.DEFAULT));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public String decrypt(String input) throws Exception {
        Log.d("Decrypt", input);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decodedInput = Base64.decode(input, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(decodedInput);
        Log.d("Decrypt", new String(decrypted));
        return new String(decrypted);
    }

    public String getKey() {
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }

    public String getIv() {
        return Base64.encodeToString(ivParameterSpec.getIV(), Base64.DEFAULT);
    }
}
