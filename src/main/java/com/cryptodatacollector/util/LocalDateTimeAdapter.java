package com.cryptodatacollector.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс LocalDateTimeAdapter реализует интерфейс TypeAdapter для сериализации и десериализации объектов LocalDateTime в формате JSON.
 * Он использует DateTimeFormatter для форматирования и парсинга даты и времени.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Сериализует объект LocalDateTime в строку JSON.
     *
     * @param out  Объект JsonWriter для записи JSON.
     * @param value Объект LocalDateTime для сериализации.
     * @throws IOException Если произошла ошибка при записи в JsonWriter.
     */
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(FORMATTER.format(value));
        }
    }

    /**
     * Десериализует строку JSON в объект LocalDateTime.
     *
     * @param in Объект JsonReader для чтения JSON.
     * @return Объект LocalDateTime, полученный из строки JSON.
     * @throws IOException Если произошла ошибка при чтении из JsonReader.
     */
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String dateTimeString = in.nextString();
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }
}
