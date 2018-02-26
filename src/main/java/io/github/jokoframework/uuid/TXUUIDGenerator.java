package io.github.jokoframework.uuid;

import org.apache.commons.codec.binary.Base32;

import java.util.UUID;

/**
 * <p>
 * Esta clase se creo para generar UUIDs en cada transaccion. Los UUIDs se
 * utilizan en base a la clase {@link UUID} utilizando primero los bits menos
 * significativos (con mayor entropía) y los que correspondan del long mas
 * significativo
 * </p>
 * <p>
 * El largo en las transacciones es de 96bits. <b>20 caracteres por 5 bits cada
 * uno nos da 100, pero necesitamos que sea multiplo de 8 )
 * </p>
 * <p>
 * Máximo de números posibles
 * </p>
 * <p>
 * Con 96 bits tenemos 2^96 numeros posibles. Si tenemos un pico de 50 TPS
 * (transacciones por segundo) y asumimos que esto se mantiene constante
 * podríamos utilizar este numero por los siguientes 5*10^19 años. (Obs.:Seguir
 * leyendo para ver el analisis de colisiones)
 * 
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * (2 ^ 96) / (50 * 24 * 60 * 60 * 365)
 * </pre>
 * 
 * </blockquote> Calculo en <a href=
 * "http://www.wolframalpha.com/input/?i=2%5E96%2F%2850*24*60*60*365%29" >
 * Wolfram Alpha</a>
 * 
 * 
 * <p>
 * Codificacion en Base32
 * </p>
 * <p>
 * La codificación en Base32 provee la ventaja de ser muy legible para humanos,
 * generando "pretty URLs". http://www.crockford.com/wrmg/base32.html. Por este
 * motivo elegimos Base32 para la codificación.
 * 
 * JCARD tiene un largo de maximo 12 caracteres (va a cambiar a 20). Teniendo en
 * cuenta que Base32 necesita 5bits para cada caracter, entonces esto nos da
 * como maximo 100bits (12*5) Para llegar al primer multiplo mas cercano de 8
 * bits (1 byte) nos quedamos en 96 bits, es decir 12 bytes.
 * </p>
 * <p>
 * <blockquote>
 * 
 * <pre>
 *     12^62 (jcard limit) >> 12^32 (limite con base 32) >> 2^64 (limite del id generado) > 2^56
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * </p>
 * <p>
 * Probabilidad de colisión
 * </p>
 * <p>
 * Si pensamos tener un alto TPS como 8, y lo mantenemos constante por los
 * proximos 20 años nos da un total de :
 * 
 * </p>
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 8*24*60*60*365*20= 5.045.760.000
 * </pre>
 * 
 * </blockquote> Llamamos a este valor "n"
 * </p>
 * <p>
 * Aplicando la formula para UUID generados de manera random. <b>Fuente
 * https://en.wikipedia.org/wiki/Universally_unique_identifier#
 * Random_UUID_probability_of_duplicates https://tools.ietf.org/html/rfc4122
 * <blockquote>
 * 
 * <pre>
 * P(n) = 1- e ^ ( -n^2 / 2x)
 * </pre>
 * 
 * </blockquote>
 * </p>
 * <p>
 * En la formula x es la cantidad de valores que puede tener un id, en nuestro
 * caso 2^96. n es la cantidad de IDs que pensamos generar (5.045.760.000). Esto
 * da como resultado 1.
 * 
 * </p>
 * <a href=
 * "http://www.wolframalpha.com/input/?i=1-e%5E%28+%28-5045760000%5E2%29%2F%282*2%5E96%29%29"
 * >Link a Wolfram Alpha</a>
 * <p>
 * Esta clase fue inicialmente pensada para generar UUIDs de transacciones pero
 * perfectamente se puede acomodar a UUIDs de otros recursos
 * </p>
 * 
 * @author danicricco
 *
 */
