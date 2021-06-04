package com.github.javiercanillas.jackson.masker.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.github.javiercanillas.jackson.masker.ser.MaskStringSerializer;
import com.github.javiercanillas.jackson.masker.view.Masked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.mockito.Mockito.*;

class MaskStringTest {

    public static class TestObject {

        private String stringValue;
        @MaskString
        private String sensibleString;
        @MaskString(keepLastCharacters = 6)
        private String sensibleStringKeepLastCharacters;

        public String getStringValue() {
            return stringValue;
        }

        void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getSensibleString() {
            return sensibleString;
        }

        void setSensibleString(String sensibleString) {
            this.sensibleString = sensibleString;
        }

        public String getSensibleStringKeepLastCharacters() {
            return sensibleStringKeepLastCharacters;
        }

        void setSensibleStringKeepLastCharacters(String sensibleStringKeepLastCharacters) {
            this.sensibleStringKeepLastCharacters = sensibleStringKeepLastCharacters;
        }
    }

    @Test
    void objectMapper() throws JsonProcessingException {
        String stringRepresentation;
        TestObject obj;
        ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter PIIWriter = mapper.writerWithView(Masked.class);
        final ObjectWriter normalWriter = mapper.writer();

        String stringValue = "aabbccdd";
        obj = new TestObject();
        obj.setSensibleString(stringValue);
        obj.setSensibleStringKeepLastCharacters(stringValue);
        obj.setStringValue(stringValue);

        stringRepresentation = PIIWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabbccdd\",\"sensibleString\":\"********\",\"sensibleStringKeepLastCharacters\":\"**bbccdd\"}",
                stringRepresentation);

        stringRepresentation = normalWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabbccdd\",\"sensibleString\":\"aabbccdd\",\"sensibleStringKeepLastCharacters\":\"aabbccdd\"}",
                stringRepresentation);

        stringValue = "aabb";
        obj = new TestObject();
        obj.setSensibleString(stringValue);
        obj.setSensibleStringKeepLastCharacters(stringValue);
        obj.setStringValue(stringValue);

        stringRepresentation = PIIWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabb\",\"sensibleString\":\"****\",\"sensibleStringKeepLastCharacters\":\"aabb\"}",
                stringRepresentation);

        stringRepresentation = normalWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabb\",\"sensibleString\":\"aabb\",\"sensibleStringKeepLastCharacters\":\"aabb\"}",
                stringRepresentation);

        stringValue = null;
        obj = new TestObject();
        obj.setSensibleString(stringValue);
        obj.setSensibleStringKeepLastCharacters(stringValue);
        obj.setStringValue(stringValue);

        stringRepresentation = PIIWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":null,\"sensibleString\":null,\"sensibleStringKeepLastCharacters\":null}",
                stringRepresentation);

        stringRepresentation = normalWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":null,\"sensibleString\":null,\"sensibleStringKeepLastCharacters\":null}",
                stringRepresentation);
    }

    @Test
    void acceptJsonFormatVisitor() throws NoSuchFieldException, IllegalAccessException, JsonMappingException {
        final Field defaultSerializerField = MaskStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        MaskStringSerializer serializer = new MaskStringSerializer(0, '*');
        defaultSerializerField.set(serializer, inner);

        serializer.acceptJsonFormatVisitor(null, null);
        verify(inner, times(1))
                .acceptJsonFormatVisitor(nullable(JsonFormatVisitorWrapper.class), nullable(JavaType.class));
    }

    @Test
    void getSchema() throws NoSuchFieldException, IllegalAccessException {
        final Field defaultSerializerField = MaskStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        MaskStringSerializer serializer = new MaskStringSerializer(0, '*');
        defaultSerializerField.set(serializer, inner);

        serializer.getSchema(null, null);
        verify(inner, times(1))
                .getSchema(nullable(SerializerProvider.class), nullable(Type.class));
    }

    @Test
    void isEmpty() throws NoSuchFieldException, IllegalAccessException {
        final Field defaultSerializerField = MaskStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        MaskStringSerializer serializer = new MaskStringSerializer(0, '*');
        defaultSerializerField.set(serializer, inner);

        serializer.isEmpty(null, null);
        verify(inner, times(1))
                .isEmpty(nullable(SerializerProvider.class), nullable(Object.class));
    }

    @Test
    void serialize() throws NoSuchFieldException, IllegalAccessException, IOException {
        final Field defaultSerializerField = MaskStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        MaskStringSerializer serializer = new MaskStringSerializer(0, '*');
        defaultSerializerField.set(serializer, inner);

        String value = "a";
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);
        serializer.serialize(value, gen, provider);
        verify(inner, times(1))
                .serialize(value, gen, provider);
    }

    @Test
    void serializeWithType() throws NoSuchFieldException, IllegalAccessException, IOException {
        final Field defaultSerializerField = MaskStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        MaskStringSerializer serializer = new MaskStringSerializer(0, '*');
        defaultSerializerField.set(serializer, inner);

        String value = "a";
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);
        TypeSerializer typeSerializer = mock(TypeSerializer.class);

        // No view active
        serializer.serializeWithType(value, gen, provider, typeSerializer);
        verify(inner, times(1))
                .serializeWithType(value, gen, provider, typeSerializer);

        reset(inner);

        // Masked view is active
        doReturn(Masked.class).when(provider).getActiveView();
        serializer.serializeWithType(value, gen, provider, typeSerializer);
        verifyNoInteractions(inner);
        verify(gen, times(1)).writeString(anyString());
    }
}
