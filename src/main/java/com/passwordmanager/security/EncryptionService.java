package com.passwordmanager.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Service to handle encryption and decryption of passwords or other sensitive data.
 * Uses AES-128 with a static secret key.
 */
@Component
public class EncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    // 16-character secret key for AES encryption (AES-128)
    private static final String SECRET_KEY = "MySecretKey12345";

    /**
     * Encrypt a plain text string and return Base64 encoded string
     */
    public String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
            logger.debug("Data encrypted successfully");
            return encrypted;
        } catch (Exception e) {
            logger.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt a Base64 encoded AES encrypted string
     */
    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            String decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
            logger.debug("Data decrypted successfully");
            return decrypted;
        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Encrypt and return as UTF-8 byte array (useful for file storage or network transmission)
     */
    public byte[] encryptToBytes(String data) {
        return encrypt(data).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Decrypt from UTF-8 byte array
     */
    public String decryptFromBytes(byte[] encryptedBytes) {
        return decrypt(new String(encryptedBytes, java.nio.charset.StandardCharsets.UTF_8));
    }
}