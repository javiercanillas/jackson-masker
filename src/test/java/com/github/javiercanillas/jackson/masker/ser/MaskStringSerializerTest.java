package com.github.javiercanillas.jackson.masker.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaskStringSerializerTest {

    MaskStringSerializer maskStringSerializerNoArgs;
    MaskStringSerializer maskStringSerializerArgs;
    final int keepFirstCharacters = 0;
    final int keepLastCharacters = 0;
    final char maskCharacter = '*';

    @Mock
    JsonSerializer innerSerializer;


    @BeforeEach
    void setup() {
        this.maskStringSerializerNoArgs = new MaskStringSerializer();
        this.maskStringSerializerArgs = new MaskStringSerializer(this.innerSerializer, this.keepFirstCharacters,
                this.keepLastCharacters, this.maskCharacter);
    }
    @Test
    void unwrappingSerializer() {
        var nameTransformer = mock(NameTransformer.class);
        doReturn(innerSerializer).when(innerSerializer).unwrappingSerializer(nameTransformer);
        var serializer = this.maskStringSerializerArgs.unwrappingSerializer(nameTransformer);
        assertNotNull(serializer);
        assertEquals(MaskStringSerializer.class, serializer.getClass());
        verify(innerSerializer, times(1)).unwrappingSerializer(nameTransformer);
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void replaceDelegatee() {
        var mockedSerializerDelegatee = mock(JsonSerializer.class);
        var mockedSerializer = mock(JsonSerializer.class);
        doReturn(mockedSerializer).when(innerSerializer).replaceDelegatee(mockedSerializerDelegatee);
        var serializer = this.maskStringSerializerArgs.replaceDelegatee(mockedSerializerDelegatee);

        assertNotNull(serializer);
        assertEquals(MaskStringSerializer.class, serializer.getClass());
        final var maskStringSerializer = (MaskStringSerializer) serializer;
        assertEquals(this.maskStringSerializerArgs.getMaskCharacter(), maskStringSerializer.getMaskCharacter());
        assertEquals(this.maskStringSerializerArgs.getKeepFirstCharacters(), maskStringSerializer.getKeepFirstCharacters());
        assertEquals(this.maskStringSerializerArgs.getKeepLastCharacters(), maskStringSerializer.getKeepLastCharacters());
        assertEquals(mockedSerializer, maskStringSerializer.getNonMaskerSerializer());

        verify(innerSerializer, times(1)).replaceDelegatee(mockedSerializerDelegatee);
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void withFilterId() {
        var mockedObj = mock(Object.class);
        var mockedSerializer = mock(JsonSerializer.class);
        doReturn(mockedSerializer).when(innerSerializer).withFilterId(mockedObj);
        var serializer = this.maskStringSerializerArgs.withFilterId(mockedObj);
        assertNotNull(serializer);
        assertEquals(MaskStringSerializer.class, serializer.getClass());
        final var maskStringSerializer = (MaskStringSerializer) serializer;
        assertEquals(this.maskStringSerializerArgs.getMaskCharacter(), maskStringSerializer.getMaskCharacter());
        assertEquals(this.maskStringSerializerArgs.getKeepFirstCharacters(), maskStringSerializer.getKeepFirstCharacters());
        assertEquals(this.maskStringSerializerArgs.getKeepLastCharacters(), maskStringSerializer.getKeepLastCharacters());
        assertEquals(mockedSerializer, maskStringSerializer.getNonMaskerSerializer());

        verify(innerSerializer, times(1)).withFilterId(mockedObj);
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void serializeWithType() throws IOException {
        var value = mock(Object.class);
        var gen = mock(JsonGenerator.class);
        var serializers = mock(SerializerProvider.class);
        var typeSer = mock(TypeSerializer.class);
        this.maskStringSerializerArgs.serializeWithType(value, gen, serializers, typeSer);
        verify(innerSerializer, times(1)).serializeWithType(value, gen, serializers, typeSer);
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void isUnwrappingSerializer() {
        doReturn(true).when(innerSerializer).isUnwrappingSerializer();
        assertTrue(this.maskStringSerializerArgs.isUnwrappingSerializer());
        verify(innerSerializer, times(1)).isUnwrappingSerializer();
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void isEmpty() {
        var mockedObj = mock(Object.class);
        var provider = mock(SerializerProvider.class);
        doReturn(true).when(innerSerializer).isEmpty(provider, mockedObj);
        assertTrue(this.maskStringSerializerArgs.isEmpty(provider, mockedObj));
        verify(innerSerializer, times(1)).isEmpty(provider, mockedObj);
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void handledType() {
        doReturn(Object.class).when(innerSerializer).handledType();
        assertEquals(Object.class, this.maskStringSerializerArgs.handledType());
        verify(innerSerializer, times(1)).handledType();
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void usesObjectId() {
        doReturn(true).when(innerSerializer).usesObjectId();
        assertTrue(this.maskStringSerializerArgs.usesObjectId());
        verify(innerSerializer, times(1)).usesObjectId();
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void getDelegatee() {
        var mockedSerializer = mock(JsonSerializer.class);
        doReturn(mockedSerializer).when(innerSerializer).getDelegatee();
        final var delegatee = this.maskStringSerializerArgs.getDelegatee();
        assertNotNull(delegatee);
        assertEquals(MaskStringSerializer.class, delegatee.getClass());
        final var maskStringSerializerDelegatee = (MaskStringSerializer) delegatee;
        assertEquals(this.maskStringSerializerArgs.getMaskCharacter(), maskStringSerializerDelegatee.getMaskCharacter());
        assertEquals(this.maskStringSerializerArgs.getKeepFirstCharacters(), maskStringSerializerDelegatee.getKeepFirstCharacters());
        assertEquals(this.maskStringSerializerArgs.getKeepLastCharacters(), maskStringSerializerDelegatee.getKeepLastCharacters());
        assertEquals(mockedSerializer, maskStringSerializerDelegatee.getNonMaskerSerializer());
        verify(innerSerializer, times(1)).getDelegatee();
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void properties() {
        var properties = mock(Iterator.class);
        doReturn(properties).when(innerSerializer).properties();
        assertEquals(properties, this.maskStringSerializerArgs.properties());
        verify(innerSerializer, times(1)).properties();
        verifyNoMoreInteractions(innerSerializer);
    }

    @Test
    void acceptJsonFormatVisitor() throws JsonMappingException {
        var visitor = mock(JsonFormatVisitorWrapper.class);
        var javaType = mock(JavaType.class);
        this.maskStringSerializerArgs.acceptJsonFormatVisitor(visitor, javaType);
        verify(innerSerializer, times(1)).acceptJsonFormatVisitor(visitor, javaType);
        verifyNoMoreInteractions(innerSerializer);
    }
}