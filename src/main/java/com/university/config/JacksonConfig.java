package com.university.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration
public class JacksonConfig {

        private static final List<DateTimeFormatter> DATETIME_FORMATTERS = List.of(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX][X]"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                        DateTimeFormatter.ISO_LOCAL_DATE);

        @Bean
        @Primary
        public ObjectMapper objectMapper() {
                ObjectMapper mapper = new ObjectMapper();

                JavaTimeModule javaTimeModule = new JavaTimeModule();
                mapper.registerModule(javaTimeModule);

                SimpleModule flexModule = new SimpleModule("FlexDateTimeModule");
                flexModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
                flexModule.addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer());
                mapper.registerModule(flexModule);

                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                return mapper;
        }

        public static class FlexibleLocalDateTimeDeserializer
                        extends com.fasterxml.jackson.databind.JsonDeserializer<LocalDateTime> {
                @Override
                public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p,
                                com.fasterxml.jackson.databind.DeserializationContext ctxt)
                                throws java.io.IOException {
                        String value = p.getValueAsString();
                        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
                                try {
                                        return LocalDateTime.parse(value, formatter);
                                } catch (DateTimeParseException ignored) {
                                }
                        }
                        return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
        }

        public static class FlexibleLocalDateDeserializer
                        extends com.fasterxml.jackson.databind.JsonDeserializer<LocalDate> {
                @Override
                public LocalDate deserialize(com.fasterxml.jackson.core.JsonParser p,
                                com.fasterxml.jackson.databind.DeserializationContext ctxt)
                                throws java.io.IOException {
                        String value = p.getValueAsString();
                        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                                try {
                                        return LocalDate.parse(value, formatter);
                                } catch (DateTimeParseException ignored) {
                                }
                        }
                        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                }
        }
}
