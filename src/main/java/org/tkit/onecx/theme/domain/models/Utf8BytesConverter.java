package org.tkit.onecx.theme.domain.models;

import java.nio.charset.StandardCharsets;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Utf8BytesConverter implements AttributeConverter<String, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : attribute.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        return dbData == null ? null : new String(dbData, StandardCharsets.UTF_8);
    }
}
