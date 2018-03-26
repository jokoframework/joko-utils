package io.github.jokoframework.utils.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.github.jokoframework.utils.exception.JokoUtilsException;

public class EncryptUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);

    /**
     * Type of algorithm used for encryptions
     */
    protected static final String ALGORITHM = "Blowfish";
    private static final String ENCODING = "UTF8";
    private static final int BCRYPT_COMPLEXITY = 6;
    
    /*
     * *********************************************************
     */

    private EncryptUtils() {

    }

    /**
     * Generates a random string of six characters
     * 
     * @return random string
     */
    public static String generateRandomPassword() {
        Random a = new Random();
        a.setSeed(System.currentTimeMillis());
        return String.format("%06d", a.nextInt(999999));
    }

    /**
     * Encrypt a string with a key
     *
     * @param message string to encrypt
     * @param key in bytes for encryption
     * @return encrypted string encoded in Base64
     */
    public static String encryptWithKey(String message, byte[] key) {
        String ret = null;
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(key, ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = c.doFinal(message.getBytes(ENCODING));
            ret = byteToBase64(encrypted);
        } catch (Exception e) {
            LOGGER.error("Couldn't encrypt the string: " + e.getMessage(), e);
        }
        return ret;
    }

    public static String decryptWithKey(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        if (StringUtils.isNotEmpty(encrypted)) {
            ret = decryptWithByteKey(encrypted, key, quiet);
        }
        return ret;
    }

    /**
     * Decrypt a string with a key
     * 
     * @param encrypted string encoded in Base64
     * @param key in bytes for decryption
     * @param quiet determines if you want to print decryption
     * errors. This parameter was set for compatibility with pages 
     * that already had encryption.
     * @return decrypted string encoded in Base64
     */
    private static String decryptWithByteKey(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        try {
            /* Encrypted value converted to byte */
            byte[] rawEnc = base64ToByte(encrypted);
            Cipher c = Cipher.getInstance(EncryptUtils.ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(key, EncryptUtils.ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] raw = c.doFinal(rawEnc);
            ret = new String(raw, ENCODING);
        } catch (Exception e) {
            if (!quiet)
                LOGGER.error("Couldn't decrypt the string: " + encrypted, e);
            if (LOGGER.isTraceEnabled()) {
                if (quiet) // solo vuelvo a imprimir si es quiet, porque sino ya
                    // se imprime antes
                    LOGGER.trace("No se pudo desencriptar la cadena: " + encrypted);
                try {
                    LOGGER.trace("\tkey: " + new String(key, "UTF-8"));
                } catch (UnsupportedEncodingException pE) {
                    LOGGER.error("Couldn't encode the string", pE);
                }
            }
        }
        return ret;
    }
    
    /**
     * Decrypt a string with a key
     * 
     * @param encrypted string encoded in Base64
     * @param key in bytes for decryption
     * @return decrypted string encoded in Base64
     */
    public static String decryptWithKey(String encrypted, byte[] key) {
        return decryptWithKey(encrypted, key, false);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data los datos a codificar
     * @return la representación en Base64 del array de bytes
     */
    public static String byteToBase64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    /**
     * From a base 64 representation, returns the corresponding byte[]
     *
     * @param data The base64 representation
     * @return el array binario
     */
    public static byte[] base64ToByte(String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }

    /**
     * Makes a simple cipher of the password
     *
     * @param rawPassword password
     * @return hash bcrypt on Base64
     */
    public static String hashPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCRYPT_COMPLEXITY);
        String encodedSecret = encoder.encode(rawPassword);
        return encodedSecret;
    }

    /**
     * Returns true if the passwords match.
     * Otherwise return false.
     * 
     * @param raw password
     * @param encoded password
     * @return true/false
     */
    public static boolean matchPassword(String raw, String encoded) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCRYPT_COMPLEXITY);
        return encoder.matches(raw, encoded);
    }

    /**
     * Generates a SHA-256 string
     * @param pwd raw password
     * @return sha256 string
     */
    public static String sha256(String pwd) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(pwd.getBytes("UTF-8"));
            return byteToBase64(messageDigest.digest());
        } catch (Exception e) {
            LOGGER.error("Couldn't generate the string SHA-256", e);
            return null;
        }
    }
    
    /**
     * Generates a md5 string
     * 
     * @param pwd raw password
     * @return md5 string
     */
    public static String md5(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(pwd.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new JokoUtilsException(e);
        }
    }

    /**
     * Reads all archive's bytes and turns them on a Base64 string
     *
     * @param filePath path for read
     * @return archive content on Base64
     * @throws IOException if cannot read the bytes
     */
    public static String readFileToBase64(String filePath) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);
        byte[] bytesFromFile = Files.readAllBytes(path);
        return byteToBase64(bytesFromFile);
    }
}
