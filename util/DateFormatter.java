package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static String formatDateToString(LocalDate date) {
        return date.format(dateTimeFormatter);
    }

    public static LocalDate formatStringToDate(String date) {
        return LocalDate.parse(date, dateTimeFormatter);
    }
}
