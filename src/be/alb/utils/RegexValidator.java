package be.alb.utils;

import java.util.regex.Pattern;

public class RegexValidator {

    // lastname and firstname, allows hyphens
	private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ\\-]{1,255}$");

    // CITY: same than name but allowing spaces
    private static final Pattern CITY_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ\\-\\s]{1,255}$");

    // POSTALCODE: allows numbers, usually 5 characters, adjustable for formats with max 10.
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{1,10}$");

    // STREETNAME: alphabetic and alphanumeric characters
    private static final Pattern STREET_NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ0-9\\-\\s]{1,255}$");

    // STREETNUMBER: allows numbers and possible suffixes 
    private static final Pattern STREET_NUMBER_PATTERN = Pattern.compile("^\\d{1,5}[A-Za-z]?$");

    
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

   
    public static boolean isValidCity(String city) {
        return city != null && CITY_PATTERN.matcher(city).matches();
    }

    
    public static boolean isValidPostalCode(String postalCode) {
        return postalCode != null && POSTAL_CODE_PATTERN.matcher(postalCode).matches();
    }

   
    public static boolean isValidStreetName(String streetName) {
        return streetName != null && STREET_NAME_PATTERN.matcher(streetName).matches();
    }

    public static boolean isValidStreetNumber(String streetNumber) {
        return streetNumber != null && STREET_NUMBER_PATTERN.matcher(streetNumber).matches();
    }
}
