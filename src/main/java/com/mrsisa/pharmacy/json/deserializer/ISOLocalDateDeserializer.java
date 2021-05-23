package com.mrsisa.pharmacy.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ISOLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        TextNode node = jsonParser.getCodec().readTree(jsonParser);
        String dateString = node.textValue();
//        System.out.println("ISOLocalDateDeserializer");
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
    }
}