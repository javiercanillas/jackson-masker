package com.github.javiercanillas.jackson.masker.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.github.javiercanillas.jackson.masker.MaskUtils;
import com.github.javiercanillas.jackson.masker.annotation.MaskString;
import com.github.javiercanillas.jackson.masker.view.Masked;
import lombok.Getter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class MaskStringSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    @Getter
    private final JsonSerializer<Object> nonMaskerSerializer;
    @Getter
    private final int keepLastCharacters;
    @Getter
    private final char maskCharacter;

    /**
     * Jackson requires me to have a non-argument constructor but this instance seems never used directly for
     * serialization.
     */
    public MaskStringSerializer() {
        this(null, MaskString.DEFAULTS_KEEP_LAST_CHARACTERS, MaskString.DEFAULTS_MASK_CHARACTER);
    }

    public MaskStringSerializer(final JsonSerializer<Object> nonMaskerSerializer, final int keepLastCharacters, final char maskCharacter) {
        this.nonMaskerSerializer = nonMaskerSerializer;
        this.keepLastCharacters = keepLastCharacters;
        this.maskCharacter = maskCharacter;
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
        return wrappedResultOrElseNull(() -> this.nonMaskerSerializer.unwrappingSerializer(unwrapper));
    }

    @Override
    public JsonSerializer<Object> replaceDelegatee(JsonSerializer<?> delegatee) {
        return wrappedResultOrElseNull(() -> this.nonMaskerSerializer.replaceDelegatee(delegatee));
    }

    @Override
    public JsonSerializer<Object> withFilterId(Object filterId) {
        return wrappedResultOrElseNull(() -> this.nonMaskerSerializer.withFilterId(filterId));
    }

    @Override
    public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        this.nonMaskerSerializer.serialize(mask(value, serializers), gen, serializers);
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        this.nonMaskerSerializer.serializeWithType(mask(value, serializers), gen, serializers, typeSer);
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return this.nonMaskerSerializer.isUnwrappingSerializer();
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Object value) {
        return this.nonMaskerSerializer.isEmpty(provider, value);
    }

    @Override
    public Class<Object> handledType() {
        return this.nonMaskerSerializer.handledType();
    }

    @Override
    public boolean usesObjectId() {
        return this.nonMaskerSerializer.usesObjectId();
    }

    @Override
    public JsonSerializer<?> getDelegatee() {
        return wrappedResultOrElseNull(() -> this.nonMaskerSerializer.getDelegatee());
    }

    @Override
    public Iterator<PropertyWriter> properties() {
        return this.nonMaskerSerializer.properties();
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
        this.nonMaskerSerializer.acceptJsonFormatVisitor(visitor, type);
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        var annotation = Optional.ofNullable(property).map(prop -> prop.getAnnotation(MaskString.class));
        return new MaskStringSerializer(serializers.findValueSerializer(property.getType(), property),
                Math.max(0, annotation.map(MaskString::keepLastCharacters).orElse(MaskString.DEFAULTS_KEEP_LAST_CHARACTERS)),
                annotation.map(MaskString::maskCharacter).orElse(MaskString.DEFAULTS_MASK_CHARACTER));
    }

    private Object mask(Object value, SerializerProvider serializers) {
        Object newValue;
        if (Masked.isEnabled(serializers)) {
            if (value instanceof String) {
                newValue = MaskUtils.mask((String) value, this.keepLastCharacters, this.maskCharacter);
            } else if (value.getClass().isArray()) {
                newValue = MaskUtils.mask((String[]) value, this.keepLastCharacters, this.maskCharacter);
            } else if (value instanceof List) {
                newValue = MaskUtils.mask((List<String>) value, this.keepLastCharacters, this.maskCharacter);
            } else if (value instanceof Set) {
                newValue = MaskUtils.mask((Set<String>) value, this.keepLastCharacters, this.maskCharacter);
            } else {
                // ups!! value type is not supported :(
                newValue = value;
            }
        } else {
            newValue = value;
        }
        return newValue;
    }

    private JsonSerializer<Object> wrappedResultOrElseNull(final Supplier<JsonSerializer<?>> supplier) {
        return Optional.ofNullable(supplier.get())
                .map(ser -> (JsonSerializer<Object>) ser)
                .map(ser -> new MaskStringSerializer(ser, this.keepLastCharacters, this.maskCharacter))
                .orElse(null);
    }
}
