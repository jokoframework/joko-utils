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
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * 
     * @param lat1 latitud 1
     * @param lat2 latitud 2
     * @param lon1 longitud 1
     * @param lon2 longitud 2
     * @param el1 elevation 1
     * @param el2 elevation 2
     * @return Distance in Meters
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

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians          ::*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees          ::*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return rad * 180 / Math.PI;
    }

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

    private static void printDistance(Double distance){
        distance = Math.sqrt(distance);
        LOGGER.debug("CALCULATED DISTANCE: {}", formatDistance(distance));
    }

}
