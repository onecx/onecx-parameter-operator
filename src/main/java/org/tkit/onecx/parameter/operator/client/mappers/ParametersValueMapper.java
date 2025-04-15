package org.tkit.onecx.parameter.operator.client.mappers;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class ParametersValueMapper {

    @Inject
    ObjectMapper mapper;

    public Object toMap(String value) throws ConvertValueException {
        if (value == null) {
            return null;
        }
        try {
            return mapper.readValue(value, Object.class);
        } catch (Exception ex) {
            throw new ConvertValueException(
                    "The given string: " + value + " cannot be transformed to json map", ex);
        }
    }

    public static class ConvertValueException extends RuntimeException {

        public ConvertValueException(String message, Throwable e) {
            super(message, e);
        }
    }
}
