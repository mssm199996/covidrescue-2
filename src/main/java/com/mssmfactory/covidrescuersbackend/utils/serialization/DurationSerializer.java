package com.mssmfactory.covidrescuersbackend.utils.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("hours", String.valueOf(hours));
        jsonGenerator.writeStringField("minutes", String.valueOf(minutes));
        jsonGenerator.writeEndObject();
    }
}
