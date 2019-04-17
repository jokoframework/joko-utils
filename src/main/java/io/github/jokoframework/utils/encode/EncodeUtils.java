/**
 * 
 */
package io.github.jokoframework.utils.encode;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import io.github.jokoframework.utils.constants.JokoConstants;
import io.github.jokoframework.utils.exception.JokoUtilsException;

/**
 * @author bsandoval
 *
 */
public class EncodeUtils {

    private EncodeUtils () {
        //No public constructor
    }

    /**
     * Convierte el String recibido a la codificación ISO-8859-1
     *
     * @param s String a codificar
     * @return String convertido
     */
    public static String convertToLATIN(String s) throws JokoUtilsException {
        byte[] iso;
        try {
            iso = s.getBytes(JokoConstants.LATIN1_CHARSET);
            s = new String(iso, JokoConstants.LATIN1_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new JokoUtilsException("Error changing encoding to ISO-8859-1 (LATIN1). ", e);
        }
        return s;
    }

    /**
     * Convierte el String recibido al set de caracteres pedido
     *
     * @param s String a codificar
     * @param charset set al cual codificar
     * @return String convertido
     */
    public static String convertToCharset(String s, String charset) throws JokoUtilsException {
        byte[] iso;
        try {
            iso = s.getBytes(charset);
            s = new String(iso, charset);
        } catch (UnsupportedEncodingException e) {
            throw new JokoUtilsException("Error changing encoding to " + charset, e);
        }
        return s;
    }

    /**
     * Remueve los acentos y los �. La conversión directa de charset
     * no se convirtió bien dejando el caracter "?"
     *
     * @param s String a codificar
     * @return String convertido
     */
    public static String convertToUS_ACII(String s) {
        char[] especiales = new char[] { 'á', 'é', 'í', 'o', 'u', 'Á', 'É', 'Í', 'Ó', 'Ú', 'ñ', 'Ñ' };
        char[] reemplazos = new char[] { 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U', 'n', 'N' };
        String retorno = s;
        for (int i = 0; i < especiales.length; i++) {
            retorno = retorno.replace(especiales[i], reemplazos[i]);
        }
        return retorno;
    }

    /**
     * Convierte de un set de caracteres especificado a otro especificado
     *
     * @param encoded String
     * @param charSetSource Set de caracteres del String inicial
     * @param charSetTarget Set de caracteres al cual codificar el String
     * @return String recodificado
     */
    public static String convertEncoding(String encoded, String charSetSource, String charSetTarget)
            throws JokoUtilsException {
        String decoded = encoded;
        Charset charsetUTF = Charset.forName(charSetTarget);
        Charset charsetISO = Charset.forName(charSetSource);
        CharsetEncoder encoder = charsetISO.newEncoder();
        CharsetDecoder decoder = charsetUTF.newDecoder();
        try {
            // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
            // The new ByteBuffer is ready to be read.
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(encoded));

            // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character
            // ByteBuffer and then to a string.
            // The new ByteBuffer is ready to be read.
            CharBuffer cbuf = decoder.decode(bbuf);
            decoded = cbuf.toString();
        } catch (CharacterCodingException e) {
            throw new JokoUtilsException("Error changing encoding from " + charSetSource + " to " + charSetTarget, e);
        }
        return decoded;
    }

}
