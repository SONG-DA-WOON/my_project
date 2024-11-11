package com.dmc.common;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.lang.reflect.Field;

public class CustomEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {



    private static <T extends Enum<T>> Class<T> getEnumClass(Class<?> dtoClass, String fieldName) throws NoSuchFieldException {
        Field field = dtoClass.getDeclaredField(fieldName);
        return (Class<T>) field.getType();
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        final String value = jsonParser.getValueAsString();
        final Class<?> dtoClass = jsonParser.getCurrentValue().getClass();
        final String fieldName = jsonParser.currentName();

        try {
            final Class<T> enumClass = getEnumClass(dtoClass, fieldName);
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException | NoSuchFieldException e) {
            return null;
        }
    }
}