// TODO luego de analizar quedamos en subir el largo generado de IDs
public class TXUUIDGenerator {

    private static final int BYTE_SIZE = 8;

    private static final int BITS_PER_CHARACTER = 5;

    private final int characterLength;
    private final int numberOfOctets;

    private static final int DEFAULT_STRING_LENGTH = 12;

    /**
     * Como maximo se producen UUIDs de characterLength
     * 
     * @param characterLength longitud de caracteres
     */
    public TXUUIDGenerator(int characterLength) {
        this.characterLength = characterLength;
        this.numberOfOctets = (characterLength * BITS_PER_CHARACTER) / BYTE_SIZE;
        if (this.numberOfOctets > (Long.SIZE / BYTE_SIZE) * 2) {
            // Tenemos 2 longs para uuids. cualquier cosa encia de eso no
            // funciona
            throw new IllegalArgumentException("invalid  characterLength. Too Long");
        }
    }

    public TXUUIDGenerator() {
        this(DEFAULT_STRING_LENGTH);
    }

    public String generate() {

        UUID uuid = UUID.randomUUID();

        byte[] buffer = new byte[numberOfOctets];

        long mostSignificantBits = uuid.getMostSignificantBits();
        int numberOfOctectsFromMostSignificant = 0;
        int numberOfOctectsFromLeastSignificantBits = BYTE_SIZE;
        if (numberOfOctets > Long.SIZE / BYTE_SIZE) {
            // Calcula la cantidad de octetos de los bytes mas significativos
            numberOfOctectsFromMostSignificant = numberOfOctets - Long.SIZE / BYTE_SIZE;
            toArray(mostSignificantBits, numberOfOctectsFromMostSignificant, buffer, 0);
        } else {
            numberOfOctectsFromLeastSignificantBits = numberOfOctets;
        }

        long leastSignificantBits = uuid.getLeastSignificantBits();
        toArray(leastSignificantBits, numberOfOctectsFromLeastSignificantBits, buffer,
                numberOfOctectsFromMostSignificant);

        // Each line of encoded data will be at most of the given length
        // (rounded down to nearest multiple of

        int lineLength = characterLength + characterLength % BYTE_SIZE;

        Base32 encoder = new Base32(lineLength);
        String s = encoder.encodeToString(buffer);

        // TODO para mejorar la busqueda de las transacciones en la BD se podría
        // incluir como primer byte algo con respecto al tiempo. Ejemplo agrupar
        // las transacciones cada x segundos. Esto permitiría que el arbol de
        // busqueda basado en los IDs de las transacciones sea efectivamente
        // util
        // Aca un articulo interesante al respecto
        // https://eager.io/blog/how-long-does-an-id-need-to-be/

        // FIXME bajon que despues de tanto cuidado con traduccion a binario
        // tenga que hacer un replace
        return s.replace("=", "").trim();

    }

    /**
     * Dentro de la definición de UUID los bits menos significativos son donde
     * hay mayor entropia. The least significant long consists of the following
     * unsigned fields: 0xC000000000000000 variant 0x3FFF000000000000 clock_seq
     * 0x0000FFFFFFFFFFFF node
     * 
     * @param l
     * @param size
     * @return
     */
    public static byte[] toArray(long l, int size, byte buff[], int offset) {

        for (int i = offset; i < size + offset; i++) {
            buff[i] = (byte) ((l >> (i * 8)) & 0XFF);
        }

        return buff;
    }

    public String test(String[] args) {
        TXUUIDGenerator txuuid = new TXUUIDGenerator(12);
        String generate = txuuid.generate();
        String salida = "";
        salida = generate + "\n";
        salida += "---" + "\n";
        salida += generate.length() + "\n";

        int totalLength = 20;
        int totalBits = totalLength * 5;// Cada caracter tiene 5 bits (Base 32)
        int cantidadBytes = totalBits / 8 + totalBits % 8;
        salida += cantidadBytes + "\n";
        return salida;
    }

}
