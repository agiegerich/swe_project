package controllers;

import exceptions.EncryptorException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

import play.Logger;

public class Encryptor {
    public static String encrypt(String key, String initVector, String value) throws EncryptorException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] inputIvBytes = initVector.getBytes("UTF-8"); 
            byte[] hashedIvBytes = digest.digest(inputIvBytes);
            IvParameterSpec iv = new IvParameterSpec(hashedIvBytes);

            byte[] inputKeyBytes = key.getBytes("UTF-8"); 
            byte[] hashedKeyBytes = digest.digest(inputKeyBytes);
            SecretKeySpec skeySpec = new SecretKeySpec(hashedKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            //Logger.debug("encrypted string: "+ Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            throw new EncryptorException("Failed to encrypt.", e);
        }
    }

    public static String decrypt(String key, String initVector, String encrypted) throws EncryptorException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] inputIvBytes = initVector.getBytes("UTF-8"); 
            byte[] hashedIvBytes = digest.digest(inputIvBytes);
            IvParameterSpec iv = new IvParameterSpec(hashedIvBytes);

            byte[] inputKeyBytes = key.getBytes("UTF-8"); 
            byte[] hashedKeyBytes = digest.digest(inputKeyBytes);
            SecretKeySpec skeySpec = new SecretKeySpec(hashedKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception e) {
            throw new EncryptorException("Failed to decrypt.", e);
        }
    }
}
