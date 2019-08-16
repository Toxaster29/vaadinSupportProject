package com.packagename.myapp.spring.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class DateUtils {

    public static final Locale RU = new Locale("ru", "RU");
    public static final DateTimeFormatter DATE_SIMPLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Nullable
    public static LocalDate toLocalDate(@Nullable final Timestamp timestamp) {
        return timestamp == null
                ? null
                : timestamp.toLocalDateTime().toLocalDate();
    }

}
