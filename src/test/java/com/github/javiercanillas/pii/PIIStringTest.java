package com.github.javiercanillas.pii;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.mockito.Mockito.*;

class PIIStringTest {

    public static class TestObject {

        private String stringValue;
        @PIIString
        private String stringPii;
        @PIIString(keepLastCharacters = 6)
        private String stringKeepLastCharactersPii;

        public String getStringValue() {
            return stringValue;
        }

        void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringPii() {
            return stringPii;
        }

        void setStringPii(String stringPii) {
            this.stringPii = stringPii;
        }

        public String getStringKeepLastCharactersPii() {
            return stringKeepLastCharactersPii;
        }

        void setStringKeepLastCharactersPii(String stringKeepLastCharactersPii) {
            this.stringKeepLastCharactersPii = stringKeepLastCharactersPii;
        }
    }

    @Test
    void objectMapper() throws JsonProcessingException {
        String stringRepresentation;
        TestObject obj;
        ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter PIIWriter = mapper.writerWithView(PIIMasked.class);
        final ObjectWriter normalWriter = mapper.writer();

        String stringValue = "aabbccdd";
        obj = new TestObject();
        obj.setStringPii(stringValue);
        obj.setStringKeepLastCharactersPii(stringValue);
        obj.setStringValue(stringValue);

        stringRepresentation = PIIWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabbccdd\",\"stringPii\":\"********\",\"stringKeepLastCharactersPii\":\"**bbccdd\"}",
                stringRepresentation);

        stringRepresentation = normalWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabbccdd\",\"stringPii\":\"aabbccdd\",\"stringKeepLastCharactersPii\":\"aabbccdd\"}",
                stringRepresentation);

        stringValue = "aabb";
        obj = new TestObject();
        obj.setStringPii(stringValue);
        obj.setStringKeepLastCharactersPii(stringValue);
        obj.setStringValue(stringValue);

        stringRepresentation = PIIWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabb\",\"stringPii\":\"****\",\"stringKeepLastCharactersPii\":\"aabb\"}",
                stringRepresentation);

        stringRepresentation = normalWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":\"aabb\",\"stringPii\":\"aabb\",\"stringKeepLastCharactersPii\":\"aabb\"}",
                stringRepresentation);

        stringValue = null;
        obj = new TestObject();
        obj.setStringPii(stringValue);
        obj.setStringKeepLastCharactersPii(stringValue);
        obj.setStringValue(stringValue);

        stringRepresentation = PIIWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":null,\"stringPii\":null,\"stringKeepLastCharactersPii\":null}",
                stringRepresentation);

        stringRepresentation = normalWriter.writeValueAsString(obj);
        Assertions.assertEquals("{\"stringValue\":null,\"stringPii\":null,\"stringKeepLastCharactersPii\":null}",
                stringRepresentation);
    }

    @Test
    void acceptJsonFormatVisitor() throws NoSuchFieldException, IllegalAccessException, JsonMappingException {
        final Field defaultSerializerField = PIIString.PIIStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        PIIString.PIIStringSerializer serializer = new PIIString.PIIStringSerializer();
        defaultSerializerField.set(serializer, inner);

        serializer.acceptJsonFormatVisitor(null, null);
        verify(inner, times(1))
                .acceptJsonFormatVisitor(nullable(JsonFormatVisitorWrapper.class), nullable(JavaType.class));
    }

    @Test
    void getSchema() throws NoSuchFieldException, IllegalAccessException {
        final Field defaultSerializerField = PIIString.PIIStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        PIIString.PIIStringSerializer serializer = new PIIString.PIIStringSerializer();
        defaultSerializerField.set(serializer, inner);

        serializer.getSchema(null, null);
        verify(inner, times(1))
                .getSchema(nullable(SerializerProvider.class), nullable(Type.class));
    }

    @Test
    void isEmpty() throws NoSuchFieldException, IllegalAccessException {
        final Field defaultSerializerField = PIIString.PIIStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        PIIString.PIIStringSerializer serializer = new PIIString.PIIStringSerializer();
        defaultSerializerField.set(serializer, inner);

        serializer.isEmpty(null, null);
        verify(inner, times(1))
                .isEmpty(nullable(SerializerProvider.class), nullable(Object.class));
    }

    @Test
    void serialize() throws NoSuchFieldException, IllegalAccessException, IOException {
        final Field defaultSerializerField = PIIString.PIIStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        PIIString.PIIStringSerializer serializer = new PIIString.PIIStringSerializer();
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
        final Field defaultSerializerField = PIIString.PIIStringSerializer.class.getDeclaredField("defaultSerializer");
        defaultSerializerField.setAccessible(true);
        StringSerializer inner = mock(StringSerializer.class);

        PIIString.PIIStringSerializer serializer = new PIIString.PIIStringSerializer();
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

        // PIIMasked view is active
        doReturn(PIIMasked.class).when(provider).getActiveView();
        serializer.serializeWithType(value, gen, provider, typeSerializer);
        verifyNoInteractions(inner);
        verify(gen, times(1)).writeString(anyString());
    }
}
