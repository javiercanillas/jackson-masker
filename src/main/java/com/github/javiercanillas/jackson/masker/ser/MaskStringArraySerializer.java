package com.github.javiercanillas.jackson.masker.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.github.javiercanillas.jackson.masker.MaskUtils;
import com.github.javiercanillas.jackson.masker.annotation.MaskStringArray;
import com.github.javiercanillas.jackson.masker.view.Masked;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("java:S110")
public class MaskStringArraySerializer extends StringArraySerializer {

    private final int keepLastCharacters;
    private final char maskCharacter;

    public MaskStringArraySerializer() {
        this(StringArraySerializer.instance, null, false,
                MaskStringArray.DEFAULTS_KEEP_LAST_CHARACTERS, MaskStringArray.DEFAULTS_MASK_CHARACTER);
    }

    public MaskStringArraySerializer(StringArraySerializer src,
                                 BeanProperty prop, Boolean unwrapSingle, int keepLastCharacters, char maskCharacter) {
        super(src, prop, (src != null) ? src.getContentSerializer() : null, unwrapSingle);
        this.keepLastCharacters = keepLastCharacters;
        this.maskCharacter = maskCharacter;
    }

    @Override
    public void serializeContents(String[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String[] newValue;
        if (value != null && Masked.isEnabled(provider)) {
            newValue = MaskUtils.mask(value, this.keepLastCharacters, this.maskCharacter);
        } else {
            newValue = value;
        }
        super.serializeContents(newValue, gen, provider);
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
        // stolen from super createContextual
        Boolean unwrapSingle = findFormatFeature(prov, property, String[].class,
                JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

        var annotation = Optional.ofNullable(property).map(prop -> prop.getAnnotation(MaskStringArray.class));

        return new MaskStringArraySerializer((StringArraySerializer) super.createContextual(prov, property), property, unwrapSingle,
                Math.max(0, annotation.map(MaskStringArray::keepLastCharacters).orElse(MaskStringArray.DEFAULTS_KEEP_LAST_CHARACTERS)),
                annotation.map(MaskStringArray::maskCharacter).orElse(MaskStringArray.DEFAULTS_MASK_CHARACTER));
    }
}
