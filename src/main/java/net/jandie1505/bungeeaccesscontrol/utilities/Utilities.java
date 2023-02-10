package net.jandie1505.bungeeaccesscontrol.utilities;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Utilities {
    public static String replacePlaceholders(String message, Map<String, String> placeholders) {
        for (String placeholder : placeholders.keySet()) {
            message = message.replace("{" + placeholder + "}", placeholders.get(placeholder));
        }

        return message;
    }

    public static String createTimeString(long epoch, String pattern) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
            return dateTimeFormatter.format(Instant.ofEpochSecond(epoch));
        } catch (DateTimeException e) {
            return "TIME FORMAT ERROR";
        }
    }

    public static String createRemainingTime(long input, String timeString) {
        long days = input / 86400;
        long hours = (input % 86400) / 3600 ;
        long minutes = ((input % 86400) % 3600) / 60;
        long seconds = ((input % 86400) % 3600) % 60;

        timeString = timeString.replace("{days}", String.valueOf(days));
        timeString = timeString.replace("{hours}", String.valueOf(hours));
        timeString = timeString.replace("{minutes}", String.valueOf(minutes));
        timeString = timeString.replace("{seconds}", String.valueOf(seconds));

        return timeString;
    }
}