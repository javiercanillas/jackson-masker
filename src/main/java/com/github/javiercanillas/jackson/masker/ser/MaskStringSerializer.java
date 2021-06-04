package com.github.javiercanillas.jackson.masker.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.github.javiercanillas.jackson.masker.MaskUtils;
import com.github.javiercanillas.jackson.masker.annotation.MaskString;
import com.github.javiercanillas.jackson.masker.view.Masked;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Custom Masking Serialization for fields annotated with {@link MaskString}. It uses internally the implementation of
 * {@link StringSerializer}
 */
public class MaskStringSerializer extends StdScalarSerializer<String> implements ContextualSerializer {

    private static final long serialVersionUID = -5753691330908523181L;

    private final StringSerializer defaultSerializer;
    private final int keepLastCharacters;
    private final char maskCharacter;

    public MaskStringSerializer() {
        this(MaskString.DEFAULTS_KEEP_LAST_CHARACTERS, MaskString.DEFAULTS_MASK_CHARACTER);
    }

    public MaskStringSerializer(final int keepLastCharacters, final char maskCharacter) {
        super(String.class, false);
        defaultSerializer = new StringSerializer();
        this.keepLastCharacters = keepLastCharacters;
        this.maskCharacter = maskCharacter;
    }

    @Override
    public boolean isEmpty(final SerializerProvider prov, final String value) {
        return defaultSerializer.isEmpty(prov, value);
    }

    @Override
    public void serialize(final String value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (isEnabled(provider)) {
            gen.writeString(MaskUtils.mask(value, this.keepLastCharacters, this.maskCharacter));
        } else {
            defaultSerializer.serialize(value, gen, provider);
        }
    }

    @Override
    public void serializeWithType(final String value, final JsonGenerator gen, final SerializerProvider provider,
                                  final TypeSerializer typeSer) throws IOException {
        if (isEnabled(provider)) {
            gen.writeString(MaskUtils.mask(value, this.keepLastCharacters, this.maskCharacter));
        } else {
            defaultSerializer.serializeWithType(value, gen, provider, typeSer);
        }
    }

    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return defaultSerializer.getSchema(provider, typeHint);
    }

    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint)
            throws JsonMappingException {
        defaultSerializer.acceptJsonFormatVisitor(visitor, typeHint);
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) {
        var annotation = Optional.ofNullable(property).map(prop -> prop.getAnnotation(MaskString.class));
        return new MaskStringSerializer(Math.max(0,
                annotation.map(MaskString::keepLastCharacters).orElse(MaskString.DEFAULTS_KEEP_LAST_CHARACTERS)),
                annotation.map(MaskString::maskCharacter).orElse(MaskString.DEFAULTS_MASK_CHARACTER));
    }

    private boolean isEnabled(final SerializerProvider provider) {
        return (null != provider.getActiveView() && Masked.class.isAssignableFrom(provider.getActiveView()));
    }
}