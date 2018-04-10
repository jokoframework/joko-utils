package io.github.jokoframework.utils.location;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilidades para trabajar con geolocalizacion
 * 
 * @author ncanata
 *
 */
public class LocationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationUtils.class);

    /**
     * Calcula la distancia entre dos puntos en latitud y longitud tomando en
     * cuenta la diferencia en elevación (Elevación en metros). Si no esta interesado
     * en tomar en cuenta la diferencia en elevación pasar 0.0 en ambas. Usa el método
     * Haversine como su base.
     *
     * @param lat1 Latitud 1
     * @param lat2 Latitud 2
     * @param lon1 Longitud 1
     * @param lon2 Longitud 2
     * @param el1 Elevación 1
     * @param el2 Elevación 2
     * @return Distancia en metros
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        final int earthRadius = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        printDistance(distance);
        return Math.sqrt(distance);
    }

    /**
     * Calcula la distancia entre dos puntos geográficos sin contar la altura y retorna dicha distancia en la medida
     * que se incluye como argumento.
     *
     * @param lat1 Latitud 1
     * @param lon1 Longitud 1
     * @param lat2 Latitud 2
     * @param lon2 Longitud 2
     * @param unit String con la unidad de medida del resultado, "K" para kilómetros, "M" para metros, "N" para Millas
     *             Náuticas y cualquier otro valor para Millas
     * @return Distancia entre dos puntos geográficos en la unidad de medida especificada (Si no se reconoce la medida
     *         sera en Millas)
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        } else if (unit == "M") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    /**
     * Transforma el argumento de grados a radianes.
     *
     * @param deg Cantidad en grados
     * @return El argumento "deg" pasado a radianes
     */
	private static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    /**
     * Transforma el argumento de radianes a grados.
     *
     * @param rad Cantidad en radianes
     * @return El argumento "rad" pasado a grados
     */
    private static double rad2deg(double rad) {
        return rad * 180 / Math.PI;
    }

    /**
     * Utiliza el argumento "distance" tal que si este es menor o igual a 1000.0 se retorna un String que contiene el
     * monto con dos decimales, separadores de miles y la unidad de medida "m" concatenada al final. Si es mayor a
     * 1000.0 "distance" se retorna lo mencionado pero con el monto pasado a kilómetros, sin decimales y "km"
     * concatenado
     *
     * @param distance Un Double conteniendo una distancia en metros
     * @return Un String que contiene el argumento "distance" con solo 2 decimales, separadores de miles, unidad de
     * medida, si la cantidad es mayor a 1,000 metros se devuelve el valor en kilometros y sin decimales
     */
    public static String formatDistance(Double distance){
        DecimalFormat formatter = new DecimalFormat("###,###.##");
        String sufix = " m";
        if(distance > 1000d){
            distance = distance/1000;
            sufix = " km";
        }
        String formattedDistance = formatter.format(distance);
        return formattedDistance.concat(sufix);
    }

    /**
     * Muestra la raiz cuadrada de la distancia pasada mediante el Logger
     *
     * @param distance Distancia
     */
    private static void printDistance(Double distance){
        distance = Math.sqrt(distance);
        LOGGER.debug("CALCULATED DISTANCE: {}", formatDistance(distance));
    }

}
