package com.packagename.myapp.spring.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class TypeParseService {

    public TypeParseService() {
    }

    public LocalDate parseDate(String date) {
        if (date != null && date != "null" && !date.contains("Дата")) {
            LocalDateTime datetime = LocalDateTime.parse(date,
                    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US));
            return  datetime.toLocalDate();
        }
        return null;
    }

}
