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
     * Tipos de algoritmos usados para encriptación
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
     * Genera un String aleatorio de seis caracteres.
     *
     * @return random String
     */
    public static String generateRandomPassword() {
        Random a = new Random();
        a.setSeed(System.currentTimeMillis());
        return String.format("%06d", a.nextInt(999999));
    }

    /**
     * Encripta un string con una llave.
     *
     * @param message String que se quiere encriptar
     * @param key En bytes para la encriptación
     * @return String encriptado codificado en Base64
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

    /**
     * Descifra un string con una llave.
     *
     * @param encrypted String codificado en Base64
     * @param key en bytes para el descifrado
     * @param quiet Determina si uno quiere imprimir errores de descifrado. Este parámetro se incluyo por motivos de
     *              compatibilidad con paginas que ya tenían un sistema de encriptado
     * @return String descifrado codificado en Base64
     */
    public static String decryptWithKey(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        if (StringUtils.isNotEmpty(encrypted)) {
            ret = decryptWithByteKey(encrypted, key, quiet);
        }
        return ret;
    }

    /**
     * Descifra un string con una llave.
     *
     * @param encrypted String codificado en Base64
     * @param key en bytes para el descifrado
     * @param quiet Determina si uno quiere imprimir errores de descifrado. Este parámetro se incluyo por motivos de
     *              compatibilidad con paginas que ya tenían un sistema de encriptado
     * @return String descifrado codificado en Base64
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
     * Decsifra String con una llave.
     *
     * @param encrypted String codificado en Base64
     * @param key en bytes para el descifrado
     * @return String descifrado codificado en Base64
     */
    public static String decryptWithKey(String encrypted, byte[] key) {
        return decryptWithKey(encrypted, key, false);
    }

    /**
     * De un byte[] retorna una representación en base 64.
     *
     * @param data los datos a codificar
     * @return la representación en Base64 del array de bytes
     */
    public static String byteToBase64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    /**
     * De una representación en Base64, retorna el byte[] correspondiente.
     *
     * @param data La representación en Base64
     * @return el array binario
     */
    public static byte[] base64ToByte(String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }

    /**
     * Hace un cifrado simple de la contraseña.
     *
     * @param rawPassword Password
     * @return hash bcrypt en Base64
     */
    public static String hashPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCRYPT_COMPLEXITY);
        String encodedSecret = encoder.encode(rawPassword);
        return encodedSecret;
    }

    /**
     * Retorna True si el password y el password cifrado son iguales, de otra forma retorna False
     *
     * @param raw Password en crudo
     * @param encoded Password cifrado
     * @return True/False
     */
    public static boolean matchPassword(String raw, String encoded) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCRYPT_COMPLEXITY);
        return encoder.matches(raw, encoded);
    }

    /**
     * Genera un String en SHA-256
     * @param pwd Password en crudo
     * @return sha256 String
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
     * Genera un String md5
     *
     * @param pwd Password en crudo
     * @return md5 String
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
     * Lee los bytes de el archivo completo y los convierte a un String en Base64
     *
     * @param filePath Camino al archivo
     * @return Contenido del archivo como String en Base64
     * @throws IOException Si no puede leer los bytes
     */
    public static String readFileToBase64(String filePath) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);
        byte[] bytesFromFile = Files.readAllBytes(path);
        return byteToBase64(bytesFromFile);
    }
}
