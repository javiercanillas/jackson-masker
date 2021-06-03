package com.github.javiercanillas.pii;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Annotation to be applied over String fields to be masked using the view {@link PIIMasked} when using
 * {@link com.fasterxml.jackson.databind.ObjectMapper#writerWithView}. If no view is passed or it's null, data will not
 * be masked (backward compatibility).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = PIIString.PIIStringSerializer.class)
public @interface PIIString {
    int DEFAULTS_KEEP_LAST_CHARACTERS = 0;
    char DEFAULTS_MASK_CHARACTERS = MaskUtils.DEFAULT_MASK_CHARACTER;

    /**
     * Character to be used for masking. Defaults to {@link PIIString#DEFAULTS_MASK_CHARACTERS} if none is set.
     */
    char maskCharacter() default DEFAULTS_MASK_CHARACTERS;

    /**
     * Quantity of final characters to left unmasked. Defaults to {@link PIIString#DEFAULTS_KEEP_LAST_CHARACTERS}
     * if none is set. Only a ZERO or POSITIVE values are allowed.
     */
    int keepLastCharacters() default DEFAULTS_KEEP_LAST_CHARACTERS;

    /**
     * Custom PII Serialization for fields annotated with {@link PIIString}. It uses internally the implementation of
     * {@link StringSerializer}
     */
    class PIIStringSerializer extends StdScalarSerializer<Object> implements ContextualSerializer {

        private static final long serialVersionUID = -5753691330908523181L;

        private final StringSerializer defaultSerializer;
        private final int keepLastCharacters;
        private final char maskCharacter;

        PIIStringSerializer() {
            this(DEFAULTS_KEEP_LAST_CHARACTERS, DEFAULTS_MASK_CHARACTERS);
        }

        PIIStringSerializer(final int keepLastCharacters, final char maskCharacter) {
            super(String.class, false);
            defaultSerializer = new StringSerializer();
            this.keepLastCharacters = keepLastCharacters;
            this.maskCharacter = maskCharacter;
        }

        @Override
        public boolean isEmpty(final SerializerProvider prov, final Object value) {
            return defaultSerializer.isEmpty(prov, value);
        }

        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            if (isEnabled(provider)) {
                gen.writeString(MaskUtils.mask((String) value, this.keepLastCharacters, this.maskCharacter));
            } else {
                defaultSerializer.serialize(value, gen, provider);
            }
        }

        @Override
        public void serializeWithType(final Object value, final JsonGenerator gen, final SerializerProvider provider,
                                      final TypeSerializer typeSer) throws IOException {
            if (isEnabled(provider)) {
                gen.writeString(MaskUtils.mask((String) value, this.keepLastCharacters, this.maskCharacter));
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
            var annotation = Optional.ofNullable(property).map(prop -> prop.getAnnotation(PIIString.class));
            return new PIIStringSerializer(Math.max(0,
                    annotation.map(PIIString::keepLastCharacters).orElse(DEFAULTS_KEEP_LAST_CHARACTERS)),
                    annotation.map(PIIString::maskCharacter).orElse(DEFAULTS_MASK_CHARACTERS));
        }

        private boolean isEnabled(final SerializerProvider provider) {
            return (null != provider.getActiveView() && PIIMasked.class.isAssignableFrom(provider.getActiveView()));
        }
    }
}
