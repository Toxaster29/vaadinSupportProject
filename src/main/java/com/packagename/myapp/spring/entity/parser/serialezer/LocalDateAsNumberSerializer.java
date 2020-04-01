package com.packagename.myapp.spring.entity.parser.serialezer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Сериализатор LocalDae в вид 20200113
 */

public class LocalDateAsNumberSerializer extends JsonSerializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void serialize(LocalDate date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(date.format(FORMATTER));
    }

    @Override
    public Class<LocalDate> handledType() {
        return LocalDate.class;
    }

}
