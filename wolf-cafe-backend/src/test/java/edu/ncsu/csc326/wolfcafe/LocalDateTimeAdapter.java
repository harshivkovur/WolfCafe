package edu.ncsu.csc326.wolfcafe;

import java.io.IOException;
import java.time.LocalDateTime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * AI generated class to allow GSON to parse java time to/from string
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write ( final JsonWriter out, final LocalDateTime value ) throws IOException {
        out.value( value == null ? null : value.toString() );
    }

    @Override
    public LocalDateTime read ( final JsonReader in ) throws IOException {
        final String str = in.nextString();
        return ( str == null || str.isEmpty() ) ? null : LocalDateTime.parse( str );
    }
}
