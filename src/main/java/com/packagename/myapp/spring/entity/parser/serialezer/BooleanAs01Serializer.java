package com.packagename.myapp.spring.entity.parser.serialezer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BooleanAs01Serializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            jsonGenerator.writeNumber(0);
        } else {
            jsonGenerator.writeNumber(value ? 1 : 0);
        }
    }

    @Override
    public Class<Boolean> handledType() {
        return Boolean.class;
    }

}
