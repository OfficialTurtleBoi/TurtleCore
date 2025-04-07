package net.turtleboi.turtlecore.util;

public class StringUtils {

    public static String sanitizeString(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String sanitizeName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9/._-]", "");
    }
}

